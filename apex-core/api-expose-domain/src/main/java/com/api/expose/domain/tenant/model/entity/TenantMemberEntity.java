package com.api.expose.domain.tenant.model.entity;

import com.api.expose.domain.tenant.model.valobj.MemberRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 租户成员实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantMemberEntity {

    /** 成员ID */
    private Long memberId;
    /** 用户ID */
    private String userId;
    /** 用户名 */
    private String username;
    /** 邮箱 */
    private String email;
    /** 角色 */
    private MemberRoleEnum role;
    /** 加入时间 */
    private Date joinTime;
}
