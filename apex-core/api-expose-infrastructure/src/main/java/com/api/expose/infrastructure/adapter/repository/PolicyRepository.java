package com.api.expose.infrastructure.adapter.repository;

import com.api.expose.domain.policy.adapter.repository.IPolicyRepository;
import com.api.expose.domain.policy.model.aggregate.GovernancePolicyAggregate;
import com.api.expose.infrastructure.dao.IGovernancePolicyDao;
import com.api.expose.infrastructure.dao.po.GovernancePolicyPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/*
 * @author xiangganluo
 */

@Repository
public class PolicyRepository implements IPolicyRepository {

    @Resource
    private IGovernancePolicyDao governancePolicyDao;

    @Override
    public void savePolicy(GovernancePolicyAggregate aggregate) {
        if (aggregate == null) return;
        GovernancePolicyPO po = toPO(aggregate);
        governancePolicyDao.insert(po);
        aggregate.setPolicyId(po.getId());
    }

    @Override
    public GovernancePolicyAggregate queryPolicyById(Long policyId) {
        GovernancePolicyPO po = governancePolicyDao.selectById(policyId);
        return po == null ? null : toAggregate(po);
    }

    @Override
    public List<GovernancePolicyAggregate> queryPoliciesByApiAssetId(Long apiAssetId) {
        List<GovernancePolicyPO> list = governancePolicyDao.selectList(
                new LambdaQueryWrapper<GovernancePolicyPO>().eq(GovernancePolicyPO::getApiAssetId, apiAssetId));
        return list.stream().map(this::toAggregate).collect(Collectors.toList());
    }

    @Override
    public List<GovernancePolicyAggregate> queryPoliciesByAppId(Long appId) {
        List<GovernancePolicyPO> list = governancePolicyDao.selectList(
                new LambdaQueryWrapper<GovernancePolicyPO>().eq(GovernancePolicyPO::getAppId, appId));
        return list.stream().map(this::toAggregate).collect(Collectors.toList());
    }

    @Override
    public void updatePolicy(GovernancePolicyAggregate aggregate) {
        if (aggregate == null || aggregate.getPolicyId() == null) return;
        governancePolicyDao.updateById(toPO(aggregate));
    }

    private GovernancePolicyPO toPO(GovernancePolicyAggregate aggregate) {
        return GovernancePolicyPO.builder()
                .id(aggregate.getPolicyId())
                .tenantId(aggregate.getTenantId())
                .policyName(aggregate.getPolicyName())
                .scope(aggregate.getScope() != null ? aggregate.getScope().name() : null)
                .apiAssetId(aggregate.getApiAssetId())
                .appId(aggregate.getAppId())
                .enabled(aggregate.getEnabled())
                .rateLimitJson(aggregate.getRateLimitJson())
                .circuitBreakerJson(aggregate.getCircuitBreakerJson())
                .accessControlJson(aggregate.getAccessControlJson())
                .build();
    }

    private GovernancePolicyAggregate toAggregate(GovernancePolicyPO po) {
        return GovernancePolicyAggregate.builder()
                .policyId(po.getId())
                .tenantId(po.getTenantId())
                .policyName(po.getPolicyName())
                .scope(po.getScope() != null ? com.api.expose.domain.policy.model.valobj.PolicyScopeEnum.valueOf(po.getScope()) : null)
                .apiAssetId(po.getApiAssetId())
                .appId(po.getAppId())
                .enabled(po.getEnabled())
                .rateLimitJson(po.getRateLimitJson())
                .circuitBreakerJson(po.getCircuitBreakerJson())
                .accessControlJson(po.getAccessControlJson())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }
}
