package com.api.expose.domain.app.service.impl;

import com.api.expose.domain.app.adapter.repository.IAppRepository;
import com.api.expose.domain.app.model.aggregate.DeveloperAppAggregate;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import com.api.expose.domain.app.service.IAppService;
import com.api.expose.domain.gateway.adapter.port.IGatewaySyncPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    public void createApp(DeveloperAppEntity appEntity) {
        log.info("正在创建应用: {}, 租户: {}", appEntity.getAppName(), appEntity.getTenantId());
        
        // 1. 生成 API Key and Secret (模拟逻辑)
        String apiKey = "ak_" + RandomStringUtils.randomAlphanumeric(16).toLowerCase();
        String apiSecret = "sk_" + RandomStringUtils.randomAlphanumeric(32).toLowerCase();

        DeveloperAppEntity finalEntity = DeveloperAppEntity.builder()
                .tenantId(appEntity.getTenantId())
                .appName(appEntity.getAppName())
                .description(appEntity.getDescription())
                .status(appEntity.getStatus())
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callbackUrl(appEntity.getCallbackUrl())
                .build();

        appRepository.saveApp(finalEntity);
    }

    @Override
    public List<DeveloperAppEntity> queryApps(String tenantId) {
        return appRepository.queryAppsByTenantId(tenantId);
    }

    @Override
    public void applySubscription(SubscriptionEntity subscriptionEntity) {
        log.info("正在申请订阅: appId={}, apiAssetId={}", subscriptionEntity.getAppId(), subscriptionEntity.getApiAssetId());
        
        SubscriptionEntity entity = SubscriptionEntity.builder()
                .appId(subscriptionEntity.getAppId())
                .apiAssetId(subscriptionEntity.getApiAssetId())
                .status(SubscriptionStatusEnum.PENDING)
                .remark(subscriptionEntity.getRemark())
                .build();

        appRepository.saveSubscription(entity);
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
    public DeveloperAppAggregate queryAppDetail(Long appId) {
        return appRepository.queryAppAggregate(appId);
    }
}
