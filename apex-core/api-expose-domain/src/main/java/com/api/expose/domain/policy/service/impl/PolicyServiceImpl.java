package com.api.expose.domain.policy.service.impl;

import com.api.expose.domain.policy.adapter.repository.IPolicyRepository;
import com.api.expose.domain.policy.model.aggregate.GovernancePolicyAggregate;
import com.api.expose.domain.policy.service.IPolicyService;
import com.api.expose.domain.policy.strategy.IAccessControlStrategy;
import com.api.expose.domain.policy.strategy.ICircuitBreakerStrategy;
import com.api.expose.domain.policy.strategy.IRateLimitStrategy;
import com.api.expose.types.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.api.expose.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 策略执行服务实现
 */
@Service
public class PolicyServiceImpl implements IPolicyService {

    @Resource
    private IPolicyRepository policyRepository;

    @Resource
    private IRateLimitStrategy rateLimitStrategy;

    @Resource
    private ICircuitBreakerStrategy circuitBreakerStrategy;

    @Resource
    private IAccessControlStrategy accessControlStrategy;

    @Override
    public void checkGovernance(Long appId, String apiKey, Long assetId, String clientIp) {
        // 获取 API 和 App 相关的所有生效策略
        List<GovernancePolicyAggregate> policies = new ArrayList<>();
        if (assetId != null) {
            policies.addAll(policyRepository.queryPoliciesByApiAssetId(assetId));
        }
        if (appId != null) {
            policies.addAll(policyRepository.queryPoliciesByAppId(appId));
        }

        // 过滤仅仅启用的策略
        List<GovernancePolicyAggregate> activePolicies = policies.stream()
                .filter(p -> Boolean.TRUE.equals(p.getEnabled()))
                .collect(Collectors.toList());

        for (GovernancePolicyAggregate policy : activePolicies) {
            String targetKey = String.format("impl:%d:target:%s", policy.getPolicyId(), apiKey);

            // 1. 访问控制过滤
            if (!accessControlStrategy.allowAccess(targetKey, clientIp, apiKey, policy.getAccessControlJson())) {
                throw exception(ErrorCodeConstants.POLICY_ACCESS_DENIED);
            }

            // 2. 限流校验
            if (!rateLimitStrategy.checkRateLimit(targetKey, policy.getRateLimitJson())) {
                throw exception(ErrorCodeConstants.POLICY_RATE_LIMITED);
            }

            // 3. 熔断校验
            if (!circuitBreakerStrategy.checkCircuitBreaker(targetKey, policy.getCircuitBreakerJson())) {
                throw exception(ErrorCodeConstants.POLICY_CIRCUIT_BROKEN);
            }
        }
    }
}

