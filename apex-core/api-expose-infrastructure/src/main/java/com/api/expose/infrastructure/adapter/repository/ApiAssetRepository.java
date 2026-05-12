package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.api.adapter.repository.IApiAssetRepository;
import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.valobj.ApiStatusEnum;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.infrastructure.dao.IApiAssetDao;
import com.api.expose.infrastructure.dao.IApiEndpointDao;
import com.api.expose.infrastructure.dao.IApiVersionDao;
import com.api.expose.infrastructure.dao.po.ApiAssetPO;
import com.api.expose.infrastructure.dao.po.ApiEndpointPO;
import com.api.expose.infrastructure.dao.po.ApiVersionPO;
import com.api.expose.infrastructure.convert.api.ApiAssetConvert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public Long saveApiAsset(ApiAssetAggregate aggregate) {
        // 1. 保存资产基本信息
        ApiAssetPO assetPO = ApiAssetConvert.INSTANCE.convert(aggregate);
        
        apiAssetDao.insert(assetPO);
        Long assetId = assetPO.getId();
        aggregate.setAssetId(assetId);

        // 2. 保存端点信息
        if (aggregate.getEndpoints() != null) {
            aggregate.getEndpoints().forEach(e -> {
                ApiEndpointPO endpointPO = ApiAssetConvert.INSTANCE.convert(e, assetPO.getTenantId(), assetId);
                apiEndpointDao.insert(endpointPO);
            });
        }

        // 3. 保存版本信息
        if (aggregate.getVersions() != null) {
            aggregate.getVersions().forEach(v -> {
                ApiVersionPO versionPO = ApiAssetConvert.INSTANCE.convert(v, assetPO.getTenantId(), assetId);
                apiVersionDao.insert(versionPO);
            });
        }

        return assetId;
    }

    @Override
    public ApiAssetAggregate queryApiAssetById(Long assetId) {
        ApiAssetPO assetPO = apiAssetDao.selectById(assetId);
        if (assetPO == null) return null;

        List<ApiEndpointPO> endpointPOs = apiEndpointDao.selectList(
                new LambdaQueryWrapper<ApiEndpointPO>().eq(ApiEndpointPO::getAssetId, assetId));
        List<ApiVersionPO> versionPOs = apiVersionDao.selectList(
                new LambdaQueryWrapper<ApiVersionPO>().eq(ApiVersionPO::getAssetId, assetId));

        return buildAggregate(assetPO, endpointPOs, versionPOs);
    }

    @Override
    public List<ApiAssetAggregate> queryApiAssets() {
        List<ApiAssetPO> assetPOs = apiAssetDao.selectList(new LambdaQueryWrapper<>());
        if (assetPOs.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 批量查询所有资产的端点和版本，避免 N+1 问题
        List<Long> assetIds = assetPOs.stream().map(ApiAssetPO::getId).collect(Collectors.toList());
        
        List<ApiEndpointPO> allEndpoints = apiEndpointDao.selectList(
                new LambdaQueryWrapper<ApiEndpointPO>().in(ApiEndpointPO::getAssetId, assetIds));
        List<ApiVersionPO> allVersions = apiVersionDao.selectList(
                new LambdaQueryWrapper<ApiVersionPO>().in(ApiVersionPO::getAssetId, assetIds));
        
        // 按 assetId 分组
        Map<Long, List<ApiEndpointPO>> endpointsByAsset = allEndpoints.stream()
                .collect(Collectors.groupingBy(ApiEndpointPO::getAssetId));
        Map<Long, List<ApiVersionPO>> versionsByAsset = allVersions.stream()
                .collect(Collectors.groupingBy(ApiVersionPO::getAssetId));
        
        return assetPOs.stream()
                .map(po -> buildAggregate(
                        po,
                        endpointsByAsset.getOrDefault(po.getId(), Collections.emptyList()),
                        versionsByAsset.getOrDefault(po.getId(), Collections.emptyList())
                ))
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

    @Override
    public void updateApiAsset(ApiAssetAggregate aggregate) {
        ApiAssetPO po = ApiAssetConvert.INSTANCE.convert(aggregate);
        apiAssetDao.updateById(po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApiAsset(Long assetId) {
        apiAssetDao.deleteById(assetId);
        apiEndpointDao.delete(new LambdaQueryWrapper<ApiEndpointPO>().eq(ApiEndpointPO::getAssetId, assetId));
        apiVersionDao.delete(new LambdaQueryWrapper<ApiVersionPO>().eq(ApiVersionPO::getAssetId, assetId));
    }

    @Override
    public PageResult<ApiAssetAggregate> pageAssets(String keywords, String groupName, String status,
                                                    com.api.expose.framework.common.pojo.PageParam pageParam) {
        LambdaQueryWrapper<ApiAssetPO> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keywords)) {
            wrapper.like(ApiAssetPO::getName, keywords).or().like(ApiAssetPO::getGroupName, keywords);
        }
        if (StrUtil.isNotBlank(groupName)) {
            wrapper.eq(ApiAssetPO::getGroupName, groupName);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(ApiAssetPO::getStatus, status);
        }
        wrapper.orderByDesc(ApiAssetPO::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ApiAssetPO> page =
                apiAssetDao.selectPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageParam.getPageNo(), pageParam.getPageSize()), wrapper);
        java.util.List<ApiAssetAggregate> list = page.getRecords().stream()
                .map(po -> buildAggregate(po, java.util.Collections.emptyList(), java.util.Collections.emptyList()))
                .collect(java.util.stream.Collectors.toList());
        return new com.api.expose.framework.common.pojo.PageResult<>(list, page.getTotal());
    }

    private ApiAssetAggregate buildAggregate(ApiAssetPO assetPO, List<ApiEndpointPO> endpointPOs, List<ApiVersionPO> versionPOs) {
        return ApiAssetAggregate.builder()
                .assetId(assetPO.getId())
                .tenantId(assetPO.getTenantId())
                .name(assetPO.getName())
                .description(assetPO.getDescription())
                .groupName(assetPO.getGroupName())
                .protocolType(assetPO.getProtocolType() != null ? com.api.expose.domain.api.model.valobj.ProtocolTypeEnum.valueOf(assetPO.getProtocolType()) : null)
                .status(assetPO.getStatus() != null ? com.api.expose.domain.api.model.valobj.ApiStatusEnum.getEnumByCode(assetPO.getStatus()) : null)
                .basePath(assetPO.getBasePath())
                .endpoints(convertEndpoints(endpointPOs))
                .versions(convertVersions(versionPOs))
                .createTime(assetPO.getCreateTime())
                .updateTime(assetPO.getUpdateTime())
                .build();
    }

    private List<com.api.expose.domain.api.model.entity.ApiEndpointEntity> convertEndpoints(List<ApiEndpointPO> pos) {
        return pos.stream().map(po ->
                com.api.expose.domain.api.model.entity.ApiEndpointEntity.builder()
                        .endpointId(po.getId())
                        .path(po.getPath())
                        .httpMethod(po.getHttpMethod() != null
                                ? com.api.expose.domain.api.model.valobj.HttpMethodEnum.valueOf(po.getHttpMethod().toUpperCase())
                                : null)
                        .name(po.getName())
                        .summary(po.getSummary())
                        .upstreamUrl(po.getUpstreamUrl())
                        .timeoutMs(po.getTimeoutMs())
                        .definition(com.api.expose.domain.api.model.valobj.ApiDefinitionVO.builder()
                                .requestSchema(po.getRequestSchema())
                                .responseSchema(po.getResponseSchema())
                                .build())
                        .build()
        ).collect(Collectors.toList());
    }

    private List<com.api.expose.domain.api.model.entity.ApiVersionEntity> convertVersions(List<ApiVersionPO> pos) {
        return pos.stream().map(po ->
                com.api.expose.domain.api.model.entity.ApiVersionEntity.builder()
                        .versionId(po.getId())
                        .version(po.getVersion())
                        .active(po.getActive() != null && po.getActive() == 1)
                        .releaseNote(po.getReleaseNote())
                        .createTime(po.getCreateTime() != null ? java.sql.Timestamp.valueOf(po.getCreateTime()) : null)
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<com.api.expose.domain.api.model.entity.ApiEndpointEntity> queryEndpointsByAssetId(Long assetId) {
        List<ApiEndpointPO> pos = apiEndpointDao.selectList(new LambdaQueryWrapper<ApiEndpointPO>().eq(ApiEndpointPO::getAssetId, assetId));
        return convertEndpoints(pos);
    }

    @Override
    public void saveEndpoint(com.api.expose.domain.api.model.entity.ApiEndpointEntity endpoint, Long assetId) {
        ApiAssetPO assetPO = apiAssetDao.selectById(assetId);
        if (assetPO == null) return;
        ApiEndpointPO po = ApiAssetConvert.INSTANCE.convert(endpoint, assetPO.getTenantId(), assetId);
        if (po.getId() == null) {
            apiEndpointDao.insert(po);
        } else {
            apiEndpointDao.updateById(po);
        }
    }

    @Override
    public void deleteEndpoint(Long endpointId) {
        apiEndpointDao.deleteById(endpointId);
    }

    @Override
    public List<com.api.expose.domain.api.model.entity.ApiVersionEntity> queryVersionsByAssetId(Long assetId) {
        List<ApiVersionPO> pos = apiVersionDao.selectList(new LambdaQueryWrapper<ApiVersionPO>().eq(ApiVersionPO::getAssetId, assetId));
        return convertVersions(pos);
    }
}

