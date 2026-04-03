package com.api.expose.domain.api.service.impl;

import com.api.expose.domain.api.adapter.port.IApiParserPort;
import com.api.expose.domain.api.adapter.repository.IApiAssetRepository;
import com.api.expose.domain.api.adapter.repository.IApiEndpointRepository;
import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.entity.ApiEndpointEntity;
import com.api.expose.domain.api.model.valobj.ApiStatusEnum;
import com.api.expose.domain.api.model.valobj.RouteRuleVO;
import com.api.expose.domain.api.service.IApiAssetService;
import com.api.expose.domain.gateway.adapter.port.IGatewaySyncPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API 资产领域服务实现
 */
@Slf4j
@Service
public class ApiAssetServiceImpl implements IApiAssetService {

    @Resource
    private IApiAssetRepository apiAssetRepository;
    
    @Resource
    private IApiParserPort apiParserPort;

    @Resource
    private IGatewaySyncPort gatewaySyncPort;

    @Resource
    private IApiEndpointRepository apiEndpointRepository;

    @Override
    public void importApi(ApiAssetAggregate aggregate, String content) {
        log.info("开始导入 API: {}", aggregate.getName());
        
        // 1. 解析 API 内容
        List<ApiEndpointEntity> endpoints = apiParserPort.parseOpenApi(content);
        aggregate.setEndpoints(endpoints);
        
        // 2. 持久化到库
        apiAssetRepository.saveApiAsset(aggregate);
    }

    @Override
    public ApiAssetAggregate queryApiAssetDetail(Long assetId) {
        return apiAssetRepository.queryApiAssetById(assetId);
    }

    @Override
    public List<ApiAssetAggregate> queryApiAssets(String tenantId) {
        return apiAssetRepository.queryApiAssetsByTenantId(tenantId);
    }

    @Override
    public void publishApi(Long assetId) {
        log.info("正在发布 API: {}", assetId);
        ApiAssetAggregate aggregate = apiAssetRepository.queryApiAssetById(assetId);
        if (aggregate == null) return;
        
        aggregate.publish();
        apiAssetRepository.updateApiStatus(assetId, aggregate.getStatus());
        
        // 生成并保存路由规则
        List<RouteRuleVO> routeRules = aggregate.getEndpoints().stream().map(e -> RouteRuleVO.builder()
                .apiAssetId(assetId)
                .endpointId(e.getEndpointId())
                .httpMethod(e.getHttpMethod().getCode())
                .gatewayPath("/" + aggregate.getGroupName() + e.getPath())
                .upstreamPath(e.getPath())
                .upstreamUrl(e.getUpstreamUrl())
                .status("ACTIVE")
                .build()).collect(Collectors.toList());
        
        apiEndpointRepository.saveEndpoints(assetId, routeRules);
        
        // 广播同步到网关
        gatewaySyncPort.syncApiAsset(aggregate);
    }

    @Override
    public void offlineApi(Long assetId) {
        log.info("正在下架 API: {}", assetId);
        ApiAssetAggregate aggregate = apiAssetRepository.queryApiAssetById(assetId);
        if (aggregate == null) return;
        
        aggregate.offline();
        apiAssetRepository.updateApiStatus(assetId, aggregate.getStatus());
        
        // 删除路由规则
        apiEndpointRepository.deleteByAssetId(assetId);
        
        // 广播下架同步
        gatewaySyncPort.removeApiAsset(assetId);
    }

    @Override
    public void deprecateApi(Long assetId) {
        log.info("正在废弃 API: {}", assetId);
        ApiAssetAggregate aggregate = apiAssetRepository.queryApiAssetById(assetId);
        if (aggregate == null) return;
        
        aggregate.offline(); // 废弃前先下线
        apiAssetRepository.updateApiStatus(assetId, ApiStatusEnum.DEPRECATED);
        
        // 广播下架
        gatewaySyncPort.removeApiAsset(assetId);
    }
}
