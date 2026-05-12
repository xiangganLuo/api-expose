package com.api.expose.domain.policy.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
            // 解析 ruleJson 获取限流配置
            JSONObject rule = JSON.parseObject(ruleJson);
            int limit = rule.getIntValue("qps");
            if (limit <= 0) {
                limit = 10; // 默认值
            }
            
            long now = System.currentTimeMillis() / 1000;
            String timeKey = targetKey + ":" + now;
            
            timeWindow.putIfAbsent(timeKey, now);
            AtomicInteger count = localCounter.computeIfAbsent(timeKey, k -> new AtomicInteger(0));
            
            // 清理过期数据（避免内存泄漏）
            cleanExpiredKeys(targetKey, now);
            
            boolean allowed = count.incrementAndGet() <= limit;
            if (!allowed) {
                log.debug("限流触发: key={}, 当前计数={}, 限制={}", targetKey, count.get(), limit);
            }
            return allowed;
        } catch (Exception e) {
            log.error("限流策略执行异常，默认放行", e);
            return true; // 异常降级放行
        }
    }
    
    /**
     * 清理过期的计数器，避免内存泄漏
     */
    private void cleanExpiredKeys(String targetKey, long currentTime) {
        localCounter.keySet().removeIf(key -> {
            if (!key.startsWith(targetKey)) {
                return false;
            }
            // 提取时间戳部分
            String[] parts = key.split(":");
            if (parts.length < 2) {
                return true; // 格式错误，删除
            }
            try {
                long keyTime = Long.parseLong(parts[parts.length - 1]);
                return currentTime - keyTime > 60; // 超过60秒的清理
            } catch (NumberFormatException e) {
                return true; // 解析失败，删除
            }
        });
    }
}
