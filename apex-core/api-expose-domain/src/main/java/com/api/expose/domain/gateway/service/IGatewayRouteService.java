package com.api.expose.domain.gateway.service;

import com.api.expose.domain.gateway.model.aggregate.RouteRuleAggregate;

import java.util.List;

/**
 * 网关路由服务接口
 */
public interface IGatewayRouteService {

    /**
     * 根据 API 资产发布生成路由规则
     */
    void generateRoutes(Long apiAssetId);

    /**
     * 查询所有生效路由
     */
    List<RouteRuleAggregate> queryActiveRoutes();

    /**
     * 停用 API 关联的所有路由
     */
    void deactivateRoutesByApiAssetId(Long apiAssetId);
}
