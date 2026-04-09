package com.api.expose.trigger.controller.admin.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 应用 Response VO")
@Data
public class ApexAppRespVO {

    @Schema(description = "应用编号", example = "1")
    private Long id;

    @Schema(description = "应用名称", example = "demo-app")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态", example = "active")
    private String status;

    @Schema(description = "API Key")
    private String apiKey;

    @Schema(description = "回调地址")
    private String callbackUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

