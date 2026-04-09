package com.api.expose.trigger.controller.admin.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 应用创建 Request VO")
@Data
public class ApexAppCreateReqVO {

    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "demo-app")
    @NotBlank(message = "name 不能为空")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "回调地址")
    private String callbackUrl;

    @Schema(description = "状态", example = "active")
    private String status;

}

