package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.GovernancePolicyPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/*
 * @author xiangganluo
 */

@Mapper
public interface IGovernancePolicyDao extends BaseMapper<GovernancePolicyPO> {
}

