package com.api.expose.domain.metering.service.impl;

import com.api.expose.domain.metering.adapter.repository.IApiCallRecordRepository;
import com.api.expose.domain.metering.model.entity.ApiCallRecordEntity;
import com.api.expose.domain.metering.service.IBillingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 计费结算服务实现
 */
@Slf4j
@Service
public class BillingServiceImpl implements IBillingService {

    @Resource
    private IApiCallRecordRepository callRecordRepository;

    @Override
    public void processBilling() {
        log.debug("开始执行月度计费结算任务...");
        
        try {
            // 1. 获取上个月的起止时间
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            LocalDateTime startTime = lastMonth.withDayOfMonth(1).atStartOfDay();
            LocalDateTime endTime = lastMonth.plusMonths(1).withDayOfMonth(1).atStartOfDay();
            
            Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
            
            log.debug("计费周期: {} ~ {}", startDate, endDate);
            
            // 2. 查询上个月的所有调用记录
            List<ApiCallRecordEntity> records = callRecordRepository.queryRecordsByPeriod(startDate, endDate);
            
            if (records == null || records.isEmpty()) {
                log.debug("上个月无调用记录，跳过计费");
                return;
            }
            
            // 3. 按租户和应用分组统计
            Map<String, Long> appCallCounts = records.stream()
                    .collect(Collectors.groupingBy(
                            record -> record.getTenantId() + ":" + record.getAppId(),
                            Collectors.counting()
                    ));
            
            log.debug("统计完成，共 {} 个应用有调用记录", appCallCounts.size());
            
            // 4. 根据资产定义的计费阶梯 (Tiered Pricing) 计算金额
            // TODO: 实现具体的计费逻辑
            // - 查询每个 API 资产的计费配置
            // - 根据调用量计算费用
            // - 生成 billing_bill 记录
            
            appCallCounts.forEach((key, count) -> {
                log.debug("应用 {} 调用次数: {}", key, count);
                // calculateAndSaveBill(key, count); // 待实现
            });
            
            log.debug("计费任务执行完成。");
        } catch (Exception e) {
            log.error("计费任务执行失败", e);
            // 定时任务中抛出异常，让 XXL-Job 捕获并标记为失败
            throw new IllegalStateException("计费任务执行失败: " + e.getMessage(), e);
        }
    }
}
