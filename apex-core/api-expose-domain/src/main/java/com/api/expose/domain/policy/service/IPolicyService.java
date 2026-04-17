package com.api.expose.domain.policy.service;

/**
 * 策略执行服务 (限流, 熔断, 黑名单 etc.)
 */
public interface IPolicyService {

    /**
     * 执行综合治理策略检查 (黑名单、限流、熔断等)
     * @param appId 应用ID
     * @param apiKey 应用密钥
     * @param assetId 资产ID
     * @param clientIp 客户端请求IP
     * @throws RuntimeException 当某项策略未通过时抛出具体的异常信息
     */
    void checkGovernance(Long appId, String apiKey, Long assetId, String clientIp);

}
