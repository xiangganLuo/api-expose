package com.api.expose.framework.common.biz.system.permission.dto;

import lombok.Data;

import java.util.LinkedHashSet;

/**
 * 部门的数据权限 Response DTO
 *
 */
@Data
public class DeptDataPermissionRespDTO {

    /**
     * 是否可查看全部数据
     */
    private Boolean all;
    /**
     * 是否可查看自己的数据
     */
    private Boolean self;
    /**
     * 可查看的部门编号数组
     */
    private LinkedHashSet<Long> deptIds;

    public DeptDataPermissionRespDTO() {
        this.all = false;
        this.self = false;
        this.deptIds = new LinkedHashSet<>();
    }

}
