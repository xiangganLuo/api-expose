package com.api.expose.domain.policy.service;

/**
 * 策略执行服务 (限流, 熔断, 黑名单 etc.)
 */
public interface IPolicyService {

    /**
     * 校验资产调用策略
     * @param apiKey 应用密钥
     * @param assetId 资产ID
     * @return true-通过, false-拦截
     */
    boolean checkRateLimit(String apiKey, Long assetId);

}
