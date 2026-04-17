package com.api.expose.domain.policy.strategy.impl;

import com.api.expose.domain.policy.strategy.IAccessControlStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认访问控制策略实现 (黑白名单过滤等)
 */
@Slf4j
@Component
public class DefaultAccessControlStrategy implements IAccessControlStrategy {

    @Override
    public boolean allowAccess(String targetKey, String clientIp, String apiKey, String ruleJson) {
        if (ruleJson == null || ruleJson.trim().isEmpty()) {
            return true;
        }
        
        // 解析 ruleJson 判断 IP 是否在黑/白名单中 
        // 比如 ruleJson = {"blacklist": ["192.168.1.100"]}
        // 这里提供默认放行的扩展基础结构
        log.debug("执行访问控制过滤: 客户端IP: {}, 规则: {}", clientIp, ruleJson);
        return true;
    }
}
