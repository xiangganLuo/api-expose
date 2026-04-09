package com.api.expose.trigger.controller.admin.vo.policy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 治理策略创建 Request VO")
@Data
public class ApexPolicyCreateReqVO {

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "policyName 不能为空")
    private String policyName;

    @Schema(description = "生效范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "GLOBAL")
    @NotNull(message = "scope 不能为空")
    private String scope;

    @Schema(description = "API 资产编号")
    private Long apiAssetId;

    @Schema(description = "应用编号")
    private Long appId;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "限流规则 JSON")
    private String rateLimitJson;

    @Schema(description = "熔断规则 JSON")
    private String circuitBreakerJson;

    @Schema(description = "访问控制规则 JSON")
    private String accessControlJson;

}

