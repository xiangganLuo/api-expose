package com.api.expose.trigger.job;

import com.api.expose.domain.metering.service.IBillingService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 计费汇总任务
 */
@Slf4j
@Component
public class BillingJobHandler {

    @Resource
    private IBillingService billingService;

    /**
     * 月度账单生成任务
     */
    @XxlJob("billingJobHandler")
    public void execute() {
        XxlJobHelper.log("开始执行计费流水汇总任务...");
        try {
            // 调用领域层服务生成月度账单
            billingService.processBilling();
            XxlJobHelper.handleSuccess("任务执行成功");
        } catch (Exception e) {
            log.error("计费流汇总任务执行失败", e);
            XxlJobHelper.log("任务执行异常: " + e.getMessage());
            XxlJobHelper.handleFail("任务执行失败");
        }
    }

}
