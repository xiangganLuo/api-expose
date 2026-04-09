package com.api.expose.trigger.controller.admin.vo.asset;

import com.api.expose.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx API 资产分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApexApiAssetPageReqVO extends PageParam {

    @Schema(description = "关键词(名称/分组)", example = "pet")
    private String keywords;

    @Schema(description = "状态", example = "draft")
    private String status;

    @Schema(description = "分组名称", example = "petstore")
    private String groupName;

}

