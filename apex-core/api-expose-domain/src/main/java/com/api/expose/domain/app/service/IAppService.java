package com.api.expose.domain.app.service;

import com.api.expose.domain.app.model.aggregate.DeveloperAppAggregate;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;

import java.util.List;

/**
 * 开发者应用管理服务接口
 */
public interface IAppService {

    /**
     * 创建应用
     */
    void createApp(DeveloperAppEntity appEntity);

    /**
     * 查询应用列表
     */
    List<DeveloperAppEntity> queryApps(String tenantId);

    /**
     * 申请 API 订阅
     */
    void applySubscription(SubscriptionEntity subscriptionEntity);

    /**
     * 审批订阅
     */
    void auditSubscription(Long subscriptionId, SubscriptionStatusEnum status, String remark);

    /**
     * 查询应用详情（包含订阅）
     */
    DeveloperAppAggregate queryAppDetail(Long appId);
}
