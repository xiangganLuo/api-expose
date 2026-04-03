package com.api.expose.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * @author xiangganluo
 */

/**
 * 用户持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_tenant_user")
public class UserPO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 租户ID */
    private String tenantId;
    /** 账号 */
    private String username;
    /** 密码 (加密) */
    private String password;
    /** 姓名 */
    private String nickname;
    /** 邮箱 */
    private String email;
    /** 角色 (ADMIN/DEVELOPER) */
    private String role;
    /** 状态 (NORMAL/DISABLED) */
    private String status;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
