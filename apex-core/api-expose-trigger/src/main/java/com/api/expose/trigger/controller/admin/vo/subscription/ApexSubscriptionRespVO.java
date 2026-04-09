package com.api.expose.trigger.controller.admin.vo.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 订阅 Response VO")
@Data
public class ApexSubscriptionRespVO {

    @Schema(description = "订阅编号", example = "1")
    private Long id;

    @Schema(description = "应用编号", example = "1")
    private Long appId;

    @Schema(description = "API 资产编号", example = "1")
    private Long apiAssetId;

    @Schema(description = "状态", example = "pending")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

