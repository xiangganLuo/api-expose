package com.api.expose.system.api.dept.dto;

import com.api.expose.framework.common.enums.CommonStatusEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Set;

/**
 * 部门 Response DTO
 *
 */
@Data
public class DeptRespDTO {

    /**
     * 部门编号
     */
    private Long id;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 父部门编号
     */
    private Long parentId;
    /**
     * 负责人的用户编号集合
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<Long> leaderUserIds;
    /**
     * 部门状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
