package com.api.expose.system.service.permission;

import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.system.controller.admin.permission.vo.DataPermissionConfigPageReqVO;
import com.api.expose.system.controller.admin.permission.vo.DataPermissionConfigSaveReqVO;
import com.api.expose.system.dal.dataobject.permission.DataPermissionConfigDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据权限配置 Service 接口
 *
 * @author 系统管理员
 */
public interface DataPermissionConfigService {

    /**
     * 检查当前用户是否需要权限限制
     *
     * @param configKey 配置键
     * @return 是否需要限制
     */
    boolean needPermissionFilter(String configKey);

    /**
     * 获取权限配置
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    DataPermissionConfigDO getConfig(String configKey);

    /**
     * 创建数据权限配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDataPermissionConfig(@Valid DataPermissionConfigSaveReqVO createReqVO);

    /**
     * 更新数据权限配置
     *
     * @param updateReqVO 更新信息
     */
    void updateDataPermissionConfig(@Valid DataPermissionConfigSaveReqVO updateReqVO);

    /**
     * 删除数据权限配置
     *
     * @param id 编号
     */
    void deleteDataPermissionConfig(Long id);

    /**
     * 获得数据权限配置
     *
     * @param id 编号
     * @return 数据权限配置
     */
    DataPermissionConfigDO getDataPermissionConfig(Long id);

    /**
     * 获得数据权限配置分页
     *
     * @param pageReqVO 分页查询
     * @return 数据权限配置分页
     */
    PageResult<DataPermissionConfigDO> getDataPermissionConfigPage(DataPermissionConfigPageReqVO pageReqVO);

    /**
     * 获得数据权限配置列表
     *
     * @return 数据权限配置列表
     */
    List<DataPermissionConfigDO> getDataPermissionConfigList();

    /**
     * 验证配置键是否唯一
     *
     * @param id 当前配置ID（新增时为null）
     * @param configKey 配置键
     */
    void validateConfigKeyUnique(Long id, String configKey);

}