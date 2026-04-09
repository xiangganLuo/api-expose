package com.api.expose.infrastructure.dao.po;

import com.api.expose.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * @author xiangganluo
 */

/**
 * 订阅关系持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_subscription")
@EqualsAndHashCode(callSuper = true)
public class SubscriptionPO extends TenantBaseDO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;
    /** 应用ID */
    private Long appId;
    /** API资产ID */
    private Long apiAssetId;
    /** 订阅状态 (PENDING/APPROVED/REJECTED/REVOKED) */
    private String status;
    /** 申请时间 */
    private java.time.LocalDateTime applyTime;
    /** 通过时间 */
    private java.time.LocalDateTime approveTime;
    /** 备注 */
    private String remark;
}
