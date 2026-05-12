package com.api.expose.domain.policy.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        
        try {
            JSONObject rule = JSON.parseObject(ruleJson);
            
            // 检查黑名单
            JSONArray blacklist = rule.getJSONArray("blacklist");
            if (blacklist != null && blacklist.contains(clientIp)) {
                log.debug("访问被拒绝: IP {} 在黑名单中", clientIp);
                return false;
            }
            
            // 检查白名单（如果配置了白名单，则只允许白名单中的 IP）
            JSONArray whitelist = rule.getJSONArray("whitelist");
            if (whitelist != null && !whitelist.isEmpty()) {
                boolean allowed = whitelist.contains(clientIp);
                if (!allowed) {
                    log.debug("访问被拒绝: IP {} 不在白名单中", clientIp);
                }
                return allowed;
            }
            
            return true;
        } catch (Exception e) {
            log.error("访问控制策略执行异常，默认放行", e);
            return true; // 异常降级放行
        }
    }
}
