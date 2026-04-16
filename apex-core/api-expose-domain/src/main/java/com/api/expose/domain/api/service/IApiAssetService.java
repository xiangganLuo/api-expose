package com.api.expose.domain.api.service;

import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;

/**
 * API 资产管理服务接口
 * 负责 API 的导入、发布、下架等生命周期管理
 */
public interface IApiAssetService {

    /**
     * 导入 API
     */
    void importApi(ApiAssetAggregate aggregate, String content);

    /**
     * 导入并返回资产ID
     */
    Long importApiAndReturnId(ApiAssetAggregate aggregate, String content);

    /**
     * 查询 API 资产列表
     */
    java.util.List<ApiAssetAggregate> queryApiAssets();

    /**
     * 查询 API 资产详情
     */
    ApiAssetAggregate queryApiAssetDetail(Long assetId);

    /**
     * 发布 API 到网关
     */
    void publishApi(Long assetId, String envCode);

    /**
     * 下架 API
     */
    void offlineApi(Long assetId);

    /**
     * 废弃 API
     */
    void deprecateApi(Long assetId);

    /**
     * 保存资产环境配置
     */
    void saveAssetEnv(com.api.expose.domain.api.model.entity.ApiAssetEnvEntity entity);

    /**
     * 查询资产的环境列表
     */
    java.util.List<com.api.expose.domain.api.model.entity.ApiAssetEnvEntity> queryAssetEnvs(Long assetId);

    /**
     * 删除资产环境配置
     */
    void removeAssetEnv(Long id);

    /**
     * 更新 API 资产
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

    /**
     * 查询资产下的所有端点
     */
    java.util.List<com.api.expose.domain.api.model.entity.ApiEndpointEntity> queryEndpoints(Long assetId);

    /**
     * 保存端点（新增或更新）
     */
    void saveEndpoint(com.api.expose.domain.api.model.entity.ApiEndpointEntity endpoint);

    /**
     * 删除端点
     */
    void removeEndpoint(Long endpointId);

    /**
     * 查询资产的所有版本记录
     */
    java.util.List<com.api.expose.domain.api.model.entity.ApiVersionEntity> queryVersions(Long assetId);
}

