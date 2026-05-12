package com.api.expose.infrastructure.adapter.port;

import com.alibaba.fastjson.JSON;
import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.gateway.adapter.port.IGatewaySyncPort;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息同步适配器 - 基于 Redis Pub/Sub
 */
@Slf4j
@Component
public class RedisSyncAdapter implements IGatewaySyncPort {

    private static final String GATEWAY_SYNC_TOPIC = "gateway_api_sync";

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void syncApiAsset(ApiAssetAggregate aggregate) {
        try {
            RTopic topic = redissonClient.getTopic(GATEWAY_SYNC_TOPIC);
            
            // 封装消息对象
            Map<String, Object> message = new HashMap<>();
            message.put("type", "SYNC");
            message.put("assetId", aggregate.getAssetId());
            message.put("aggregate", aggregate);
            message.put("timestamp", System.currentTimeMillis());

            long count = topic.publish(JSON.toJSONString(message));
            log.debug("API 同步消息已广播, assetId: {}, 接收者数量: {}", aggregate.getAssetId(), count);
        } catch (Exception e) {
            log.error("API 同步故障", e);
        }
    }

    @Override
    public void removeApiAsset(Long assetId) {
        try {
            RTopic topic = redissonClient.getTopic(GATEWAY_SYNC_TOPIC);
            
            Map<String, Object> message = new HashMap<>();
            message.put("type", "REMOVE");
            message.put("assetId", assetId);
            message.put("timestamp", System.currentTimeMillis());

            topic.publish(JSON.toJSONString(message));
            log.debug("API 移除消息已广播, assetId: {}", assetId);
        } catch (Exception e) {
            log.error("API 移除广播故障", e);
        }
    }

    @Override
    public void syncSubscription(Long appId, Long apiAssetId, String apiKey) {
        try {
            RTopic topic = redissonClient.getTopic("gateway_subscription_sync");
            Map<String, Object> message = new HashMap<>();
            message.put("type", "SYNC");
            message.put("appId", appId);
            message.put("apiAssetId", apiAssetId);
            message.put("apiKey", apiKey);
            message.put("timestamp", System.currentTimeMillis());

            topic.publish(JSON.toJSONString(message));
            log.debug("订阅同步消息已广播, apiKey: {}, assetId: {}", apiKey, apiAssetId);
        } catch (Exception e) {
            log.error("订阅同步故障", e);
        }
    }

    @Override
    public void removeSubscription(Long appId, Long apiAssetId) {
        try {
            RTopic topic = redissonClient.getTopic("gateway_subscription_sync");
            Map<String, Object> message = new HashMap<>();
            message.put("type", "REMOVE");
            message.put("appId", appId);
            message.put("apiAssetId", apiAssetId);
            message.put("timestamp", System.currentTimeMillis());

            topic.publish(JSON.toJSONString(message));
            log.debug("订阅移除消息已广播, appId: {}, assetId: {}", appId, apiAssetId);
        } catch (Exception e) {
            log.error("订阅移除故障", e);
        }
    }
}
