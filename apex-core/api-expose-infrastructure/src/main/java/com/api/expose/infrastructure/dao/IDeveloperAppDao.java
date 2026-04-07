package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.DeveloperAppPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 开发者应用数据访问对象
 */
@Mapper
public interface IDeveloperAppDao extends BaseMapper<DeveloperAppPO> {
}
