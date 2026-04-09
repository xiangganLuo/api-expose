package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.app.adapter.repository.IAppRepository;
import com.api.expose.domain.app.model.aggregate.DeveloperAppAggregate;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import com.api.expose.framework.common.pojo.PageParam;
import cn.hutool.core.util.StrUtil;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.infrastructure.dao.IDeveloperAppDao;
import com.api.expose.infrastructure.dao.ISubscriptionDao;
import com.api.expose.infrastructure.dao.po.DeveloperAppPO;
import com.api.expose.infrastructure.dao.po.SubscriptionPO;
import com.api.expose.infrastructure.convert.app.AppConvert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/*
 * @author xiangganluo
 */
@Repository
public class AppRepository implements IAppRepository {

    @Resource
    private IDeveloperAppDao developerAppDao;

    @Resource
    private ISubscriptionDao subscriptionDao;

    @Override
    public void saveApp(DeveloperAppEntity appEntity) {
        if (appEntity == null) return;

        String status = appEntity.getStatus() != null ? appEntity.getStatus().getCode() : com.api.expose.domain.app.model.valobj.AppStatusEnum.ACTIVE.getCode();

        DeveloperAppPO po = AppConvert.INSTANCE.convert(appEntity);
        po.setStatus(status);

        if (appEntity.getAppId() == null) {
            developerAppDao.insert(po);
        } else {
            developerAppDao.updateById(po);
        }
    }

    

    @Override
    public List<DeveloperAppEntity> queryApps() {
        LambdaQueryWrapper<DeveloperAppPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DeveloperAppPO::getCreateTime);
        return developerAppDao.selectList(wrapper).stream().map(AppConvert.INSTANCE::convert).collect(Collectors.toList());
    }

    @Override
    public PageResult<DeveloperAppEntity> pageApps(String keywords, String status, PageParam pageParam) {
        LambdaQueryWrapper<DeveloperAppPO> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keywords)) {
            wrapper.like(DeveloperAppPO::getAppName, keywords);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(DeveloperAppPO::getStatus, status);
        }
        wrapper.orderByDesc(DeveloperAppPO::getCreateTime);
        Page<DeveloperAppPO> page = developerAppDao.selectPage(new Page<>(pageParam.getPageNo(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords().stream().map(AppConvert.INSTANCE::convert).collect(Collectors.toList()), page.getTotal());
    }

    @Override
    public void updateApp(DeveloperAppEntity appEntity) {
        if (appEntity == null || appEntity.getAppId() == null) return;
        DeveloperAppPO po = AppConvert.INSTANCE.convert(appEntity);
        developerAppDao.updateById(po);
    }

    @Override
    public void deleteApp(Long appId) {
        if (appId == null) return;
        developerAppDao.deleteById(appId);
    }

    @Override
    public Long saveSubscription(SubscriptionEntity subscriptionEntity) {
        if (subscriptionEntity == null) {
            return null;
        }

        String status = subscriptionEntity.getStatus() != null ? subscriptionEntity.getStatus().getCode() : SubscriptionStatusEnum.PENDING.getCode();

        SubscriptionPO po = AppConvert.INSTANCE.convert(subscriptionEntity);
        po = AppConvert.INSTANCE.convertTimes(subscriptionEntity, po);
        po.setStatus(status);
        if (po.getApplyTime() == null) {
            po.setApplyTime(java.time.LocalDateTime.now());
        }

        if (subscriptionEntity.getId() == null) {
            subscriptionDao.insert(po);
            return po.getId();
        }
        subscriptionDao.updateById(po);
        return subscriptionEntity.getId();
    }

    @Override
    public void updateSubscriptionStatus(Long subscriptionId, SubscriptionStatusEnum status, String remark) {
        if (subscriptionId == null || status == null) return;

        SubscriptionPO po = SubscriptionPO.builder()
                .id(subscriptionId)
                .status(status.getCode())
                .remark(remark)
                .approveTime(SubscriptionStatusEnum.APPROVED.equals(status) ? java.time.LocalDateTime.now() : null)
                .build();
        subscriptionDao.updateById(po);
    }

    @Override
    public PageResult<SubscriptionEntity> getSubscriptionPage(Long appId, Long apiAssetId, String status, PageParam pageParam) {
        LambdaQueryWrapper<SubscriptionPO> wrapper = new LambdaQueryWrapper<>();
        if (appId != null) {
            wrapper.eq(SubscriptionPO::getAppId, appId);
        }
        if (apiAssetId != null) {
            wrapper.eq(SubscriptionPO::getApiAssetId, apiAssetId);
        }
        if (status != null && !StringUtils.hasText(status)) {
            wrapper.eq(SubscriptionPO::getStatus, status);
        }
        wrapper.orderByDesc(SubscriptionPO::getCreateTime);

        Page<SubscriptionPO> page = subscriptionDao.selectPage(
                new Page<>(pageParam.getPageNo(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords().stream().map(AppConvert.INSTANCE::convert).collect(Collectors.toList()), page.getTotal());
    }

    @Override
    public List<SubscriptionEntity> querySubscriptionsByAppId(Long appId) {
        LambdaQueryWrapper<SubscriptionPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SubscriptionPO::getAppId, appId);
        wrapper.orderByDesc(SubscriptionPO::getCreateTime);
        return subscriptionDao.selectList(wrapper).stream().map(AppConvert.INSTANCE::convert).collect(Collectors.toList());
    }

    @Override
    public DeveloperAppAggregate queryAppAggregate(Long appId) {
        DeveloperAppPO appPO = developerAppDao.selectById(appId);
        if (appPO == null) return null;

        List<SubscriptionEntity> subscriptions = querySubscriptionsByAppId(appId);
        return DeveloperAppAggregate.builder()
                .app(AppConvert.INSTANCE.convert(appPO))
                .subscriptions(subscriptions)
                .build();
    }

    @Override
    public DeveloperAppEntity queryAppByApiKey(String apiKey) {
        LambdaQueryWrapper<DeveloperAppPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeveloperAppPO::getApiKey, apiKey);
        DeveloperAppPO po = developerAppDao.selectOne(wrapper);
        return po == null ? null : AppConvert.INSTANCE.convert(po);
    }

    @Override
    public DeveloperAppAggregate queryAppAggregateBySubscriptionId(Long subscriptionId) {
        SubscriptionPO subscriptionPO = subscriptionDao.selectById(subscriptionId);
        if (subscriptionPO == null) return null;
        return queryAppAggregate(subscriptionPO.getAppId());
    }


}
