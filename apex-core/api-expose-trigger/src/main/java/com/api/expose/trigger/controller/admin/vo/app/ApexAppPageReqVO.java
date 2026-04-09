package com.api.expose.trigger.controller.admin.vo.app;

import com.api.expose.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 应用分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApexAppPageReqVO extends PageParam {

    @Schema(description = "关键词(名称)", example = "demo")
    private String keywords;

    @Schema(description = "状态", example = "active")
    private String status;

}

