package com.api.expose.domain.auth.adapter.repository;

import com.api.expose.domain.auth.model.entity.UserEntity;

/**
 * 用户仓储接口
 */
public interface IUserRepository {

    void save(UserEntity user);

    UserEntity findByUsername(String username);
}
