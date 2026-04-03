package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.SubscriptionPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订阅关系数据访问对象
 */
@Mapper
public interface ISubscriptionDao extends BaseMapper<SubscriptionPO> {
}
