package com.api.expose.domain.api.adapter.repository;

import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.valobj.ApiStatusEnum;

import java.util.List;

/**
 * API 资产仓储接口
 */
public interface IApiAssetRepository {

    /**
     * 保存 API 资产
     */
    void saveApiAsset(ApiAssetAggregate aggregate);

    /**
     * 根据资产ID查询
     */
    ApiAssetAggregate queryApiAssetById(Long assetId);

    /**
     * 按租户查询 API 资产列表
     */
    List<ApiAssetAggregate> queryApiAssetsByTenantId(String tenantId);

    /**
     * 更新 API 状态
     */
    void updateApiStatus(Long assetId, ApiStatusEnum status);
}
