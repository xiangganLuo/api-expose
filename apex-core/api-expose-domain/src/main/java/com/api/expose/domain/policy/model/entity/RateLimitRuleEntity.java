package com.api.expose.domain.policy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 限流规则实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateLimitRuleEntity {

    /** 规则ID */
    private Long ruleId;
    /** QPS 阈值 */
    private Integer qpsThreshold;
    /** 令牌桶大小 */
    private Integer bucketSize;
    /** 每秒填充速率 */
    private Integer refillRate;
    /** 自定义降级响应内容 */
    private String fallbackResponse;
}
