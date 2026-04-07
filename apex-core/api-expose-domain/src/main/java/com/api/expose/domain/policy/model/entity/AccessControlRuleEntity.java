package com.api.expose.domain.policy.model.entity;

import com.api.expose.domain.policy.model.valobj.AuthTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 访问控制规则实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessControlRuleEntity {

    /** 规则ID */
    private Long ruleId;
    /** 认证方式 */
    private AuthTypeEnum authType;
    /** IP 白名单 */
    private List<String> ipWhitelist;
    /** 是否启用 IP 白名单 */
    private Boolean ipWhitelistEnabled;
}
