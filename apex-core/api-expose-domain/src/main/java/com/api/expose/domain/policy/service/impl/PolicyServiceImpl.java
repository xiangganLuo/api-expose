package com.api.expose.domain.policy.service.impl;

import com.api.expose.domain.policy.adapter.port.IPolicyPort;
import com.api.expose.domain.policy.service.IPolicyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 策略执行服务实现
 */
@Service
public class PolicyServiceImpl implements IPolicyService {

    @Resource
    private IPolicyPort policyPort;

    @Override
    public boolean checkRateLimit(String apiKey, Long assetId) {
        // 1. 获取该资产配置的限流阈值 (本里程碑暂硬编码 10qps/app, 后续由治理策略配置确定)
        int limit = 10;
        int seconds = 1;
        
        // 2. 构造 Redis Key: ratelimit:app:{apiKey}:asset:{assetId}
        String key = String.format("ratelimit:app:%s:asset:%d", apiKey, assetId);
        
        // 3. 调用执行
        return policyPort.allow(key, limit, seconds);
    }
}
