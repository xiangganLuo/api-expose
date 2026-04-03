package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.gateway.adapter.repository.IGatewayRoutingRepository;
import com.api.expose.domain.gateway.model.entity.RouteRuleEntity;
import com.api.expose.infrastructure.dao.IRouteRuleDao;
import com.api.expose.infrastructure.dao.po.RouteRulePO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * 网关运行时路由仓储实现
 */
@Repository
public class GatewayRoutingRepository implements IGatewayRoutingRepository {

    @Resource
    private IRouteRuleDao routeRuleDao;

    @Override
    public RouteRuleEntity findByPath(String gatewayPath, String method) {
        LambdaQueryWrapper<RouteRulePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RouteRulePO::getGatewayPath, gatewayPath);
        wrapper.eq(RouteRulePO::getHttpMethod, method);
        wrapper.eq(RouteRulePO::getStatus, "ACTIVE");
        RouteRulePO po = routeRuleDao.selectOne(wrapper);
        
        if (po == null) return null;
        
        return RouteRuleEntity.builder()
                .id(po.getId())
                .apiAssetId(po.getApiAssetId())
                .endpointId(po.getEndpointId())
                .httpMethod(po.getHttpMethod())
                .gatewayPath(po.getGatewayPath())
                .upstreamPath(po.getUpstreamPath())
                .upstreamUrl(po.getUpstreamUrl())
                .status(po.getStatus())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }
}
