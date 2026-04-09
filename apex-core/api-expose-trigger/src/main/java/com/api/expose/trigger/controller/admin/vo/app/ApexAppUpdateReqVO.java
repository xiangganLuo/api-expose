package com.api.expose.trigger.controller.admin.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 应用更新 Request VO")
@Data
public class ApexAppUpdateReqVO {

    @Schema(description = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "id 不能为空")
    private Long id;

    @Schema(description = "应用名称", example = "demo-app")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "回调地址")
    private String callbackUrl;

    @Schema(description = "状态", example = "active")
    private String status;

}

