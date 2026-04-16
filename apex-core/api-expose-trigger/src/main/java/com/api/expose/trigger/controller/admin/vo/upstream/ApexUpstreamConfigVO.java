package com.api.expose.trigger.controller.admin.vo.upstream;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 上游服务环境配置 VO")
@Data
public class ApexUpstreamConfigVO {

    @Schema(description = "环境ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long envId;

    @Schema(description = "环境标识")
    private String envCode;

    @Schema(description = "环境名称")
    private String envName;

    @Schema(description = "基础地址 (Base URL)", requiredMode = Schema.RequiredMode.REQUIRED, example = "http://backend-service:8080")
    private String baseUrl;
}
