package com.api.expose.domain.api.adapter.repository;

import com.api.expose.domain.api.model.entity.ApiAssetEnvEntity;
import java.util.List;

/**
 * 资产环境配置仓储接口
 */
public interface IApiAssetEnvRepository {

    /**
     * 保存环境配置
     */
    void save(ApiAssetEnvEntity entity);

    /**
     * 根据资产ID查询所有环境配置
     */
    List<ApiAssetEnvEntity> queryListByAssetId(Long assetId);

    /**
     * 根据资产ID和环境代码获取基础路径
     */
    String queryBaseUrl(Long assetId, String envCode);

    /**
     * 删除环境配置
     */
    void removeById(Long id);
}
