package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.MeteringStatPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 计量统计数据访问对象
 */
@Mapper
public interface IMeteringStatDao extends BaseMapper<MeteringStatPO> {
}
