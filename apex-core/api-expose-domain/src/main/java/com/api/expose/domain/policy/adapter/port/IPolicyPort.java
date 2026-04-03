package com.api.expose.domain.policy.adapter.port;

/**
 * 策略执行端口
 */
public interface IPolicyPort {

    /**
     * 判断是否允许通过 (基于 Redis 限流)
     * @param key 唯一标识 (app:api:path)
     * @param limit 限制数
     * @param seconds 窗口时长
     * @return true-允许, false-超出
     */
    boolean allow(String key, int limit, int seconds);
}
