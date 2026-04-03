package com.api.expose.domain.policy.adapter.repository;

import com.api.expose.domain.policy.model.aggregate.GovernancePolicyAggregate;

import java.util.List;

/**
 * 策略仓储接口
 */
public interface IPolicyRepository {

    /**
     * 保存策略
     */
    void savePolicy(GovernancePolicyAggregate aggregate);

    /**
     * 根据策略ID查询
     */
    GovernancePolicyAggregate queryPolicyById(Long policyId);

    /**
     * 根据 API 资产ID 查询生效策略列表
     */
    List<GovernancePolicyAggregate> queryPoliciesByApiAssetId(Long apiAssetId);

    /**
     * 根据应用ID查询生效策略列表
     */
    List<GovernancePolicyAggregate> queryPoliciesByAppId(Long appId);

    /**
     * 更新策略
     */
    void updatePolicy(GovernancePolicyAggregate aggregate);
}
