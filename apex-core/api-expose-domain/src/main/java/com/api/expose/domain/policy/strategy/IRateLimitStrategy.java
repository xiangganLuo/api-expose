package com.api.expose.domain.policy.strategy;

/**
 * 限流策略扩展点
 */
public interface IRateLimitStrategy {
    
    /**
     * 校验资源是否触发限流
     * @param targetKey 目标限流标识 (如 apiAssetId 或 apiKey 组合组合标识)
     * @param ruleJson 限流规则实体(JSON格式，比如 QPS=10)
     * @return true-放行, false-被限流拦截
     */
    boolean checkRateLimit(String targetKey, String ruleJson);

}
