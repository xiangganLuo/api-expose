package com.api.expose.trigger.controller.admin.vo.policy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/*
 * @author xiangganluo
 */

@Schema(description = "管理后台 - APEx 治理策略更新 Request VO")
@Data
public class ApexPolicyUpdateReqVO extends ApexPolicyCreateReqVO {

    @Schema(description = "策略编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "id 不能为空")
    private Long id;

}

