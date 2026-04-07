package com.api.expose.domain.api.adapter.repository;

import com.api.expose.domain.api.model.valobj.RouteRuleVO;

import java.util.List;

/**
 * API 端点（路由映射）仓储接口
 * 负责解析后的 Endpoint 到具体实现路径的持久化定义
 */
public interface IApiEndpointRepository {

    /**
     * 根据资产ID保存路由端点定义
     */
    void saveEndpoints(Long assetId, List<RouteRuleVO> endpoints);

    /**
     * 删除资产的所有端点定义
     */
    void deleteByAssetId(Long assetId);
}
