package com.api.expose.domain.api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 导入命令实体 - 封装导入 Swagger/OpenAPI 文件时的请求数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiImportCommandEntity {

    /** 租户ID */
    private String tenantId;
    /** 导入来源类型: SWAGGER_FILE / OPENAPI_URL / MANUAL */
    private String sourceType;
    /** 文件内容（JSON/YAML） */
    private String fileContent;
    /** 远程文件URL */
    private String sourceUrl;
    /** API 分组名称 */
    private String groupName;
}
