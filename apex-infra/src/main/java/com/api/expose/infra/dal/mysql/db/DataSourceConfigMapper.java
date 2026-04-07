package com.api.expose.infra.dal.mysql.db;

import com.api.expose.framework.mybatis.core.mapper.BaseMapperX;
import com.api.expose.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
