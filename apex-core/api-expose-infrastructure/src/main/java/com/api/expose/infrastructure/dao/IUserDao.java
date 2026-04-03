package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.UserPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 */
@Mapper
public interface IUserDao extends BaseMapper<UserPO> {
}
