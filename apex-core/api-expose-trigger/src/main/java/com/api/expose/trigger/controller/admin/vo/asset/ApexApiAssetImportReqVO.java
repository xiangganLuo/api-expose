package com.api.expose.trigger.controller.admin.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx API 资产导入 Request VO")
@Data
public class ApexApiAssetImportReqVO {

    @Schema(description = "分组名称/标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "petstore")
    @NotBlank(message = "groupName 不能为空")
    private String groupName;

    @Schema(description = "导入来源类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "OPENAPI_CONTENT")
    @NotNull(message = "sourceType 不能为空")
    private SourceType sourceType;

    @Schema(description = "OpenAPI 文本内容")
    private String fileContent;

    @Schema(description = "OpenAPI URL")
    private String sourceUrl;

    @Schema(description = "是否覆盖同名资产", example = "false")
    private Boolean override;

    public enum SourceType {
        OPENAPI_CONTENT,
        OPENAPI_URL
    }

}

