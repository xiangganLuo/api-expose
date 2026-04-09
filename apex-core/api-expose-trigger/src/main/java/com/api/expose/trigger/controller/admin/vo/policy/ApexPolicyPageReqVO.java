package com.api.expose.trigger.controller.admin.vo.policy;

import com.api.expose.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 治理策略分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApexPolicyPageReqVO extends PageParam {

    @Schema(description = "范围", example = "GLOBAL")
    private String scope;

    @Schema(description = "API 资产编号")
    private Long apiAssetId;

    @Schema(description = "应用编号")
    private Long appId;

    @Schema(description = "是否启用")
    private Boolean enabled;

}

