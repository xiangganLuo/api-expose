package com.api.expose.infrastructure.adapter.port;

import com.api.expose.domain.policy.adapter.port.IPolicyPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 策略执行适配器 (Redis 实现) todo
 */
@Slf4j
@Service
public class RedisPolicyAdapter implements IPolicyPort {

    @Override
    public boolean allow(String key, int limit, int seconds) {
        return true;
    }
}
