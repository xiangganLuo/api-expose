package com.api.expose.domain.policy.strategy.impl;

import com.api.expose.domain.policy.strategy.IRateLimitStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认限流策略实现 (本实现作为兜底适配器，可替换为 Sentinel/RedisLua 等高级网关组件)
 */
@Slf4j
@Component
public class DefaultRateLimitStrategy implements IRateLimitStrategy {

    // 简单本地计数器模拟 (生产环境应换为 RedisLua 或 Sentinel)
    private final ConcurrentHashMap<String, AtomicInteger> localCounter = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> timeWindow = new ConcurrentHashMap<>();

    @Override
    public boolean checkRateLimit(String targetKey, String ruleJson) {
        if (ruleJson == null || ruleJson.trim().isEmpty()) {
            return true; // 没有规则直接放行
        }
        
        try {
            // 假设 ruleJson 中包含限流阈值如 {"qps": 10}
            // 这里为了最简实现默认按10处理，实际可引入 JSON 解析解析 ruleJson 取值
            int limit = 10; 
            
            long now = System.currentTimeMillis() / 1000;
            String timeKey = targetKey + ":" + now;
            
            timeWindow.putIfAbsent(timeKey, now);
            AtomicInteger count = localCounter.computeIfAbsent(timeKey, k -> new AtomicInteger(0));
            
            // 清理过期数据
            localCounter.keySet().removeIf(k -> k.startsWith(targetKey) && !k.equals(timeKey));
            
            return count.incrementAndGet() <= limit;
        } catch (Exception e) {
            log.error("限流策略执行异常", e);
            return true; // 异常降级放行
        }
    }
}
