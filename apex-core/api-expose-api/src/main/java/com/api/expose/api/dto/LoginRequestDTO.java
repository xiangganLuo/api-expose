package com.api.expose.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO implements Serializable {
    private String username;
    private String password;
}
