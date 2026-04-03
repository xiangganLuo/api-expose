package com.api.expose.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import com.api.expose.framework.common.enums.CommonStatusEnum;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.framework.security.core.util.SecurityFrameworkUtils;
import com.api.expose.system.controller.admin.permission.vo.DataPermissionConfigPageReqVO;
import com.api.expose.system.controller.admin.permission.vo.DataPermissionConfigSaveReqVO;
import com.api.expose.system.convert.permission.DataPermissionConfigConvert;
import com.api.expose.system.dal.dataobject.permission.DataPermissionConfigDO;
import com.api.expose.system.dal.mysql.permission.SystemDataPermissionMapper;
import com.api.expose.system.enums.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static com.api.expose.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 数据权限配置 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class DataPermissionConfigServiceImpl implements DataPermissionConfigService {

    @Resource
    private SystemDataPermissionMapper systemDataPermissionMapper;

    @Resource
    private PermissionService permissionService;

    @Override
    public boolean needPermissionFilter(String configKey) {
        // 获取配置
        DataPermissionConfigDO config = getConfig(configKey);
        if (config == null || !CommonStatusEnum.isEnable(config.getStatus())) {
            return false;
        }

        // 获取当前用户角色ID集合
        Set<Long> userRoleIds = getCurrentUserRoleIds();
        if (CollUtil.isEmpty(userRoleIds) || CollUtil.isEmpty(config.getRoleIds())) {
            return false;
        }

        // 判断用户角色是否在不受限制的角色列表中（特权角色）
        boolean hasPrivilegeRole = config.getRoleIds().stream().anyMatch(userRoleIds::contains);
        // 没有特权角色的用户需要过滤，有特权角色的用户不过滤
        return !hasPrivilegeRole;
    }

    @Override
    @Cacheable(value = "system:data-permission-config", key = "#configKey")
    public DataPermissionConfigDO getConfig(String configKey) {
        return systemDataPermissionMapper.selectByConfigKeyAndStatus(configKey, CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    public Long createDataPermissionConfig(DataPermissionConfigSaveReqVO createReqVO) {
        // 校验配置键唯一性
        validateConfigKeyUnique(null, createReqVO.getConfigKey());
        
        // 插入
        DataPermissionConfigDO config = DataPermissionConfigConvert.INSTANCE.convert(createReqVO);
        systemDataPermissionMapper.insert(config);
        // 返回
        return config.getId();
    }

    @Override
    @CacheEvict(value = "system:data-permission-config", key = "#updateReqVO.configKey")
    public void updateDataPermissionConfig(DataPermissionConfigSaveReqVO updateReqVO) {
        // 校验存在
        validateDataPermissionConfigExists(updateReqVO.getId());
        // 校验配置键唯一性
        validateConfigKeyUnique(updateReqVO.getId(), updateReqVO.getConfigKey());
        
        // 更新
        DataPermissionConfigDO updateObj = DataPermissionConfigConvert.INSTANCE.convert(updateReqVO);
        systemDataPermissionMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(value = "system:data-permission-config", allEntries = true)
    public void deleteDataPermissionConfig(Long id) {
        // 校验存在
        validateDataPermissionConfigExists(id);
        // 删除
        systemDataPermissionMapper.deleteById(id);
    }

    private void validateDataPermissionConfigExists(Long id) {
        if (systemDataPermissionMapper.selectById(id) == null) {
            throw exception(ErrorCodeConstants.DATA_PERMISSION_CONFIG_NOT_EXISTS);
        }
    }

    @Override
    public DataPermissionConfigDO getDataPermissionConfig(Long id) {
        return systemDataPermissionMapper.selectById(id);
    }

    @Override
    public PageResult<DataPermissionConfigDO> getDataPermissionConfigPage(DataPermissionConfigPageReqVO pageReqVO) {
        return systemDataPermissionMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DataPermissionConfigDO> getDataPermissionConfigList() {
        return systemDataPermissionMapper.selectList();
    }

    @Override
    public void validateConfigKeyUnique(Long id, String configKey) {
        DataPermissionConfigDO config = systemDataPermissionMapper.selectByConfigKey(configKey);
        if (config == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的数据权限配置
        if (id == null) {
            throw exception(ErrorCodeConstants.DATA_PERMISSION_CONFIG_KEY_DUPLICATE, configKey);
        }
        if (!config.getId().equals(id)) {
            throw exception(ErrorCodeConstants.DATA_PERMISSION_CONFIG_KEY_DUPLICATE, configKey);
        }
    }

    /**
     * 获取当前用户角色ID集合
     */
    private Set<Long> getCurrentUserRoleIds() {
        try {
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            if (userId == null) {
                return CollUtil.newHashSet();
            }

            // 调用权限服务获取用户角色ID集合
            return permissionService.getUserRoleIdListByUserIdFromCache(userId);

        } catch (Exception e) {
            log.error("[getCurrentUserRoleIds][获取用户角色失败]", e);
            return CollUtil.newHashSet();
        }
    }

}
