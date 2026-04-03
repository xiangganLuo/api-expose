package com.api.expose.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务模块枚举
 * 用于标识不同业务模块对应的数据表
 *
 */
@Getter
@AllArgsConstructor
public enum BusinessModuleEnum {

    /**
     * 招聘需求模块
     */
    RECRUIT_DEMAND("RECRUIT_DEMAND", "招聘需求", "hrm_recruit_demand"),
    
    /**
     * 员工管理模块
     */
    EMPLOYEE("EMPLOYEE", "员工管理", "system_users"),
    
    /**
     * 项目管理模块
     */
    PROJECT("PROJECT", "项目管理", "project_info"),
    
    /**
     * 合同管理模块
     */
    CONTRACT("CONTRACT", "合同管理", "crm_contract"),
    
    /**
     * 客户管理模块
     */
    CUSTOMER("CUSTOMER", "客户管理", "crm_customer");

    /**
     * 模块编码
     */
    private final String code;
    
    /**
     * 模块名称
     */
    private final String name;
    
    /**
     * 对应的数据表名
     */
    private final String tableName;

    /**
     * 根据模块编码获取枚举
     */
    public static BusinessModuleEnum getByCode(String code) {
        for (BusinessModuleEnum moduleEnum : values()) {
            if (moduleEnum.getCode().equals(code)) {
                return moduleEnum;
            }
        }
        throw new IllegalArgumentException("未找到对应的业务模块: " + code);
    }
}
