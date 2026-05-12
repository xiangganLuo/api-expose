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
import com.api.expose.domain.policy.strategy.impl.DefaultCircuitBreakerStrategy;
import com.api.expose.types.enums.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    
    @Resource
    private DefaultCircuitBreakerStrategy circuitBreakerStrategy;

    @Override
    public Mono<ResponseEntity<byte[]>> execute(String apiKey, String path, String queryString, HttpMethod method, Map<String, String> headers, Object body) {
        long start = System.currentTimeMillis();
        log.debug("网关收到请求: {} [{}] apiKey: {}", path, method, apiKey);

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
        String clientIp = getClientIp(headers);
        policyService.checkGovernance(app.getAppId(), apiKey, rule.getApiAssetId(), clientIp);

        // 4. 执行异步 HTTP 转发
        String baseUrl = rule.getUpstreamUrl();
        final String targetUrl = (queryString != null && !queryString.isEmpty()) 
                ? baseUrl + (baseUrl.contains("?") ? "&" : "?") + queryString
                : baseUrl;
        
        // 构建熔断器 key
        String circuitBreakerKey = "api:" + rule.getApiAssetId() + ":app:" + app.getAppId();
        
        return forwardPort.forward(targetUrl, method, headers, body)
                .doOnNext(response -> {
                    long latency = System.currentTimeMillis() - start;
                    log.debug("转发成功, 状态码: {}, 耗时: {}ms", response.getStatusCode(), latency);
                    
                    // 记录熔断器成功
                    if (response.getStatusCode().is2xxSuccessful()) {
                        circuitBreakerStrategy.recordSuccess(circuitBreakerKey);
                    } else {
                        // 非 2xx 状态码也视为失败
                        circuitBreakerStrategy.recordFailure(circuitBreakerKey);
                    }
                    
                    // 5. 异步记录调用流水 (避免阻塞响应式流)
                    ApiCallRecordEntity record = ApiCallRecordEntity.builder()
                            .tenantId(app.getTenantId())
                            .apiAssetId(rule.getApiAssetId())
                            .appId(app.getAppId())
                            .requestPath(path)
                            .httpMethod(method.name())
                            .responseCode(response.getStatusCodeValue())
                            .latencyMs(latency)
                            .callerIp(clientIp)
                            .callTime(new Date())
                            .build();
                    
                    Mono.fromRunnable(() -> apiCallRecordRepository.saveRecord(record))
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe(
                                    null,
                                    error -> log.error("保存调用记录失败", error)
                            );
                })
                .doOnError(error -> {
                    // 记录熔断器失败
                    circuitBreakerStrategy.recordFailure(circuitBreakerKey);
                    log.error("网关转发异常: {}", targetUrl, error);
                });
    }
    
    /**
     * 获取客户端真实 IP
     * 优先级: X-Real-IP > X-Forwarded-For > Remote-Addr
     */
    private String getClientIp(Map<String, String> headers) {
        // 1. 尝试从 X-Real-IP 获取
        String ip = headers.get("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        // 2. 尝试从 X-Forwarded-For 获取（可能包含多个 IP，取第一个）
        ip = headers.get("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 格式: client, proxy1, proxy2
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index).trim();
            }
            return ip.trim();
        }
        
        // 3. 尝试从 Remote-Addr 获取
        ip = headers.get("Remote-Addr");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        // 4. 默认返回 127.0.0.1
        return "127.0.0.1";
    }
}
