package com.api.expose.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 导入请求 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiImportRequestDTO {

    /** 租户ID */
    private String tenantId;
    /** 资源类型 (OPENAPI_CONTENT / OPENAPI_URL) */
    private String sourceType;
    /** 资源内容 (JSON/YAML) */
    private String fileContent;
    /** 资源URL */
    private String sourceUrl;
    /** 分组名称 */
    private String groupName;
}
