package com.api.expose.infrastructure.adapter.port;

import com.api.expose.domain.policy.adapter.port.IPolicyPort;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * 策略执行适配器 (Redis 实现)
 */
@Slf4j
@Service
public class RedisPolicyAdapter implements IPolicyPort {

    @Resource
    private RedissonClient redissonClient;

    private String rateLimitLua;

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("lua/rate_limit.lua");
        rateLimitLua = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Override
    public boolean allow(String key, int limit, int seconds) {
        try {
            Long result = redissonClient.getScript().eval(
                    RScript.Mode.READ_WRITE,
                    rateLimitLua,
                    RScript.ReturnType.INTEGER,
                    Collections.singletonList(key),
                    limit, seconds
            );
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("Redis 限流执行异常, key: {}", key, e);
            return true; // 异常时默认放行，防误杀 (Fail Safe)
        }
    }
}
