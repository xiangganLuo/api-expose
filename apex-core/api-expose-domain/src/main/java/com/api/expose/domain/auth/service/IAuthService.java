package com.api.expose.domain.auth.service;

import com.api.expose.domain.auth.model.entity.UserEntity;

/**
 * 认证服务接口
 */
public interface IAuthService {

    /**
     * 用户注册
     */
    void register(UserEntity user);

    /**
     * 用户登录
     * @return 登录成功的用户信息
     */
    UserEntity login(String username, String password);
}
