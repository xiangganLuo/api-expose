package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.auth.adapter.repository.IUserRepository;
import com.api.expose.domain.auth.model.entity.UserEntity;
import org.springframework.stereotype.Repository;

/*
 * @author xiangganluo
 */
@Repository
public class UserRepository implements IUserRepository {
    @Override
    public void save(UserEntity user) {
        throw new UnsupportedOperationException("Core 模块已移除 User 持久化实现，请在新模块提供实现。");
    }

    @Override
    public UserEntity findByUsername(String username) {
        throw new UnsupportedOperationException("Core 模块已移除 User 持久化实现，请在新模块提供实现。");
    }
}
