package com.api.expose.infrastructure.dao;

import com.api.expose.infrastructure.dao.po.ApiVersionPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * API 版本数据访问对象
 */
@Mapper
public interface IApiVersionDao extends BaseMapper<ApiVersionPO> {

    /**
     * 根据资产 ID 查询
     */
    List<ApiVersionPO> queryByAssetId(Long assetId);

    /**
     * 重置活跃状态
     */
    void resetActiveByAssetId(Long assetId);
}
