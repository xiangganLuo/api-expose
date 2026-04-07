package com.api.expose.domain.app.model.aggregate;

import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 开发者应用聚合
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperAppAggregate {

    /** 核心应用实体 */
    private DeveloperAppEntity app;
    /** 该应用的订阅记录列表 */
    private List<SubscriptionEntity> subscriptions;

    /**
     * 为应用新增订阅申请
     */
    public void applyForSubscription(Long apiAssetId, String remark) {
        // 逻辑校验等...
    }
}
