package com.api.expose.trigger.http;

import com.api.expose.api.dto.ApiAssetResponseDTO;
import com.api.expose.api.dto.ApiImportRequestDTO;
import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.valobj.ProtocolTypeEnum;
import com.api.expose.domain.api.service.IApiAssetService;
import com.api.expose.types.common.Response;
import com.api.expose.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API 资产管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/assets")
public class ApiAssetController {

    @Resource
    private IApiAssetService apiAssetService;

    /**
     * 导入 API 资产
     */
    @PostMapping("/import")
    public Response<Boolean> importApi(@RequestBody ApiImportRequestDTO requestDTO) {
        try {
            log.info("API 导入请求:{}", requestDTO.getGroupName());

            ApiAssetAggregate aggregate = ApiAssetAggregate.builder()
                    .tenantId(requestDTO.getTenantId())
                    .name(requestDTO.getGroupName())
                    .groupName(requestDTO.getGroupName())
                    .protocolType(ProtocolTypeEnum.HTTP)
                    .build();

            apiAssetService.importApi(aggregate, requestDTO.getFileContent());

            return Response.success(true);
        } catch (Exception e) {
            log.error("API 导入失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 查询 API 资产列表
     */
    @GetMapping("")
    public Response<List<ApiAssetResponseDTO>> queryList(@RequestParam String tenantId) {
        try {
            List<ApiAssetAggregate> aggregates = apiAssetService.queryApiAssets(tenantId);
            List<ApiAssetResponseDTO> dtos = aggregates.stream().map(agg -> ApiAssetResponseDTO.builder()
                    .assetId(agg.getAssetId())
                    .name(agg.getName())
                    .groupName(agg.getGroupName())
                    .protocolType(agg.getProtocolType().getCode())
                    .status(agg.getStatus().getCode())
                    .basePath(agg.getBasePath())
                    .createTime(agg.getCreateTime())
                    .build()).collect(Collectors.toList());

            return Response.success(dtos);
        } catch (Exception e) {
            log.error("查询列表失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 查询详情
     */
    @GetMapping("/{assetId}")
    public Response<ApiAssetResponseDTO> queryDetail(@PathVariable Long assetId) {
        try {
            ApiAssetAggregate agg = apiAssetService.queryApiAssetDetail(assetId);
            if (agg == null) return Response.failure(ResponseCode.ILLEGAL_PARAMETER.getCode(), "资产不存在");

            ApiAssetResponseDTO dto = ApiAssetResponseDTO.builder()
                    .assetId(agg.getAssetId())
                    .name(agg.getName())
                    .groupName(agg.getGroupName())
                    .protocolType(agg.getProtocolType().getCode())
                    .status(agg.getStatus().getCode())
                    .basePath(agg.getBasePath())
                    .createTime(agg.getCreateTime())
                    .build();

            return Response.success(dto);
        } catch (Exception e) {
            log.error("查询详情失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 发布 API 到网关
     */
    @PostMapping("/{assetId}/publish")
    public Response<Boolean> publishApi(@PathVariable Long assetId) {
        try {
            log.info("发布 API: {}", assetId);
            apiAssetService.publishApi(assetId);
            return Response.success(true);
        } catch (Exception e) {
            log.error("发布 API 失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 下架 API
     */
    @PostMapping("/{assetId}/offline")
    public Response<Boolean> offlineApi(@PathVariable Long assetId) {
        try {
            log.info("下架 API: {}", assetId);
            apiAssetService.offlineApi(assetId);
            return Response.success(true);
        } catch (Exception e) {
            log.error("下架 API 失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 废弃 API
     */
    @PostMapping("/{assetId}/deprecate")
    public Response<Boolean> deprecateApi(@PathVariable Long assetId) {
        try {
            log.info("废弃 API: {}", assetId);
            apiAssetService.deprecateApi(assetId);
            return Response.success(true);
        } catch (Exception e) {
            log.error("废弃 API 失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }
}
