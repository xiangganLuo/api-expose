package com.api.expose.domain.gateway.adapter.port;

import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;

/**
 * 网关同步端口 - 用于广播 API 发布/更新消息
 */
public interface IGatewaySyncPort {

    /**
     * 同步 API 资产到网关
     */
    void syncApiAsset(ApiAssetAggregate aggregate);

    /**
     * 同步移出（线下）API
     */
    void removeApiAsset(Long assetId);

    /**
     * 同步订阅权限到网关
     */
    void syncSubscription(Long appId, Long apiAssetId, String apiKey);

    /**
     * 移除订阅权限
     */
    void removeSubscription(Long appId, Long apiAssetId);
}
