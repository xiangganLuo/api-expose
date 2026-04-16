package com.api.expose.trigger.controller.admin.vo.route;

import com.api.expose.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - APEx 网关路由分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApexRouteRulePageReqVO extends PageParam {

    @Schema(description = "关键词 (网关路径/上游地址)", example = "/api/v1")
    private String keywords;

}
