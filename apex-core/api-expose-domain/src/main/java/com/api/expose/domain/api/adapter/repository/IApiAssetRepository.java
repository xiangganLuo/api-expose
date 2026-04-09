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
    com.api.expose.framework.common.pojo.PageResult<ApiAssetAggregate> pageAssets(String keywords,
                                                                                   String groupName,
                                                                                   String status,
                                                                                   com.api.expose.framework.common.pojo.PageParam pageParam);
}
