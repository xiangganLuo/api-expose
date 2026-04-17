package com.api.expose.domain.policy.strategy;

/**
 * 熔断策略扩展点
 */
public interface ICircuitBreakerStrategy {
    
    /**
     * 校验资源是否出于熔断状态需要被降级
     * @param targetKey 目标限流标识 (如 apiAssetId)
     * @param ruleJson 熔断规则实体(JSON格式，比如超时比例或异常比例)
     * @return true-状态正常放行, false-已熔断请求拦截
     */
    boolean checkCircuitBreaker(String targetKey, String ruleJson);

}
