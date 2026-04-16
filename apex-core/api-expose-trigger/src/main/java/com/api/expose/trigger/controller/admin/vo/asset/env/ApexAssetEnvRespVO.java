package com.api.expose.trigger.controller.admin.vo.asset.env;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - API 资产环境配置 Response VO")
@Data
public class ApexAssetEnvRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "资产编号", example = "1")
    private Long assetId;

    @Schema(description = "环境标识", example = "test")
    private String envCode;

    @Schema(description = "环境名称", example = "测试环境")
    private String envName;

    @Schema(description = "后端基础路径", example = "http://api-test.example.com")
    private String baseUrl;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
