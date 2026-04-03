package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.app.adapter.repository.IAppRepository;
import com.api.expose.domain.app.model.aggregate.DeveloperAppAggregate;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * @author xiangganluo
 */
@Repository
public class AppRepository implements IAppRepository {
    @Override
    public void saveApp(DeveloperAppEntity appEntity) {
        throw new UnsupportedOperationException("Core 模块已移除 App 持久化实现，请在新模块提供实现。");
    }

    @Override
    public List<DeveloperAppEntity> queryAppsByTenantId(String tenantId) {
        throw new UnsupportedOperationException("Core 模块已移除 App 持久化实现，请在新模块提供实现。");
    }

    @Override
    public void saveSubscription(SubscriptionEntity subscriptionEntity) {
        throw new UnsupportedOperationException("Core 模块已移除 Subscription 持久化实现，请在新模块提供实现。");
    }

    @Override
    public void updateSubscriptionStatus(Long subscriptionId, SubscriptionStatusEnum status, String remark) {
        throw new UnsupportedOperationException("Core 模块已移除 Subscription 持久化实现，请在新模块提供实现。");
    }

    @Override
    public List<SubscriptionEntity> querySubscriptionsByAppId(Long appId) {
        throw new UnsupportedOperationException("Core 模块已移除 Subscription 持久化实现，请在新模块提供实现。");
    }

    @Override
    public DeveloperAppAggregate queryAppAggregate(Long appId) {
        throw new UnsupportedOperationException("Core 模块已移除 App 持久化实现，请在新模块提供实现。");
    }

    @Override
    public DeveloperAppEntity queryAppByApiKey(String apiKey) {
        throw new UnsupportedOperationException("Core 模块已移除 App 持久化实现，请在新模块提供实现。");
    }

    @Override
    public DeveloperAppAggregate queryAppAggregateBySubscriptionId(Long subscriptionId) {
        throw new UnsupportedOperationException("Core 模块已移除 Subscription 持久化实现，请在新模块提供实现。");
    }
}
