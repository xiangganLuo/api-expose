package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.api.adapter.repository.IApiAssetEnvRepository;
import com.api.expose.domain.api.model.entity.ApiAssetEnvEntity;
import com.api.expose.infrastructure.dao.IApiAssetEnvDao;
import com.api.expose.infrastructure.dao.po.ApiAssetEnvPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产环境配置仓储实现
 */
@Repository
public class ApiAssetEnvRepository implements IApiAssetEnvRepository {

    @Resource
    private IApiAssetEnvDao apiAssetEnvDao;

    @Override
    public void save(ApiAssetEnvEntity entity) {
        ApiAssetEnvPO po = ApiAssetEnvPO.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .assetId(entity.getAssetId())
                .envCode(entity.getEnvCode())
                .envName(entity.getEnvName())
                .baseUrl(entity.getBaseUrl())
                .status(entity.getStatus())
                .build();
        
        if (po.getId() == null) {
            apiAssetEnvDao.insert(po);
        } else {
            apiAssetEnvDao.updateById(po);
        }
    }

    @Override
    public List<ApiAssetEnvEntity> queryListByAssetId(Long assetId) {
        List<ApiAssetEnvPO> list = apiAssetEnvDao.selectList(new LambdaQueryWrapper<ApiAssetEnvPO>()
                .eq(ApiAssetEnvPO::getAssetId, assetId));
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public String queryBaseUrl(Long assetId, String envCode) {
        ApiAssetEnvPO po = apiAssetEnvDao.selectOne(new LambdaQueryWrapper<ApiAssetEnvPO>()
                .eq(ApiAssetEnvPO::getAssetId, assetId)
                .eq(ApiAssetEnvPO::getEnvCode, envCode)
                .eq(ApiAssetEnvPO::getStatus, 0));
        return po != null ? po.getBaseUrl() : null;
    }

    @Override
    public void removeById(Long id) {
        apiAssetEnvDao.deleteById(id);
    }

    private ApiAssetEnvEntity convert(ApiAssetEnvPO po) {
        return ApiAssetEnvEntity.builder()
                .id(po.getId())
                .assetId(po.getAssetId())
                .tenantId(po.getTenantId())
                .envCode(po.getEnvCode())
                .envName(po.getEnvName())
                .baseUrl(po.getBaseUrl())
                .status(po.getStatus())
                .build();
    }
}
