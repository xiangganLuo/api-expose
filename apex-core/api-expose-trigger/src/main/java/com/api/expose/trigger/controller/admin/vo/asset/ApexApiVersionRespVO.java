package com.api.expose.trigger.controller.admin.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx API 版本 Response VO")
@Data
public class ApexApiVersionRespVO {

    @Schema(description = "版本编号", example = "1")
    private Long id;

    @Schema(description = "资产编号", example = "1")
    private Long assetId;

    @Schema(description = "版本号", example = "v1.0.0")
    private String version;

    @Schema(description = "是否当前生效版本 (1-是, 0-否)", example = "1")
    private Integer active;

    @Schema(description = "发布日志", example = "初始版本发布")
    private String releaseNote;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
