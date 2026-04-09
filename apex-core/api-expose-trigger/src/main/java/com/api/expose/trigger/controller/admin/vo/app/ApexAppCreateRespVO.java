package com.api.expose.trigger.controller.admin.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 应用创建 Response VO")
@Data
public class ApexAppCreateRespVO {

    @Schema(description = "应用编号", example = "1")
    private Long appId;

    @Schema(description = "API Key")
    private String apiKey;

    @Schema(description = "API Secret")
    private String apiSecret;

}

