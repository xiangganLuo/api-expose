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
    DeveloperAppEntity createApp(DeveloperAppEntity appEntity);

    /**
     * 查询应用列表
     */
    List<DeveloperAppEntity> queryApps();

    /**
     * 应用分页
     */
    com.api.expose.framework.common.pojo.PageResult<DeveloperAppEntity> pageApps(String keywords, String status,
                                                                                 com.api.expose.framework.common.pojo.PageParam pageParam);

    /**
     * 更新应用
     */
    void updateApp(DeveloperAppEntity appEntity);

    /**
     * 删除应用
     */
    void deleteApp(Long appId);

    /**
     * 申请 API 订阅
     */
    Long applySubscription(SubscriptionEntity subscriptionEntity);

    /**
     * 审批订阅
     */
    void auditSubscription(Long subscriptionId, SubscriptionStatusEnum status, String remark);

    /**
     * 分页查询订阅
     */
    com.api.expose.framework.common.pojo.PageResult<SubscriptionEntity> getSubscriptionPage(Long appId, Long apiAssetId, String status,
                                                                                           com.api.expose.framework.common.pojo.PageParam pageParam);

    /**
     * 查询应用详情（包含订阅）
     */
    DeveloperAppAggregate queryAppDetail(Long appId);
}
