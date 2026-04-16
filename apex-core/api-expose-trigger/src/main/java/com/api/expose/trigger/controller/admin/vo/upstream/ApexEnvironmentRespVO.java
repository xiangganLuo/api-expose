package com.api.expose.trigger.controller.admin.vo.upstream;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 运行环境 Response VO")
@Data
public class ApexEnvironmentRespVO {

    @Schema(description = "环境ID", example = "1")
    private Long id;

    @Schema(description = "环境标识", example = "test")
    private String code;

    @Schema(description = "环境名称", example = "测试环境")
    private String name;

    @Schema(description = "排序", example = "10")
    private Integer sort;

    @Schema(description = "状态 (0-开启, 1-关闭)", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
