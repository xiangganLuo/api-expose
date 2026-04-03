package com.api.expose.domain.tenant.model.aggregate;

import com.api.expose.domain.tenant.model.entity.TenantMemberEntity;
import com.api.expose.domain.tenant.model.valobj.MemberRoleEnum;
import com.api.expose.domain.tenant.model.valobj.TenantStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 租户聚合根 - 管理租户信息、成员和隔离配置
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantAggregate {

    /** 租户ID */
    private String tenantId;
    /** 租户名称 */
    private String tenantName;
    /** 自定义域名 */
    private String customDomain;
    /** Logo URL */
    private String logoUrl;
    /** 租户状态 */
    private TenantStatusEnum status;
    /** 成员列表 */
    private List<TenantMemberEntity> members;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

    /**
     * 添加成员
     */
    public void addMember(TenantMemberEntity member) {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        // 检查重复
        boolean exists = this.members.stream()
                .anyMatch(m -> m.getUserId().equals(member.getUserId()));
        if (exists) {
            throw new IllegalStateException("该用户已是租户成员");
        }
        this.members.add(member);
    }

    /**
     * 移除成员
     */
    public void removeMember(String userId) {
        if (this.members == null) {
            return;
        }
        // 不能移除管理员
        this.members.stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .ifPresent(m -> {
                    if (MemberRoleEnum.ADMIN.equals(m.getRole())) {
                        throw new IllegalStateException("不能移除管理员");
                    }
                });
        this.members.removeIf(m -> m.getUserId().equals(userId));
    }

    /**
     * 停用租户
     */
    public void suspend() {
        this.status = TenantStatusEnum.SUSPENDED;
        this.updateTime = new Date();
    }

    /**
     * 激活租户
     */
    public void activate() {
        this.status = TenantStatusEnum.ACTIVE;
        this.updateTime = new Date();
    }
}
