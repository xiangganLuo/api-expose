package com.api.expose.trigger.controller.admin.vo.upstream;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 运行环境保存 Request VO")
@Data
public class ApexEnvironmentSaveReqVO {

    @Schema(description = "环境ID", example = "1")
    private Long id;

    @Schema(description = "环境标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    @NotBlank(message = "环境标识不能为空")
    private String code;

    @Schema(description = "环境名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "测试环境")
    @NotBlank(message = "环境名称不能为空")
    private String name;

    @Schema(description = "排序", example = "10")
    private Integer sort;

    @Schema(description = "状态 (0-开启, 1-关闭)", example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
