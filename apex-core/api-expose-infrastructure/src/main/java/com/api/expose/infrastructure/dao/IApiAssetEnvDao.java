package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.ApiAssetEnvPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产环境配置数据访问对象
 */
@Mapper
public interface IApiAssetEnvDao extends BaseMapper<ApiAssetEnvPO> {
}
