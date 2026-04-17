package com.api.expose.domain.gateway.service.impl;

import com.api.expose.domain.app.adapter.repository.IAppRepository;
import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.valobj.AppStatusEnum;
import com.api.expose.domain.gateway.adapter.port.IForwardPort;
import com.api.expose.domain.gateway.adapter.repository.IGatewayRoutingRepository;
import com.api.expose.domain.gateway.model.entity.RouteRuleEntity;
import com.api.expose.domain.gateway.service.IGatewayService;
import com.api.expose.domain.metering.adapter.repository.IApiCallRecordRepository;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;
import com.api.expose.domain.policy.service.IPolicyService;
import com.api.expose.types.enums.ResponseCode;
import com.api.expose.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

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
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "无效或已禁用的 ApiKey");
        }

        // 2. 查找路由规则
        RouteRuleEntity rule = gatewayRoutingRepository.findByPath(path, method.name());
        if (rule == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "未找到匹配的路由规则: " + path);
        }

        // 3. 执行流量治理策略 (限流)
        if (!policyService.checkRateLimit(apiKey, rule.getApiAssetId())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "请求过于频繁, 请稍后再试 (Rate Limited)");
        }

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
