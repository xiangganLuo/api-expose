package com.api.expose.system.service.permission;

import cn.hutool.core.util.ReflectUtil;
import com.api.expose.framework.security.core.util.SecurityFrameworkUtils;
import com.api.expose.system.dal.dataobject.permission.DataPermissionConfigDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * 简单数据权限帮助类
 * 用于在业务层快速应用数据权限过滤
 *
 * @author 系统管理员
 */
@Component
@Slf4j
public class SimpleDataPermissionHelper {

    @Resource
    private DataPermissionConfigService dataPermissionConfigService;

    /**
     * 为候选人查询应用权限过滤
     *
     * @param pageReqVO 查询请求VO
     */
    public void applyCandidatePermissionFilter(Object pageReqVO) {
        applyPermissionFilter("hrm:candidate:permission", pageReqVO);
    }

    /**
     * 为面试记录查询应用权限过滤
     * 只能查看自己作为面试官的面试记录
     *
     * @param pageReqVO 查询请求VO
     */
    public void applyInterviewPermissionFilter(Object pageReqVO) {
        applyPermissionFilter("hrm:interview:permission", pageReqVO);
    }

    /**
     * 通用权限过滤方法
     *
     * @param configKey   配置键
     * @param queryObject 查询对象
     */
    public <T> void applyPermissionFilter(String configKey, T queryObject) {
        if (queryObject == null) {
            return;
        }

        try {
            // 检查是否需要权限过滤
            if (!dataPermissionConfigService.needPermissionFilter(configKey)) {
                log.debug("[applyPermissionFilter][配置键: {} 不需要权限过滤]", configKey);
                return;
            }

            // 获取配置信息
            DataPermissionConfigDO config = dataPermissionConfigService.getConfig(configKey);
            if (config == null) {
                log.warn("[applyPermissionFilter][配置键: {} 未找到配置]", configKey);
                return;
            }

            // 获取当前用户ID
            Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
            if (currentUserId == null) {
                log.warn("[applyPermissionFilter][当前用户未登录，无法应用权限过滤]");
                return;
            }

            // 设置过滤字段值
            setFieldValue(queryObject, config.getFilterColumn(), currentUserId);
            log.debug("[applyPermissionFilter][已应用权限过滤，配置键: {}, 过滤字段: {}, 用户ID: {}]", 
                    configKey, config.getFilterColumn(), currentUserId);

        } catch (Exception e) {
            log.error("[applyPermissionFilter][应用权限过滤失败，配置键: {}]", configKey, e);
            // 权限过滤失败不应该影响正常业务流程，只记录日志
        }
    }

    /**
     * 使用反射设置对象字段值
     *
     * @param target    目标对象
     * @param fieldName 字段名
     * @param value     字段值
     */
    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = ReflectUtil.getField(target.getClass(), fieldName);
            if (field == null) {
                log.warn("[setFieldValue][字段不存在，对象类型: {}, 字段名: {}]", 
                        target.getClass().getSimpleName(), fieldName);
                return;
            }

            // 设置字段可访问
            field.setAccessible(true);
            field.set(target, value);

        } catch (Exception e) {
            log.error("[setFieldValue][设置字段值失败，字段名: {}, 值: {}]", fieldName, value, e);
        }
    }

}


