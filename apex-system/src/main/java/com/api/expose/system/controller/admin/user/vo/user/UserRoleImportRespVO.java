package com.api.expose.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleImportRespVO {

    @Schema(description = "成功数量")
    private Integer createSuccessCount;

    @Schema(description = "失败的用户ID列表")
    private List<Long> failureUserIds;
}