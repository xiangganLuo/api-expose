package com.api.expose.trigger.controller.admin.vo.upstream;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "管理后台 - 上游服务保存 Request VO")
@Data
public class ApexUpstreamSaveReqVO {

    @Schema(description = "上游ID", example = "1")
    private Long id;

    @Schema(description = "上游名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户服务")
    @NotBlank(message = "上游名称不能为空")
    private String name;

    @Schema(description = "描述", example = "提供基础用户管理接口")
    private String description;

    @Schema(description = "环境配置列表")
    private List<ApexUpstreamConfigVO> configs;
}
