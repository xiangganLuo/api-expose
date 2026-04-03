package com.api.expose.domain.gateway.adapter.repository;

import com.api.expose.domain.gateway.model.entity.RouteRuleEntity;

/**
 * 网关运行时路由仓储接口
 * 负责在请求进入时，根据路径匹配目标上游配置
 */
public interface IGatewayRoutingRepository {

    /**
     * 根据网关路径匹配规则
     * @param gatewayPath 请求路径
     * @param method HTTP方法
     * @return 路由规则实体
     */
    RouteRuleEntity findByPath(String gatewayPath, String method);

}
