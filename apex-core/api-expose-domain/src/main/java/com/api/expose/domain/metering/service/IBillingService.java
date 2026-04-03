package com.api.expose.domain.metering.service;

/**
 * 计费服务
 */
public interface IBillingService {

    /**
     * 执行计费结算 (按 API 调用次数计费示例)
     * 实际场景为离线 Job 汇总月度/季度账单
     */
    void processBilling();

}
