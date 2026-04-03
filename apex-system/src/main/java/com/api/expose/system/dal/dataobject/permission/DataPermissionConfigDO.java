package com.api.expose.system.dal.dataobject.permission;

import com.api.expose.framework.mybatis.core.dataobject.BaseDO;
import com.api.expose.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.util.List;

/**
 * 数据权限配置 DO
 *
 * @author 系统管理员
 */
@TableName(value = "system_data_permission_config", autoResultMap = true)
@KeySequence("system_data_permission_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore // 忽略租户隔离，这是全局配置表
public class DataPermissionConfigDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 配置键(唯一标识)
     */
    private String configKey;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 受限制的角色ID数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> roleIds;

    /**
     * 过滤字段名
     */
    private String filterColumn;

    /**
     * 过滤表名(跨表时使用)
     */
    private String filterTable;

    /**
     * 状态(1=启用 0=禁用)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}
