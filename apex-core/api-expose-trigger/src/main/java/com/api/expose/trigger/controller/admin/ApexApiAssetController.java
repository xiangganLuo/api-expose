package com.api.expose.trigger.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.valobj.ApiStatusEnum;
import com.api.expose.domain.api.model.valobj.ProtocolTypeEnum;
import com.api.expose.domain.api.service.IApiAssetService;
import com.api.expose.infrastructure.gateway.HttpForwardService;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiAssetImportReqVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiAssetPageReqVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiAssetRespVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiAssetUpdateReqVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiTryReqVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiEndpointRespVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiEndpointSaveReqVO;
import com.api.expose.trigger.controller.admin.vo.asset.ApexApiVersionRespVO;
import com.api.expose.domain.api.model.entity.ApiEndpointEntity;
import com.api.expose.framework.common.util.UrlUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.api.expose.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - APEx - API 资产")
@RestController
@RequestMapping("/apex/assets")
@Validated
public class ApexApiAssetController {

    @Resource
    private IApiAssetService apiAssetService;

    @Resource
    private HttpForwardService httpForwardService;

    @PostMapping("/import")
    @Operation(summary = "资产导入")
    @PreAuthorize("@ss.hasPermission('apex:asset:import')")
    public CommonResult<Long> importAsset(@Valid @RequestBody ApexApiAssetImportReqVO reqVO) {
        ApiAssetAggregate aggregate = ApiAssetAggregate.builder()
                .name(reqVO.getGroupName())
                .groupName(reqVO.getGroupName())
                .protocolType(ProtocolTypeEnum.HTTP)
                .status(ApiStatusEnum.DRAFT)
                .build();
        Long id = apiAssetService.importApiAndReturnId(aggregate, reqVO.getFileContent());
        return success(id);
    }

    @GetMapping("/page")
    @Operation(summary = "资产分页")
    @PreAuthorize("@ss.hasPermission('apex:asset:query')")
    public CommonResult<PageResult<ApexApiAssetRespVO>> getAssetPage(@Valid ApexApiAssetPageReqVO pageReqVO) {
        PageResult<ApiAssetAggregate> pageResult = apiAssetService.pageAssets(
                pageReqVO.getKeywords(), pageReqVO.getGroupName(), pageReqVO.getStatus(), pageReqVO);
        List<ApexApiAssetRespVO> list = pageResult.getList().stream().map(this::convert).collect(Collectors.toList());
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "资产详情")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:query')")
    public CommonResult<ApexApiAssetRespVO> getAsset(@RequestParam("id") Long id) {
        ApiAssetAggregate agg = apiAssetService.queryApiAssetDetail(id);
        return success(agg == null ? null : convert(agg));
    }

    @PutMapping("/update")
    @Operation(summary = "资产更新")
    @PreAuthorize("@ss.hasPermission('apex:asset:update')")
    public CommonResult<Boolean> updateAsset(@Valid @RequestBody ApexApiAssetUpdateReqVO reqVO) {
        ApiAssetAggregate aggregate = ApiAssetAggregate.builder()
                .assetId(reqVO.getId())
                .name(reqVO.getName())
                .description(reqVO.getDescription())
                .groupName(reqVO.getGroupName())
                .basePath(reqVO.getBasePath())
                .status(reqVO.getStatus() != null ? ApiStatusEnum.getEnumByCode(reqVO.getStatus()) : null)
                .build();
        apiAssetService.updateApiAsset(aggregate);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "资产删除")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:delete')")
    public CommonResult<Boolean> deleteAsset(@RequestParam("id") Long id) {
        apiAssetService.deleteApiAsset(id);
        return success(true);
    }

    @PostMapping("/publish")
    @Operation(summary = "资产发布")
    @PreAuthorize("@ss.hasPermission('apex:asset:publish')")
    public CommonResult<Boolean> publish(@RequestParam("id") Long id,
                                         @RequestParam("envCode") String envCode) {
        apiAssetService.publishApi(id, envCode);
        return success(true);
    }

    @PostMapping("/offline")
    @Operation(summary = "资产下架")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:offline')")
    public CommonResult<Boolean> offline(@RequestParam("id") Long id) {
        apiAssetService.offlineApi(id);
        return success(true);
    }

    @PostMapping("/deprecate")
    @Operation(summary = "资产废弃")
    @Parameter(name = "id", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:deprecate')")
    public CommonResult<Boolean> deprecate(@RequestParam("id") Long id) {
        apiAssetService.deprecateApi(id);
        return success(true);
    }

    @PostMapping("/try")
    @Operation(summary = "在线调试")
    @PreAuthorize("@ss.hasPermission('apex:asset:try')")
    public Mono<ResponseEntity<byte[]>> tryEndpoint(@Valid @RequestBody ApexApiTryReqVO reqVO) {
        String envCode = StrUtil.isBlank(reqVO.getEnvCode()) ? "test" : reqVO.getEnvCode();

        // 1. 找到对应的端点获取路径
        List<ApiEndpointEntity> endpoints = apiAssetService.queryEndpoints(reqVO.getAssetId());
        ApiEndpointEntity endpoint = endpoints.stream()
                .filter(e -> e.getPath().equals(reqVO.getEndpointPath()))
                .findFirst().orElse(null);

        if (endpoint == null) {
            return Mono.just(ResponseEntity.status(404).body("Endpoint not found".getBytes()));
        }

        // 2. 获取环境对应的 BaseUrl
        List<com.api.expose.domain.api.model.entity.ApiAssetEnvEntity> assetEnvs = apiAssetService.queryAssetEnvs(reqVO.getAssetId());
        String baseUrl = assetEnvs.stream()
                .filter(env -> env.getEnvCode().equals(envCode))
                .map(com.api.expose.domain.api.model.entity.ApiAssetEnvEntity::getBaseUrl)
                .findFirst().orElse(null);

        if (StrUtil.isBlank(baseUrl)) {
            // 如果环境未配置，尝试使用默认上游地址 (如果有的话)
            baseUrl = endpoint.getUpstreamUrl();
        } else {
            // 拼接最终地址
            baseUrl = UrlUtils.resolveUrl(baseUrl, endpoint.getPath());
        }

        if (StrUtil.isBlank(baseUrl)) {
            return Mono.just(ResponseEntity.status(400).body("Target URL could not be resolved".getBytes()));
        }

        // 3. 转换 HttpMethod & 执行转发
        HttpMethod method = HttpMethod.valueOf(reqVO.getHttpMethod().toUpperCase());
        return httpForwardService.forward(baseUrl, method, reqVO.getHeaders(), reqVO.getBody());
    }

    // ====== 资产环境配置 ======

    @GetMapping("/envs/list")
    @Operation(summary = "查询资产环境列表")
    @Parameter(name = "assetId", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:query')")
    public CommonResult<List<com.api.expose.trigger.controller.admin.vo.asset.env.ApexAssetEnvRespVO>> getAssetEnvs(@RequestParam("assetId") Long assetId) {
        List<com.api.expose.domain.api.model.entity.ApiAssetEnvEntity> list = apiAssetService.queryAssetEnvs(assetId);
        return success(list.stream().map(this::convert).collect(Collectors.toList()));
    }

    @PostMapping("/envs/save")
    @Operation(summary = "保存资产环境 (新增/更新)")
    @PreAuthorize("@ss.hasPermission('apex:asset:update')")
    public CommonResult<Boolean> saveAssetEnv(@Valid @RequestBody com.api.expose.trigger.controller.admin.vo.asset.env.ApexAssetEnvSaveReqVO reqVO) {
        com.api.expose.domain.api.model.entity.ApiAssetEnvEntity entity = com.api.expose.domain.api.model.entity.ApiAssetEnvEntity.builder()
                .id(reqVO.getId())
                .assetId(reqVO.getAssetId())
                .envCode(reqVO.getEnvCode())
                .envName(reqVO.getEnvName())
                .baseUrl(reqVO.getBaseUrl())
                .status(reqVO.getStatus())
                .build();
        entity.validate();
        apiAssetService.saveAssetEnv(entity);
        return success(true);
    }

    @DeleteMapping("/envs/delete")
    @Operation(summary = "删除资产环境")
    @Parameter(name = "id", description = "配置编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:update')")
    public CommonResult<Boolean> deleteAssetEnv(@RequestParam("id") Long id) {
        apiAssetService.removeAssetEnv(id);
        return success(true);
    }

    private com.api.expose.trigger.controller.admin.vo.asset.env.ApexAssetEnvRespVO convert(com.api.expose.domain.api.model.entity.ApiAssetEnvEntity entity) {
        com.api.expose.trigger.controller.admin.vo.asset.env.ApexAssetEnvRespVO vo = new com.api.expose.trigger.controller.admin.vo.asset.env.ApexAssetEnvRespVO();
        vo.setId(entity.getId());
        vo.setAssetId(entity.getAssetId());
        vo.setEnvCode(entity.getEnvCode());
        vo.setEnvName(entity.getEnvName());
        vo.setBaseUrl(entity.getBaseUrl());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    @GetMapping("/endpoints/list")
    @Operation(summary = "查询端点列表")
    @Parameter(name = "assetId", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:query')")
    public CommonResult<List<ApexApiEndpointRespVO>> getEndpoints(@RequestParam("assetId") Long assetId) {
        List<ApiEndpointEntity> list = apiAssetService.queryEndpoints(assetId);
        return success(list.stream().map(this::convert).collect(Collectors.toList()));
    }

    @PostMapping("/endpoints/save")
    @Operation(summary = "保存端点 (新增/更新)")
    @PreAuthorize("@ss.hasPermission('apex:asset:update')")
    public CommonResult<Boolean> saveEndpoint(@Valid @RequestBody ApexApiEndpointSaveReqVO reqVO) {
        ApiEndpointEntity entity = ApiEndpointEntity.builder()
                .endpointId(reqVO.getId())
                .assetId(reqVO.getAssetId())
                .path(reqVO.getPath())
                .httpMethod(com.api.expose.domain.api.model.valobj.HttpMethodEnum.valueOf(reqVO.getHttpMethod().toUpperCase()))
                .name(reqVO.getName())
                .summary(reqVO.getSummary())
                .upstreamUrl(reqVO.getUpstreamUrl())
                .timeoutMs(reqVO.getTimeoutMs())
                .definition(com.api.expose.domain.api.model.valobj.ApiDefinitionVO.builder()
                        .requestSchema(reqVO.getRequestSchema())
                        .responseSchema(reqVO.getResponseSchema())
                        .build())
                .build();
        apiAssetService.saveEndpoint(entity);
        return success(true);
    }

    @DeleteMapping("/endpoints/delete")
    @Operation(summary = "删除端点")
    @Parameter(name = "id", description = "端点编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:update')")
    public CommonResult<Boolean> deleteEndpoint(@RequestParam("id") Long id) {
        apiAssetService.removeEndpoint(id);
        return success(true);
    }

    @GetMapping("/versions/list")
    @Operation(summary = "查询版本记录")
    @Parameter(name = "assetId", description = "资产编号", required = true)
    @PreAuthorize("@ss.hasPermission('apex:asset:query')")
    public CommonResult<List<ApexApiVersionRespVO>> getVersions(@RequestParam("assetId") Long assetId) {
        List<com.api.expose.domain.api.model.entity.ApiVersionEntity> list = apiAssetService.queryVersions(assetId);
        return success(list.stream().map(this::convert).collect(Collectors.toList()));
    }

    private ApexApiAssetRespVO convert(ApiAssetAggregate agg) {
        ApexApiAssetRespVO vo = new ApexApiAssetRespVO();
        vo.setId(agg.getAssetId());
        vo.setName(agg.getName());
        vo.setDescription(agg.getDescription());
        vo.setGroupName(agg.getGroupName());
        vo.setProtocolType(agg.getProtocolType() != null ? agg.getProtocolType().name() : null);
        vo.setStatus(agg.getStatus() != null ? agg.getStatus().getCode() : null);
        vo.setBasePath(agg.getBasePath());
        vo.setCreateTime(agg.getCreateTime());
        vo.setUpdateTime(agg.getUpdateTime());
        return vo;
    }

    private ApexApiEndpointRespVO convert(ApiEndpointEntity entity) {
        ApexApiEndpointRespVO vo = new ApexApiEndpointRespVO();
        vo.setId(entity.getEndpointId());
        vo.setAssetId(entity.getAssetId());
        vo.setPath(entity.getPath());
        vo.setHttpMethod(entity.getHttpMethod() != null ? entity.getHttpMethod().getCode() : null);
        vo.setName(entity.getName());
        vo.setSummary(entity.getSummary());
        if (entity.getDefinition() != null) {
            vo.setRequestSchema(entity.getDefinition().getRequestSchema());
            vo.setResponseSchema(entity.getDefinition().getResponseSchema());
        }
        vo.setUpstreamUrl(entity.getUpstreamUrl());
        vo.setTimeoutMs(entity.getTimeoutMs());
        return vo;
    }

    private ApexApiVersionRespVO convert(com.api.expose.domain.api.model.entity.ApiVersionEntity entity) {
        ApexApiVersionRespVO vo = new ApexApiVersionRespVO();
        vo.setId(entity.getVersionId());
        vo.setAssetId(null); // Entity 中暂未透传 assetId，此处由前端上下文持有
        vo.setVersion(entity.getVersion());
        vo.setActive(entity.isActive() ? 1 : 0);
        vo.setReleaseNote(entity.getReleaseNote());
        vo.setCreateTime(entity.getCreateTime() != null ? new java.sql.Timestamp(entity.getCreateTime().getTime()).toLocalDateTime() : null);
        return vo;
    }
}

