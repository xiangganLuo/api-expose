package com.api.expose.domain.gateway.service.impl;

import com.api.expose.domain.app.adapter.repository.IAppRepository;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.AppStatusEnum;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import com.api.expose.domain.gateway.adapter.port.IForwardPort;
import com.api.expose.domain.gateway.adapter.repository.IGatewayRoutingRepository;
import com.api.expose.domain.gateway.model.entity.RouteRuleEntity;
import com.api.expose.domain.gateway.service.IGatewayService;
import com.api.expose.domain.metering.adapter.repository.IApiCallRecordRepository;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;
import com.api.expose.domain.policy.service.IPolicyService;
import com.api.expose.types.enums.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.api.expose.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 核心网关代理服务实现
 */
@Slf4j
@Service
public class GatewayServiceImpl implements IGatewayService {

    @Resource
    private IAppRepository appRepository;
    @Resource
    private IGatewayRoutingRepository gatewayRoutingRepository;
    @Resource
    private IPolicyService policyService;
    @Resource
    private IForwardPort forwardPort;
    @Resource
    private IApiCallRecordRepository apiCallRecordRepository;

    @Override
    public Mono<ResponseEntity<byte[]>> execute(String apiKey, String path, String queryString, HttpMethod method, Map<String, String> headers, Object body) {
        long start = System.currentTimeMillis();
        log.info("网关收到请求: {} [{}] apiKey: {}", path, method, apiKey);

        // 1. 鉴权
        DeveloperAppEntity app = appRepository.queryAppByApiKey(apiKey);
        if (app == null || app.getStatus() != AppStatusEnum.ACTIVE) {
            throw exception(ErrorCodeConstants.API_KEY_INVALID);
        }

        // 2. 查找路由规则
        RouteRuleEntity rule = gatewayRoutingRepository.findByPath(path, method.name());
        if (rule == null) {
            throw exception(ErrorCodeConstants.ROUTE_NOT_FOUND);
        }

        // 2.5 订阅权限校验
        List<SubscriptionEntity> subscriptions = appRepository.querySubscriptionsByAppId(app.getAppId());
        boolean hasPermission = subscriptions.stream().anyMatch(sub ->
                sub.getApiAssetId().equals(rule.getApiAssetId()) &&
                SubscriptionStatusEnum.APPROVED.equals(sub.getStatus())
        );
        if (!hasPermission) {
            throw exception(ErrorCodeConstants.SUBSCRIPTION_FORBIDDEN);
        }

        // 3. 执行流量治理策略 (动态执行策略链：黑名单、限流、熔断)
        String clientIp = headers.getOrDefault("X-Real-IP", "127.0.0.1");
        if (clientIp == null || "127.0.0.1".equals(clientIp)) {
             clientIp = headers.getOrDefault("Host", "unknown");
        }
        policyService.checkGovernance(app.getAppId(), apiKey, rule.getApiAssetId(), clientIp);

        // 4. 执行异步 HTTP 转发
        String targetUrl = rule.getUpstreamUrl();
        if (queryString != null && !queryString.isEmpty()) {
            targetUrl += (targetUrl.contains("?") ? "&" : "?") + queryString;
        }
        
        return forwardPort.forward(targetUrl, method, headers, body)
                .doOnNext(response -> {
                    long latency = System.currentTimeMillis() - start;
                    log.info("转发成功, 状态码: {}, 耗时: {}ms", response.getStatusCode(), latency);
                    
                    // 5. 记录调用流水 (使用实体)
                    try {
                        ApiCallRecordEntity record = ApiCallRecordEntity.builder()
                                .tenantId(app.getTenantId())
                                .apiAssetId(rule.getApiAssetId())
                                .appId(app.getAppId())
                                .requestPath(path)
                                .httpMethod(method.name())
                                .responseCode(response.getStatusCodeValue())
                                .latencyMs(latency)
                                .callTime(new Date())
                                .build();
                        apiCallRecordRepository.saveRecord(record);
                    } catch (Exception e) {
                        log.error("保存调用记录失败", e);
                    }
                });
    }
}
