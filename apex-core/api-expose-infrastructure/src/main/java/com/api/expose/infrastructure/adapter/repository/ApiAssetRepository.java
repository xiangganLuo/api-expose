package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.api.adapter.repository.IApiAssetRepository;
import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.entity.ApiEndpointEntity;
import com.api.expose.domain.api.model.entity.ApiVersionEntity;
import com.api.expose.domain.api.model.valobj.ApiDefinitionVO;
import com.api.expose.domain.api.model.valobj.ApiStatusEnum;
import com.api.expose.domain.api.model.valobj.HttpMethodEnum;
import com.api.expose.infrastructure.dao.IApiAssetDao;
import com.api.expose.infrastructure.dao.IApiEndpointDao;
import com.api.expose.infrastructure.dao.IApiVersionDao;
import com.api.expose.infrastructure.dao.po.ApiAssetPO;
import com.api.expose.infrastructure.dao.po.ApiEndpointPO;
import com.api.expose.infrastructure.dao.po.ApiVersionPO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API 资产仓储实现
 */
@Repository
public class ApiAssetRepository implements IApiAssetRepository {

    @Resource
    private IApiAssetDao apiAssetDao;

    @Resource
    private IApiEndpointDao apiEndpointDao;

    @Resource
    private IApiVersionDao apiVersionDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveApiAsset(ApiAssetAggregate aggregate) {
        // 1. 保存资产基本信息
        ApiAssetPO assetPO = ApiAssetPO.builder()
                .tenantId(aggregate.getTenantId())
                .name(aggregate.getName())
                .description(aggregate.getDescription())
                .groupName(aggregate.getGroupName())
                .protocolType(aggregate.getProtocolType() != null ? aggregate.getProtocolType().name() : "HTTP")
                .status(aggregate.getStatus().getCode())
                .basePath(aggregate.getBasePath())
                .build();
        
        apiAssetDao.insert(assetPO);
        Long assetId = assetPO.getId();

        // 2. 保存端点信息
        if (aggregate.getEndpoints() != null) {
            aggregate.getEndpoints().forEach(e -> {
                ApiEndpointPO endpointPO = ApiEndpointPO.builder()
                        .assetId(assetId)
                        .path(e.getPath())
                        .httpMethod(e.getHttpMethod().getCode())
                        .name(e.getName())
                        .summary(e.getSummary())
                        .upstreamUrl(e.getUpstreamUrl())
                        .timeoutMs(e.getTimeoutMs())
                        .build();
                if (e.getDefinition() != null) {
                    endpointPO.setRequestSchema(e.getDefinition().getRequestSchema());
                    endpointPO.setResponseSchema(e.getDefinition().getResponseSchema());
                }
                apiEndpointDao.insert(endpointPO);
            });
        }

        // 3. 保存版本信息
        if (aggregate.getVersions() != null) {
            aggregate.getVersions().forEach(v -> {
                ApiVersionPO versionPO = ApiVersionPO.builder()
                        .assetId(assetId)
                        .version(v.getVersion())
                        .active(v.isActive() ? 1 : 0)
                        .releaseNote(v.getReleaseNote())
                        .build();
                apiVersionDao.insert(versionPO);
            });
        }
    }

    @Override
    public ApiAssetAggregate queryApiAssetById(Long assetId) {
        ApiAssetPO assetPO = apiAssetDao.selectById(assetId);
        if (assetPO == null) return null;

        List<ApiEndpointPO> endpointPOs = apiEndpointDao.queryByAssetId(assetId);
        List<ApiVersionPO> versionPOs = apiVersionDao.queryByAssetId(assetId);

        return buildAggregate(assetPO, endpointPOs, versionPOs);
    }

    @Override
    public List<ApiAssetAggregate> queryApiAssetsByTenantId(String tenantId) {
        List<ApiAssetPO> assetPOs = apiAssetDao.queryListByTenantId(tenantId);
        return assetPOs.stream()
                .map(po -> buildAggregate(po, Collections.emptyList(), Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateApiStatus(Long assetId, ApiStatusEnum status) {
        ApiAssetPO po = ApiAssetPO.builder()
                .id(assetId)
                .status(status.getCode())
                .build();
        apiAssetDao.updateById(po);
    }

    private ApiAssetAggregate buildAggregate(ApiAssetPO assetPO, List<ApiEndpointPO> endpointPOs, List<ApiVersionPO> versionPOs) {
        List<ApiEndpointEntity> endpoints = endpointPOs.stream().map(po -> ApiEndpointEntity.builder()
                .endpointId(po.getId())
                .path(po.getPath())
                .httpMethod(HttpMethodEnum.valueOf(po.getHttpMethod().toUpperCase()))
                .name(po.getName())
                .summary(po.getSummary())
                .upstreamUrl(po.getUpstreamUrl())
                .timeoutMs(po.getTimeoutMs())
                .definition(ApiDefinitionVO.builder()
                        .requestSchema(po.getRequestSchema())
                        .responseSchema(po.getResponseSchema())
                        .build())
                .build()).collect(Collectors.toList());

        List<ApiVersionEntity> versions = versionPOs.stream().map(po -> ApiVersionEntity.builder()
                .versionId(po.getId())
                .version(po.getVersion())
                .active(po.getActive() == 1)
                .releaseNote(po.getReleaseNote())
                .createTime(po.getCreateTime())
                .build()).collect(Collectors.toList());

        return ApiAssetAggregate.builder()
                .assetId(assetPO.getId())
                .tenantId(assetPO.getTenantId())
                .name(assetPO.getName())
                .description(assetPO.getDescription())
                .groupName(assetPO.getGroupName())
                .status(ApiStatusEnum.getEnumByCode(assetPO.getStatus()))
                .basePath(assetPO.getBasePath())
                .endpoints(endpoints)
                .versions(versions)
                .createTime(assetPO.getCreateTime())
                .updateTime(assetPO.getUpdateTime())
                .build();
    }
}
