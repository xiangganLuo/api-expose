package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.ApiAssetPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * API 资产数据访问对象
 */
@Mapper
public interface IApiAssetDao extends BaseMapper<ApiAssetPO> {

    /**
     * 根据租户 ID 查询列表
     */
    List<ApiAssetPO> queryListByTenantId(String tenantId);

}
