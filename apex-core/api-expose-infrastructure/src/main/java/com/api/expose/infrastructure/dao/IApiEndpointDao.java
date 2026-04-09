package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.ApiEndpointPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API 端点数据访问对象
 */
@Mapper
public interface IApiEndpointDao extends BaseMapper<ApiEndpointPO> {

}
