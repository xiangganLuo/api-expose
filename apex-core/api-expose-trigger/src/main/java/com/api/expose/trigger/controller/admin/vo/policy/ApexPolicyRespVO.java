package com.api.expose.trigger.controller.admin.vo.policy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 治理策略 Response VO")
@Data
public class ApexPolicyRespVO {

    @Schema(description = "策略编号", example = "1")
    private Long id;

    @Schema(description = "策略名称")
    private String policyName;

    @Schema(description = "生效范围")
    private String scope;

    @Schema(description = "API 资产编号")
    private Long apiAssetId;

    @Schema(description = "应用编号")
    private Long appId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "限流规则 JSON")
    private String rateLimitJson;

    @Schema(description = "熔断规则 JSON")
    private String circuitBreakerJson;

    @Schema(description = "访问控制规则 JSON")
    private String accessControlJson;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

