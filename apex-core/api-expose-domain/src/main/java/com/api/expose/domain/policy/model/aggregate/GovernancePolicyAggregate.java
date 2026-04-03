package com.api.expose.domain.policy.model.aggregate;

import com.api.expose.domain.policy.model.entity.AccessControlRuleEntity;
import com.api.expose.domain.policy.model.entity.CircuitBreakerRuleEntity;
import com.api.expose.domain.policy.model.entity.RateLimitRuleEntity;
import com.api.expose.domain.policy.model.valobj.PolicyScopeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 治理策略聚合根 - 管理 API/应用的访问控制、限流和熔断策略
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GovernancePolicyAggregate {

    /** 策略ID */
    private Long policyId;
    /** 策略名称 */
    private String policyName;
    /** 生效范围 */
    private PolicyScopeEnum scope;
    /** 关联的 API 资产ID（API_LEVEL 时使用） */
    private Long apiAssetId;
    /** 关联的应用ID（APP_LEVEL 时使用） */
    private Long appId;
    /** 限流规则 */
    private RateLimitRuleEntity rateLimitRule;
    /** 熔断规则 */
    private CircuitBreakerRuleEntity circuitBreakerRule;
    /** 访问控制规则 */
    private AccessControlRuleEntity accessControlRule;
    /** 是否启用 */
    private Boolean enabled;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

    /**
     * 绑定到 API
     */
    public void attachToApi(Long apiAssetId) {
        this.scope = PolicyScopeEnum.API_LEVEL;
        this.apiAssetId = apiAssetId;
        this.appId = null;
        this.updateTime = new Date();
    }

    /**
     * 绑定到应用
     */
    public void attachToApp(Long appId) {
        this.scope = PolicyScopeEnum.APP_LEVEL;
        this.appId = appId;
        this.apiAssetId = null;
        this.updateTime = new Date();
    }

    /**
     * 启用策略
     */
    public void enable() {
        this.enabled = true;
        this.updateTime = new Date();
    }

    /**
     * 禁用策略
     */
    public void disable() {
        this.enabled = false;
        this.updateTime = new Date();
    }
}
