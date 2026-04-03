package com.api.expose.domain.policy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 熔断规则实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CircuitBreakerRuleEntity {

    /** 规则ID */
    private Long ruleId;
    /** 错误率阈值（百分比） */
    private Integer errorRateThreshold;
    /** 熔断恢复窗口(秒) */
    private Integer recoveryWindowSeconds;
    /** 半开状态允许的请求数 */
    private Integer halfOpenRequests;
    /** 最小请求数（触发统计的最小调用次数） */
    private Integer minimumRequests;
}
