package com.api.expose.domain.api.adapter.repository;

import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.entity.ApiEndpointEntity;
import com.api.expose.domain.api.model.entity.ApiVersionEntity;
import com.api.expose.domain.api.model.valobj.ApiStatusEnum;
import com.api.expose.framework.common.pojo.PageResult;

import java.util.List;

/**
 * API 资产仓储接口
 */
public interface IApiAssetRepository {

    /**
     * 保存 API 资产
     */
    Long saveApiAsset(ApiAssetAggregate aggregate);

    /**
     * 根据资产ID查询
     */
    ApiAssetAggregate queryApiAssetById(Long assetId);

    /**
     * 查询 API 资产列表
     */
    List<ApiAssetAggregate> queryApiAssets();

    /**
     * 更新 API 状态
     */
    void updateApiStatus(Long assetId, ApiStatusEnum status);

    /**
     * 更新 API 资产信息
     */
    void updateApiAsset(ApiAssetAggregate aggregate);

    /**
     * 删除 API 资产
     */
    void deleteApiAsset(Long assetId);

    /**
     * 分页查询 API 资产
     */
    PageResult<ApiAssetAggregate> pageAssets(String keywords,
                                             String groupName,
                                             String status,
                                             com.api.expose.framework.common.pojo.PageParam pageParam);

    /**
     * 查询资产下的所有端点
     */
    List<ApiEndpointEntity> queryEndpointsByAssetId(Long assetId);

    /**
     * 保存端点
     */
    void saveEndpoint(com.api.expose.domain.api.model.entity.ApiEndpointEntity endpoint, Long assetId);

    /**
     * 删除端点
     */
    void deleteEndpoint(Long endpointId);

    /**
     * 查询资产的所有版本记录
     */
    List<ApiVersionEntity> queryVersionsByAssetId(Long assetId);
}

