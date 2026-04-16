package com.api.expose.trigger.controller.admin.vo.upstream;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 上游服务 Response VO")
@Data
public class ApexUpstreamRespVO {

    @Schema(description = "上游ID", example = "1")
    private Long id;

    @Schema(description = "上游名称", example = "用户服务")
    private String name;

    @Schema(description = "描述", example = "提供基础用户管理接口")
    private String description;

    @Schema(description = "环境配置列表")
    private List<ApexUpstreamConfigVO> configs;
}
