package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.ApiVersionPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API 版本数据访问对象
 */
@Mapper
public interface IApiVersionDao extends BaseMapper<ApiVersionPO> {

}
