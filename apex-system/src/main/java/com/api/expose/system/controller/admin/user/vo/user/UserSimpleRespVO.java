package com.api.expose.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Schema(description = "管理后台 - 用户精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "员工编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String staffCode;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "APEX")
    private String nickname;

    @Schema(description = "部门编号", example = "我是一个用户")
    private Long deptId;
    @Schema(description = "多部门编号", example = "我是一个用户")
    private LinkedHashSet<Long> deptIds;
    @Schema(description = "部门名称", example = "IT 部")
    private String deptName;

}
