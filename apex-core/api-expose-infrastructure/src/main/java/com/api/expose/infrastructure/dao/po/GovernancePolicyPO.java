package com.api.expose.infrastructure.dao.po;

import com.api.expose.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * @author xiangganluo
 */

/**
 * 治理策略持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("governance_policy")
@EqualsAndHashCode(callSuper = true)
public class GovernancePolicyPO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String policyName;

    private String scope;

    private Long apiAssetId;

    private Long appId;

    private Boolean enabled;

    private String rateLimitJson;

    private String circuitBreakerJson;

    private String accessControlJson;
}
