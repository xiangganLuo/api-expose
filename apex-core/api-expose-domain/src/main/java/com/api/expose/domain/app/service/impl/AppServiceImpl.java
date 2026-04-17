package com.api.expose.domain.app.service.impl;

import com.api.expose.domain.app.adapter.repository.IAppRepository;
import com.api.expose.domain.app.model.aggregate.DeveloperAppAggregate;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import com.api.expose.domain.app.service.IAppService;
import com.api.expose.domain.gateway.adapter.port.IGatewaySyncPort;
import com.api.expose.framework.common.pojo.PageParam;
import com.api.expose.framework.common.pojo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.api.expose.framework.tenant.core.context.TenantContextHolder.getTenantId;

/**
 * 开发者应用管理服务实现
 */
@Slf4j
@Service
public class AppServiceImpl implements IAppService {

    @Resource
    private IAppRepository appRepository;

    @Resource
    private IGatewaySyncPort gatewaySyncPort;

    @Override
    public DeveloperAppEntity createApp(DeveloperAppEntity appEntity) {
        Long tenantId = getTenantId();
        log.info("正在创建应用: {}, 租户: {}", appEntity.getAppName(), tenantId);
        
        // 1. 生成 API Key and Secret (模拟逻辑)
        String apiKey = "ak_" + RandomStringUtils.randomAlphanumeric(16).toLowerCase();
        String apiSecret = "sk_" + RandomStringUtils.randomAlphanumeric(32).toLowerCase();

        DeveloperAppEntity finalEntity = DeveloperAppEntity.builder()
                .tenantId(tenantId)
                .appName(appEntity.getAppName())
                .description(appEntity.getDescription())
                .status(appEntity.getStatus() != null ? appEntity.getStatus() : com.api.expose.domain.app.model.valobj.AppStatusEnum.ACTIVE)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callbackUrl(appEntity.getCallbackUrl())
                .build();

        appRepository.saveApp(finalEntity);
        return finalEntity;
    }

    @Override
    public List<DeveloperAppEntity> queryApps() {
        return appRepository.queryApps();
    }

    @Override
    public PageResult<DeveloperAppEntity> pageApps(String keywords, String status,
                                                                                        com.api.expose.framework.common.pojo.PageParam pageParam) {
        return appRepository.pageApps(keywords, status, pageParam);
    }

    @Override
    public void updateApp(DeveloperAppEntity appEntity) {
        appRepository.updateApp(appEntity);
    }

    @Override
    public void deleteApp(Long appId) {
        appRepository.deleteApp(appId);
    }

    @Override
    public Long applySubscription(SubscriptionEntity subscriptionEntity) {
        log.info("正在申请订阅: appId={}, apiAssetId={}", subscriptionEntity.getAppId(), subscriptionEntity.getApiAssetId());

        Long tenantId = getTenantId();
        
        SubscriptionEntity entity = SubscriptionEntity.builder()
                .tenantId(tenantId)
                .appId(subscriptionEntity.getAppId())
                .apiAssetId(subscriptionEntity.getApiAssetId())
                .status(SubscriptionStatusEnum.PENDING)
                .remark(subscriptionEntity.getRemark())
                .build();

        return appRepository.saveSubscription(entity);
    }

    @Override
    public void auditSubscription(Long subscriptionId, SubscriptionStatusEnum status, String remark) {
        log.info("正在审核订阅: id={}, 结果: {}", subscriptionId, status.getCode());
        appRepository.updateSubscriptionStatus(subscriptionId, status, remark);
        
        // 如果是已通过，同步到网关
        if (SubscriptionStatusEnum.APPROVED.equals(status)) {
            DeveloperAppAggregate aggregate = appRepository.queryAppAggregateBySubscriptionId(subscriptionId);
            if (aggregate != null) {
                Long apiAssetId = aggregate.getSubscriptions().stream()
                        .filter(s -> s.getId().equals(subscriptionId))
                        .map(SubscriptionEntity::getApiAssetId)
                        .findFirst().orElse(null);
                
                gatewaySyncPort.syncSubscription(aggregate.getApp().getAppId(), apiAssetId, aggregate.getApp().getApiKey());
            }
        }
    }

    @Override
    public PageResult<SubscriptionEntity> getSubscriptionPage(Long appId, Long apiAssetId, String status, PageParam pageParam) {
        return appRepository.getSubscriptionPage(appId, apiAssetId, status, pageParam);
    }

    @Override
    public DeveloperAppAggregate queryAppDetail(Long appId) {
        return appRepository.queryAppAggregate(appId);
    }
}
