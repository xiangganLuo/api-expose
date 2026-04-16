package com.api.expose.trigger.controller.admin.vo.asset.env;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - API 资产环境配置 Save Request VO")
@Data
public class ApexAssetEnvSaveReqVO {

    @Schema(description = "编号 (更新时必填)", example = "1")
    private Long id;

    @Schema(description = "资产编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "assetId 不能为空")
    private Long assetId;

    @Schema(description = "环境标识 (如 test, prod)", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    @NotBlank(message = "envCode 不能为空")
    private String envCode;

    @Schema(description = "环境显示名称 (如 测试环境)")
    private String envName;

    @Schema(description = "后端基础路径 (如 http://api-test.example.com)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "baseUrl 不能为空")
    private String baseUrl;

    @Schema(description = "状态 (0正常 1停用)", example = "0")
    private Integer status;

}
