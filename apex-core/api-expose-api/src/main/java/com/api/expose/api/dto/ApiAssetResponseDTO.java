package com.api.expose.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * API 资产响应 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiAssetResponseDTO {

    /** 资产ID */
    private Long assetId;
    /** 名称 */
    private String name;
    /** 分组 */
    private String groupName;
    /** 协议 */
    private String protocolType;
    /** 状态 */
    private String status;
    /** 基础路径 */
    private String basePath;
    /** 创建时间 */
    private Date createTime;
}
