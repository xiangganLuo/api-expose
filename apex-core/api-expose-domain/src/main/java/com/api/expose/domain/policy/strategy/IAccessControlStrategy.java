package com.api.expose.domain.policy.strategy;

/**
 * 访问控制（黑白名单等）策略扩展点
 */
public interface IAccessControlStrategy {
    
    /**
     * 校验请求是否允许访问
     * @param targetKey 目标标识 (如 apiAssetId)
     * @param clientIp 客户端来源 IP
     * @param apiKey 正在调用的 AppKey
     * @param ruleJson 访问控制配置实体(JSON格式)
     * @return true-允许, false-拦截
     */
    boolean allowAccess(String targetKey, String clientIp, String apiKey, String ruleJson);

}
