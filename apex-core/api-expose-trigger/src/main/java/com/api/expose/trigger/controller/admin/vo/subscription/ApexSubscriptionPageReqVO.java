package com.api.expose.trigger.controller.admin.vo.subscription;

import com.api.expose.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 订阅分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApexSubscriptionPageReqVO extends PageParam {

    @Schema(description = "应用编号", example = "1")
    private Long appId;

    @Schema(description = "API 资产编号", example = "1")
    private Long apiAssetId;

    @Schema(description = "状态", example = "pending")
    private String status;

}

