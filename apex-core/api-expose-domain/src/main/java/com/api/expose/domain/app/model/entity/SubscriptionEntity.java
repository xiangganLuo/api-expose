package com.api.expose.domain.app.model.entity;

import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API 订阅关系实体
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionEntity {

    /** 订阅ID */
    private Long id;

    /** 租户ID */
    private Long tenantId;
    /** 应用ID */
    private Long appId;
    /** API 资产ID */
    private Long apiAssetId;
    /** 订阅状态 */
    private SubscriptionStatusEnum status;
    /** 备注 */
    private String remark;
    /** 申请时间(创建时间) */
    private LocalDateTime applyTime;
    /** 通过时间 */
    private LocalDateTime approveTime;

    /** 申请时间别名，兼容 VO */
    public LocalDateTime getCreateTime() {
        return applyTime;
    }

    /**
     * 审核通过
     */
    public void approve() {
        this.status = SubscriptionStatusEnum.APPROVED;
        this.approveTime = LocalDateTime.now();
    }

    /**
     * 驳回
     */
    public void reject(String remark) {
        this.status = SubscriptionStatusEnum.REJECTED;
        this.remark = remark;
    }
}

