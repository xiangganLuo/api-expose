package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.RouteRulePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网关路由数据访问对象
 */
@Mapper
public interface IRouteRuleDao extends BaseMapper<RouteRulePO> {
}
