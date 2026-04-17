package com.api.expose.domain.policy.strategy.impl;

import com.api.expose.domain.policy.strategy.ICircuitBreakerStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认熔断降级策略实现
 */
@Slf4j
@Component
public class DefaultCircuitBreakerStrategy implements ICircuitBreakerStrategy {

    @Override
    public boolean checkCircuitBreaker(String targetKey, String ruleJson) {
        if (ruleJson == null || ruleJson.trim().isEmpty()) {
            return true;
        }
        // TODO: 可在此处桥接 Sentinel 的 SphU.entry / DegradeRuleManager 或者是 Hystrix 
        // 作为示范，这里默认返回通过
        log.debug("执行熔断检查: {}, 规则: {}", targetKey, ruleJson);
        return true; 
    }
}
