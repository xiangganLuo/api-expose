package com.api.expose.system.api.user.dto;

import lombok.Data;

/**
 * Admin 用户 Response DTO
 *
 */
@Data
public class BindStaffCodeReqDTO {

    /**
     * 用户ID
     */
    private Long id;

    private String staffCode;

}
