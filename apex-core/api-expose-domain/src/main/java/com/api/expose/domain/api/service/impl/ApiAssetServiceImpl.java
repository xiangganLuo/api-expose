package com.api.expose.domain.api.service.impl;

import com.api.expose.domain.api.adapter.port.IApiParserPort;
import com.api.expose.domain.api.adapter.repository.IApiAssetEnvRepository;
import com.api.expose.domain.api.adapter.repository.IApiAssetRepository;
import com.api.expose.domain.api.adapter.repository.IApiEndpointRepository;
import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.entity.ApiAssetEnvEntity;
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

import static com.api.expose.framework.tenant.core.context.TenantContextHolder.getTenantId;

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

    @Resource
    private IApiAssetEnvRepository apiAssetEnvRepository;

    @Override
    public void importApi(ApiAssetAggregate aggregate, String content) {
        Long tenantId = getTenantId();
        log.info("开始导入 API: {}", aggregate.getName());
        aggregate.setTenantId(tenantId);
        if (aggregate.getStatus() == null) {
            aggregate.setStatus(ApiStatusEnum.DRAFT);
        }
        
        // 1. 解析 API 内容
        List<ApiEndpointEntity> endpoints = apiParserPort.parseOpenApi(content);
        aggregate.setEndpoints(endpoints);
        
        // 2. 持久化到库
        apiAssetRepository.saveApiAsset(aggregate);
    }

    @Override
    public Long importApiAndReturnId(ApiAssetAggregate aggregate, String content) {
        importApi(aggregate, content);
        return aggregate.getAssetId();
    }

    @Override
    public ApiAssetAggregate queryApiAssetDetail(Long assetId) {
        return apiAssetRepository.queryApiAssetById(assetId);
    }

    @Override
    public List<ApiAssetAggregate> queryApiAssets() {
        return apiAssetRepository.queryApiAssets();
    }

    @Override
    public void publishApi(Long assetId, String envCode) {
        log.info("正在发布 API: {} 到环境: {}", assetId, envCode);
        ApiAssetAggregate aggregate = apiAssetRepository.queryApiAssetById(assetId);
        if (aggregate == null) return;
        
        // 获取环境对应的 BaseUrl
        String baseUrl = apiAssetEnvRepository.queryBaseUrl(assetId, envCode);
        if (cn.hutool.core.util.StrUtil.isBlank(baseUrl)) {
            throw new RuntimeException("所选环境未配置 BaseUrl，请先在环境配置中设置");
        }

        aggregate.publish();
        apiAssetRepository.updateApiStatus(assetId, aggregate.getStatus());
        
        // 生成并保存路由规则
        List<RouteRuleVO> routeRules = aggregate.getEndpoints().stream().map(e -> {
            // 解析最终地址 (BaseUrl + RelativePath)
            String resolvedUrl = resolveUrl(baseUrl, e.getPath());

            return RouteRuleVO.builder()
                    .apiAssetId(assetId)
                    .endpointId(e.getEndpointId())
                    .httpMethod(e.getHttpMethod().getCode())
                    .gatewayPath("/" + aggregate.getGroupName() + e.getPath())
                    .upstreamPath(e.getPath())
                    .upstreamUrl(resolvedUrl)
                    .status("ACTIVE")
                    .build();
        }).collect(Collectors.toList());
        
        apiEndpointRepository.saveEndpoints(assetId, routeRules);
        
        // 广播同步到网关
        gatewaySyncPort.syncApiAsset(aggregate);
    }

    private String resolveUrl(String baseUrl, String relativePath) {
        if (cn.hutool.core.util.StrUtil.isBlank(relativePath)) {
            return baseUrl;
        }
        boolean baseEndsWithSlash = baseUrl.endsWith("/");
        boolean pathStartsWithSlash = relativePath.startsWith("/");

        if (baseEndsWithSlash && pathStartsWithSlash) {
            return baseUrl + relativePath.substring(1);
        } else if (!baseEndsWithSlash && !pathStartsWithSlash) {
            return baseUrl + "/" + relativePath;
        } else {
            return baseUrl + relativePath;
        }
    }

    @Override
    public void saveAssetEnv(ApiAssetEnvEntity entity) {
        apiAssetEnvRepository.save(entity);
    }

    @Override
    public List<ApiAssetEnvEntity> queryAssetEnvs(Long assetId) {
        return apiAssetEnvRepository.queryListByAssetId(assetId);
    }

    @Override
    public void removeAssetEnv(Long id) {
        apiAssetEnvRepository.removeById(id);
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

    @Override
    public void updateApiAsset(ApiAssetAggregate aggregate) {
        apiAssetRepository.updateApiAsset(aggregate);
    }

    @Override
    public void deleteApiAsset(Long assetId) {
        apiAssetRepository.deleteApiAsset(assetId);
        apiEndpointRepository.deleteByAssetId(assetId);
        gatewaySyncPort.removeApiAsset(assetId);
    }

    @Override
    public com.api.expose.framework.common.pojo.PageResult<ApiAssetAggregate> pageAssets(String keywords,
                                                                                          String groupName,
                                                                                          String status,
                                                                                          com.api.expose.framework.common.pojo.PageParam pageParam) {
        return apiAssetRepository.pageAssets(keywords, groupName, status, pageParam);
    }

    @Override
    public List<com.api.expose.domain.api.model.entity.ApiEndpointEntity> queryEndpoints(Long assetId) {
        return apiAssetRepository.queryEndpointsByAssetId(assetId);
    }

    @Override
    public void saveEndpoint(com.api.expose.domain.api.model.entity.ApiEndpointEntity endpoint) {
        apiAssetRepository.saveEndpoint(endpoint, endpoint.getAssetId());
    }

    @Override
    public void removeEndpoint(Long endpointId) {
        apiAssetRepository.deleteEndpoint(endpointId);
    }

    @Override
    public List<com.api.expose.domain.api.model.entity.ApiVersionEntity> queryVersions(Long assetId) {
        return apiAssetRepository.queryVersionsByAssetId(assetId);
    }
}
