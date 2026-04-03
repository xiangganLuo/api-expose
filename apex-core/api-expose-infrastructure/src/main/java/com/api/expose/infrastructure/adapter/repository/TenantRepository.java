package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.tenant.adapter.repository.ITenantRepository;
import com.api.expose.domain.tenant.model.aggregate.TenantAggregate;
import org.springframework.stereotype.Repository;

/*
 * @author xiangganluo
 */
@Repository
public class TenantRepository implements ITenantRepository {

    @Override
    public void saveTenant(TenantAggregate aggregate) {
        throw new UnsupportedOperationException("Core 模块已移除 Tenant 持久化实现，请在新模块提供实现。");
    }

    @Override
    public TenantAggregate queryTenantById(String tenantId) {
        throw new UnsupportedOperationException("Core 模块已移除 Tenant 持久化实现，请在新模块提供实现。");
    }

    @Override
    public TenantAggregate queryTenantByDomain(String domain) {
        throw new UnsupportedOperationException("Core 模块已移除 Tenant 持久化实现，请在新模块提供实现。");
    }

    @Override
    public void updateTenant(TenantAggregate aggregate) {
        throw new UnsupportedOperationException("Core 模块已移除 Tenant 持久化实现，请在新模块提供实现。");
    }
}
