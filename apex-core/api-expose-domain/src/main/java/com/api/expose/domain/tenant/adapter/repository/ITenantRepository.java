package com.api.expose.domain.tenant.adapter.repository;

import com.api.expose.domain.tenant.model.aggregate.TenantAggregate;

/**
 * 租户仓储接口
 */
public interface ITenantRepository {

    /**
     * 保存租户
     */
    void saveTenant(TenantAggregate aggregate);

    /**
     * 根据租户ID查询
     */
    TenantAggregate queryTenantById(String tenantId);

    /**
     * 根据自定义域名查询租户
     */
    TenantAggregate queryTenantByDomain(String domain);

    /**
     * 更新租户信息
     */
    void updateTenant(TenantAggregate aggregate);
}
