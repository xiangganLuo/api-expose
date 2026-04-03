package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.api.adapter.repository.IApiEndpointRepository;
import com.api.expose.domain.api.model.valobj.RouteRuleVO;
import com.api.expose.infrastructure.dao.IRouteRuleDao;
import com.api.expose.infrastructure.dao.po.RouteRulePO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API 端点（路由映射）仓储实现
 */
@Repository
public class ApiEndpointRepository implements IApiEndpointRepository {

    @Resource
    private IRouteRuleDao routeRuleDao;

    @Override
    public void saveEndpoints(Long assetId, List<RouteRuleVO> endpoints) {
        // 先清理旧的端点定义
        deleteByAssetId(assetId);
        
        // 转换并批量保存新的定义
        List<RouteRulePO> pos = endpoints.stream().map(vo -> RouteRulePO.builder()
                .apiAssetId(assetId)
                .endpointId(vo.getEndpointId())
                .httpMethod(vo.getHttpMethod())
                .gatewayPath(vo.getGatewayPath())
                .upstreamPath(vo.getUpstreamPath())
                .upstreamUrl(vo.getUpstreamUrl())
                .status(vo.getStatus())
                .createTime(new Date())
                .updateTime(new Date())
                .build()).collect(Collectors.toList());
        
        for (RouteRulePO po : pos) {
            routeRuleDao.insert(po);
        }
    }

    @Override
    public void deleteByAssetId(Long assetId) {
        LambdaQueryWrapper<RouteRulePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RouteRulePO::getApiAssetId, assetId);
        routeRuleDao.delete(wrapper);
    }
}
