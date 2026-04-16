package com.api.expose.trigger.controller.admin.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx API 端点保存 Request VO")
@Data
public class ApexApiEndpointSaveReqVO {

    @Schema(description = "端点编号（新增时不填，修改时必填）", example = "1")
    private Long id;

    @Schema(description = "资产编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "资产编号不能为空")
    private Long assetId;

    @Schema(description = "路由路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "/v1/users")
    @NotBlank(message = "路由路径不能为空")
    private String path;

    @Schema(description = "HTTP 方法", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @NotBlank(message = "HTTP 方法不能为空")
    private String httpMethod;

    @Schema(description = "端点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "获取用户列表")
    @NotBlank(message = "端点名称不能为空")
    private String name;

    @Schema(description = "端点描述")
    private String summary;

    @Schema(description = "请求模型 (JSON Schema)")
    private String requestSchema;

    @Schema(description = "响应模型 (JSON Schema)")
    private String responseSchema;

    @Schema(description = "从上游服务继承地址时的相对路径或直接填写的上游目标全路径", example = "http://backend-service:8080/users")
    private String upstreamUrl;

    @Schema(description = "超时时间(毫秒)", example = "3000")
    private Integer timeoutMs;

}
