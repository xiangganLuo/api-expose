package com.api.expose.trigger.controller.admin.vo.route;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - APEx 网关路由规则 Response VO")
@Data
public class ApexRouteRuleRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "API资产ID", example = "1")
    private Long apiAssetId;

    @Schema(description = "端点ID", example = "1")
    private Long endpointId;

    @Schema(description = "请求方法", example = "GET")
    private String httpMethod;

    @Schema(description = "网关路径", example = "/auth/v1/login")
    private String gatewayPath;

    @Schema(description = "上游路径", example = "/login")
    private String upstreamPath;

    @Schema(description = "上游真实地址", example = "http://192.168.1.10:8080/login")
    private String upstreamUrl;

    @Schema(description = "状态", example = "ACTIVE")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
