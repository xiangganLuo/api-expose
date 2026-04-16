package com.api.expose.trigger.controller.admin.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx API 资产在线调试 Request VO")
@Data
public class ApexApiTryReqVO {

    @Schema(description = "资产编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "assetId 不能为空")
    private Long assetId;

    @Schema(description = "环境标识", example = "test")
    private String envCode;

    @Schema(description = "关联上游服务ID", example = "1")
    private Long upstreamId;

    @Schema(description = "端点路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "/pets")
    @NotBlank(message = "endpointPath 不能为空")
    private String endpointPath;

    @Schema(description = "HTTP 方法", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @NotBlank(message = "httpMethod 不能为空")
    private String httpMethod;

    @Schema(description = "请求头")
    private Map<String, String> headers;

    @Schema(description = "请求体")
    private String body;

}
