package com.api.expose.trigger.http;

import com.api.expose.api.dto.ApiEndpointDTO;
import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.entity.ApiEndpointEntity;
import com.api.expose.domain.api.service.IApiAssetService;
import com.api.expose.domain.gateway.adapter.port.IForwardPort;
import com.api.expose.types.common.Response;
import com.api.expose.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API 文档自动生成控制器
 * 提供交互式文档查询与在线调试代理能力
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/docs")
public class ApiDocController {

    @Resource
    private IApiAssetService apiAssetService;

    @Resource
    private IForwardPort forwardPort;

    /**
     * 获取 API 资产完整文档数据
     * 包含端点列表、请求/响应 Schema、认证方式、baseUrl
     */
    @GetMapping("/{assetId}")
    public Response<Map<String, Object>> queryApiDoc(@PathVariable Long assetId) {
        try {
            ApiAssetAggregate agg = apiAssetService.queryApiAssetDetail(assetId);
            if (agg == null) {
                return Response.failure(ResponseCode.ILLEGAL_PARAMETER.getCode(), "API 资产不存在");
            }

            // 组装文档数据
            Map<String, Object> doc = new HashMap<>();
            doc.put("assetId", agg.getAssetId());
            doc.put("name", agg.getName());
            doc.put("description", agg.getDescription());
            doc.put("groupName", agg.getGroupName());
            doc.put("status", agg.getStatus() != null ? agg.getStatus().getCode() : null);
            doc.put("protocolType", agg.getProtocolType() != null ? agg.getProtocolType().getCode() : null);
            doc.put("basePath", agg.getBasePath());
            doc.put("authType", "API_KEY");
            doc.put("baseUrl", "/open/" + agg.getGroupName());

            // 端点列表
            if (agg.getEndpoints() != null) {
                List<ApiEndpointDTO> endpoints = agg.getEndpoints().stream()
                        .map(e -> ApiEndpointDTO.builder()
                                .path(e.getPath())
                                .httpMethod(e.getHttpMethod() != null ? e.getHttpMethod().getCode() : null)
                                .summary(e.getSummary())
                                .upstreamUrl(e.getUpstreamUrl())
                                .build())
                        .collect(Collectors.toList());
                doc.put("endpoints", endpoints);
                doc.put("endpointCount", endpoints.size());
            }

            // 版本信息
            if (agg.getVersions() != null && !agg.getVersions().isEmpty()) {
                doc.put("currentVersion", agg.getVersions().get(0).getVersion());
            }

            return Response.success(doc);
        } catch (Exception e) {
            log.error("查询 API 文档失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 在线调试代理 - 接收参数并转发到上游 API
     */
    @PostMapping("/{assetId}/try")
    public Mono<ResponseEntity<byte[]>> tryApi(
            @PathVariable Long assetId,
            @RequestParam String endpointPath,
            @RequestParam(defaultValue = "GET") String httpMethod,
            @RequestBody(required = false) byte[] body) {
        try {
            ApiAssetAggregate agg = apiAssetService.queryApiAssetDetail(assetId);
            if (agg == null || agg.getEndpoints() == null) {
                return Mono.just(ResponseEntity.badRequest().body("API 资产不存在".getBytes()));
            }

            // 查找匹配的端点
            ApiEndpointEntity endpoint = agg.getEndpoints().stream()
                    .filter(e -> endpointPath.equals(e.getPath()))
                    .findFirst()
                    .orElse(null);

            if (endpoint == null || endpoint.getUpstreamUrl() == null) {
                return Mono.just(ResponseEntity.badRequest().body("端点不存在或缺少上游地址".getBytes()));
            }

            String targetUrl = endpoint.getUpstreamUrl() + endpointPath;
            HttpMethod method = HttpMethod.valueOf(httpMethod.toUpperCase());

            return forwardPort.forward(targetUrl, method, new HashMap<>(), body);
        } catch (Exception e) {
            log.error("在线调试失败", e);
            return Mono.just(ResponseEntity.internalServerError().body(e.getMessage().getBytes()));
        }
    }
}
