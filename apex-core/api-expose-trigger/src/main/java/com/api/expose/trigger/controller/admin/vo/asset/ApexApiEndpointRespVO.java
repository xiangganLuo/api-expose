package com.api.expose.trigger.controller.admin.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx API 端点 Response VO")
@Data
public class ApexApiEndpointRespVO {

    @Schema(description = "端点编号", example = "1")
    private Long id;

    @Schema(description = "所属资产编号", example = "1")
    private Long assetId;

    @Schema(description = "路由路径", example = "/v1/users")
    private String path;

    @Schema(description = "HTTP 方法", example = "GET")
    private String httpMethod;

    @Schema(description = "端点名称", example = "获取用户列表")
    private String name;

    @Schema(description = "端点描述")
    private String summary;

    @Schema(description = "请求模型 (JSON Schema)")
    private String requestSchema;

    @Schema(description = "响应模型 (JSON Schema)")
    private String responseSchema;

    @Schema(description = "上游目标地址", example = "http://backend-service:8080/users")
    private String upstreamUrl;

    @Schema(description = "超时时间(毫秒)", example = "3000")
    private Integer timeoutMs;

}
