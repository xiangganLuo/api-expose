package com.api.expose.domain.auth.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户领域实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    private Long id;
    private String tenantId;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String role;
    private String status;
    private Date createTime;
    private Date updateTime;
}
