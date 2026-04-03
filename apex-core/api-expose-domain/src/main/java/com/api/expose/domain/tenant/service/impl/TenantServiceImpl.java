package com.api.expose.domain.tenant.service.impl;

import com.api.expose.domain.tenant.adapter.repository.ITenantRepository;
import com.api.expose.domain.tenant.model.aggregate.TenantAggregate;
import com.api.expose.domain.tenant.model.entity.TenantMemberEntity;
import com.api.expose.domain.tenant.model.valobj.MemberRoleEnum;
import com.api.expose.domain.tenant.model.valobj.TenantStatusEnum;
import com.api.expose.domain.tenant.service.ITenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * 租户管理服务实现
 */
@Slf4j
@Service
public class TenantServiceImpl implements ITenantService {

    @Resource
    private ITenantRepository tenantRepository;

    @Override
    public TenantAggregate createTenant(String tenantName, String adminUserId, String adminEmail) {
        log.info("创建租户, tenantName:{}, adminUserId:{}", tenantName, adminUserId);

        TenantMemberEntity admin = TenantMemberEntity.builder()
                .userId(adminUserId)
                .email(adminEmail)
                .role(MemberRoleEnum.ADMIN)
                .joinTime(new Date())
                .build();

        ArrayList<TenantMemberEntity> members = new ArrayList<>();
        members.add(admin);

        TenantAggregate tenant = TenantAggregate.builder()
                .tenantId(UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .tenantName(tenantName)
                .status(TenantStatusEnum.ACTIVE)
                .members(members)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        tenantRepository.saveTenant(tenant);
        return tenant;
    }

    @Override
    public void addMember(String tenantId, TenantMemberEntity member) {
        log.info("添加租户成员, tenantId:{}, userId:{}", tenantId, member.getUserId());
        TenantAggregate tenant = tenantRepository.queryTenantById(tenantId);
        tenant.addMember(member);
        tenantRepository.updateTenant(tenant);
    }

    @Override
    public void removeMember(String tenantId, String userId) {
        log.info("移除租户成员, tenantId:{}, userId:{}", tenantId, userId);
        TenantAggregate tenant = tenantRepository.queryTenantById(tenantId);
        tenant.removeMember(userId);
        tenantRepository.updateTenant(tenant);
    }

    @Override
    public void suspendTenant(String tenantId) {
        log.info("停用租户, tenantId:{}", tenantId);
        TenantAggregate tenant = tenantRepository.queryTenantById(tenantId);
        tenant.suspend();
        tenantRepository.updateTenant(tenant);
    }

    @Override
    public void activateTenant(String tenantId) {
        log.info("激活租户, tenantId:{}", tenantId);
        TenantAggregate tenant = tenantRepository.queryTenantById(tenantId);
        tenant.activate();
        tenantRepository.updateTenant(tenant);
    }
}
