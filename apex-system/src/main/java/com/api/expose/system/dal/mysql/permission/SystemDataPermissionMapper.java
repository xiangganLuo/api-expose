package com.api.expose.system.dal.mysql.permission;

import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.framework.mybatis.core.mapper.BaseMapperX;
import com.api.expose.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.api.expose.system.controller.admin.permission.vo.DataPermissionConfigPageReqVO;
import com.api.expose.system.dal.dataobject.permission.DataPermissionConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据权限配置 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface SystemDataPermissionMapper extends BaseMapperX<DataPermissionConfigDO> {

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    default DataPermissionConfigDO selectByConfigKey(String configKey) {
        return selectOne("config_key", configKey);
    }

    /**
     * 根据配置键和状态查询配置
     *
     * @param configKey 配置键
     * @param status 状态
     * @return 配置信息
     */
    default DataPermissionConfigDO selectByConfigKeyAndStatus(String configKey, Integer status) {
        return selectOne("config_key", configKey, "status", status);
    }
    

    /**
     * 分页查询数据权限配置
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    default PageResult<DataPermissionConfigDO> selectPage(DataPermissionConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DataPermissionConfigDO>()
                .likeIfPresent(DataPermissionConfigDO::getConfigKey, reqVO.getConfigKey())
                .likeIfPresent(DataPermissionConfigDO::getConfigName, reqVO.getConfigName())
                .eqIfPresent(DataPermissionConfigDO::getBusinessType, reqVO.getBusinessType())
                .eqIfPresent(DataPermissionConfigDO::getStatus, reqVO.getStatus())
                .orderByDesc(DataPermissionConfigDO::getId));
    }

}