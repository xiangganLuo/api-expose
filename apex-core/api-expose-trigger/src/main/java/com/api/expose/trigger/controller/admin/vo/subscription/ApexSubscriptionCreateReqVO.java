package com.api.expose.trigger.controller.admin.vo.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 订阅创建 Request VO")
@Data
public class ApexSubscriptionCreateReqVO {

    @Schema(description = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "appId 不能为空")
    private Long appId;

    @Schema(description = "API 资产编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "apiAssetId 不能为空")
    private Long apiAssetId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态(pending/approved)", example = "pending")
    private String status;

}

