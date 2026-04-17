package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.api.adapter.repository.IApiRouteRepository;
import com.api.expose.domain.api.model.valobj.RouteRuleVO;
import com.api.expose.framework.common.pojo.PageParam;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.infrastructure.dao.IRouteRuleDao;
import com.api.expose.infrastructure.dao.po.RouteRulePO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由规则仓储实现
 */
@Repository
public class ApiRouteRepository implements IApiRouteRepository {

    @Resource
    private IRouteRuleDao routeRuleDao;

    @Override
    public PageResult<RouteRuleVO> pageRouteRules(String keywords, PageParam pageParam) {
        LambdaQueryWrapper<RouteRulePO> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keywords)) {
            wrapper.like(RouteRulePO::getGatewayPath, keywords)
                   .or().like(RouteRulePO::getUpstreamUrl, keywords);
        }
        wrapper.orderByDesc(RouteRulePO::getCreateTime);

        Page<RouteRulePO> page = routeRuleDao.selectPage(
                new Page<>(pageParam.getPageNo(), pageParam.getPageSize()), wrapper);
        
        List<RouteRuleVO> list = page.getRecords().stream().map(po -> RouteRuleVO.builder()
                .id(po.getId())
                .apiAssetId(po.getApiAssetId())
                .endpointId(po.getEndpointId())
                .httpMethod(po.getHttpMethod())
                .gatewayPath(po.getGatewayPath())
                .upstreamPath(po.getUpstreamPath())
                .upstreamUrl(po.getUpstreamUrl())
                .status(po.getStatus())
                .build()
        ).collect(Collectors.toList());

        return new PageResult<>(list, page.getTotal());
    }
}
