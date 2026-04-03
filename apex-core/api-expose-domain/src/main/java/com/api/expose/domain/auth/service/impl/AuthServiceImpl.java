package com.api.expose.domain.auth.service.impl;

import com.api.expose.domain.auth.adapter.repository.IUserRepository;
import com.api.expose.domain.auth.model.entity.UserEntity;
import com.api.expose.domain.auth.service.IAuthService;
import com.api.expose.types.enums.ResponseCode;
import com.api.expose.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Resource
    private IUserRepository userRepository;

    @Override
    public void register(UserEntity user) {
        log.info("正在注册用户: {}", user.getUsername());
        
        // 1. 检查账号是否已存在
        UserEntity existing = userRepository.findByUsername(user.getUsername());
        if (existing != null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "账号已存在");
        }

        // 2. 加密密码
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        
        // 3. 设置默认状态与角色 (如果未指定)
        if (user.getRole() == null) user.setRole("DEVELOPER");
        if (user.getStatus() == null) user.setStatus("NORMAL");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 4. 保存
        userRepository.save(user);
    }

    @Override
    public UserEntity login(String username, String password) {
        log.info("用户尝试登录: {}", username);
        
        // 1. 查询用户
        UserEntity user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(DigestUtils.sha256Hex(password))) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "用户名或密码错误");
        }

        // 2. 检查状态
        if (!"NORMAL".equals(user.getStatus())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "账号被禁用");
        }

        // 3. 执行 Sa-Token 登录逻辑
        // 登录 ID 规则: tenantId:role:userId
        String loginId = String.format("%s:%s:%d", user.getTenantId(), user.getRole(), user.getId());

        log.info("用户登录成功: {}", loginId);
        return user;
    }
}
