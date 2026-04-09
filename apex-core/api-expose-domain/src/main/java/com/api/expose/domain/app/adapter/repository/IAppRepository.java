package com.api.expose.domain.app.adapter.repository;

import com.api.expose.domain.app.model.aggregate.DeveloperAppAggregate;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import com.api.expose.framework.common.pojo.PageParam;
import com.api.expose.framework.common.pojo.PageResult;

import java.util.List;

/**
 * 开发者应用仓储接口
 */
public interface IAppRepository {

    /**
     * 保存应用信息
     */
    void saveApp(DeveloperAppEntity appEntity);

    /**
     * 查询应用列表
     */
    List<DeveloperAppEntity> queryApps();

    /**
     * 应用分页
     */
    PageResult<DeveloperAppEntity> pageApps(String keywords, String status, PageParam pageParam);

    /**
     * 更新应用
     */
    void updateApp(DeveloperAppEntity appEntity);

    /**
     * 删除应用
     */
    void deleteApp(Long appId);

    /**
     * 保存/更新订阅信息
     */
    Long saveSubscription(SubscriptionEntity subscriptionEntity);

    /**
     * 更新订阅状态
     */
    void updateSubscriptionStatus(Long subscriptionId, SubscriptionStatusEnum status, String remark);

    /**
     * 订阅分页
     */
    PageResult<SubscriptionEntity> getSubscriptionPage(Long appId, Long apiAssetId, String status, PageParam pageParam);

    /**
     * 查询应用的全部订阅
     */
    List<SubscriptionEntity> querySubscriptionsByAppId(Long appId);

    /**
     * 查询单个应用聚合详情
     */
    DeveloperAppAggregate queryAppAggregate(Long appId);

    /**
     * 根据 ApiKey 查询应用
     */
    DeveloperAppEntity queryAppByApiKey(String apiKey);

    /**
     * 根据订阅 ID 查询应用聚合
     */
    DeveloperAppAggregate queryAppAggregateBySubscriptionId(Long subscriptionId);
}
