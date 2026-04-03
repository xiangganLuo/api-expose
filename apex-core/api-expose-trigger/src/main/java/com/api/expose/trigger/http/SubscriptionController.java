package com.api.expose.trigger.http;

import com.api.expose.api.dto.SubscriptionRequestDTO;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import com.api.expose.domain.app.service.IAppService;
import com.api.expose.types.common.Response;
import com.api.expose.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 订阅服务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    @Resource
    private IAppService appService;

    /**
     * 申请订阅
     */
    @PostMapping("")
    public Response<Boolean> subscribe(@RequestBody SubscriptionRequestDTO requestDTO) {
        try {
            log.info("新订阅申请: appId={}, apiAssetId={}", requestDTO.getAppId(), requestDTO.getApiAssetId());
            SubscriptionEntity entity = SubscriptionEntity.builder()
                    .appId(requestDTO.getAppId())
                    .apiAssetId(requestDTO.getApiAssetId())
                    .remark(requestDTO.getRemark())
                    .build();
            
            appService.applySubscription(entity);
            return Response.success(true);
        } catch (Exception e) {
            log.error("订阅申请失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 审核通过订阅
     */
    @PutMapping("/{id}/approve")
    public Response<Boolean> approve(@PathVariable Long id, @RequestParam(required = false) String remark) {
        try {
            appService.auditSubscription(id, SubscriptionStatusEnum.APPROVED, remark);
            return Response.success(true);
        } catch (Exception e) {
            log.error("订阅通过操作失败", e);
            return Response.failure(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }
}
