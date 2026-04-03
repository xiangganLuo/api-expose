package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.TenantPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户数据访问对象
 */
@Mapper
public interface ITenantDao extends BaseMapper<TenantPO> {
}
