package com.api.expose.trigger.controller.admin.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx API 资产更新 Request VO")
@Data
public class ApexApiAssetUpdateReqVO {

    @Schema(description = "资产编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "id 不能为空")
    private Long id;

    @Schema(description = "资产名称", example = "petstore")
    private String name;

    @Schema(description = "资产描述")
    private String description;

    @Schema(description = "分组名称", example = "petstore")
    private String groupName;

    @Schema(description = "基础路径", example = "/v1")
    private String basePath;

    @Schema(description = "状态", example = "draft")
    private String status;

}

