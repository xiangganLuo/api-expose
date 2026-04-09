package com.api.expose.trigger.controller.admin.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx API 资产 Response VO")
@Data
public class ApexApiAssetRespVO {

    @Schema(description = "资产编号", example = "1")
    private Long id;

    @Schema(description = "资产名称", example = "petstore")
    private String name;

    @Schema(description = "资产描述")
    private String description;

    @Schema(description = "分组名称", example = "petstore")
    private String groupName;

    @Schema(description = "协议类型", example = "HTTP")
    private String protocolType;

    @Schema(description = "状态", example = "draft")
    private String status;

    @Schema(description = "基础路径", example = "/v1")
    private String basePath;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

