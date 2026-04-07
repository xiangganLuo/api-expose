package com.api.expose.domain.tenant.service;

import com.api.expose.domain.tenant.model.aggregate.TenantAggregate;
import com.api.expose.domain.tenant.model.entity.TenantMemberEntity;

/**
 * 租户管理服务接口
 */
public interface ITenantService {

    /**
     * 创建租户
     */
    TenantAggregate createTenant(String tenantName, String adminUserId, String adminEmail);

    /**
     * 添加租户成员
     */
    void addMember(String tenantId, TenantMemberEntity member);

    /**
     * 移除租户成员
     */
    void removeMember(String tenantId, String userId);

    /**
     * 停用租户
     */
    void suspendTenant(String tenantId);

    /**
     * 激活租户
     */
    void activateTenant(String tenantId);
}
