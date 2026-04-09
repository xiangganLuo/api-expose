package com.api.expose.infrastructure.convert.app;

import com.api.expose.domain.app.model.entity.DeveloperAppEntity;
import com.api.expose.domain.app.model.entity.SubscriptionEntity;
import com.api.expose.domain.app.model.valobj.AppStatusEnum;
import com.api.expose.domain.app.model.valobj.SubscriptionStatusEnum;
import com.api.expose.infrastructure.dao.po.DeveloperAppPO;
import com.api.expose.infrastructure.dao.po.SubscriptionPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/*
 * @author xiangganluo
 */

@Mapper
public interface AppConvert {

    AppConvert INSTANCE = Mappers.getMapper(AppConvert.class);

    @Mapping(source = "appId", target = "id")
    @Mapping(source = "appName", target = "appName")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().getCode() : null)")
    DeveloperAppPO convert(DeveloperAppEntity entity);

    default DeveloperAppEntity convert(DeveloperAppPO po) {
        if (po == null) {
            return null;
        }
        return DeveloperAppEntity.builder()
                .appId(po.getId())
                .tenantId(po.getTenantId())
                .appName(po.getAppName())
                .description(po.getDescription())
                .status(po.getStatus() != null ? AppStatusEnum.getEnumByCode(po.getStatus()) : null)
                .apiKey(po.getApiKey())
                .apiSecret(po.getApiSecret())
                .callbackUrl(po.getCallbackUrl())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    @Mapping(source = "id", target = "id")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().getCode() : null)")
    SubscriptionPO convert(SubscriptionEntity entity);

    default SubscriptionPO convertTimes(SubscriptionEntity entity, SubscriptionPO po) {
        if (entity == null) return po;
        if (po == null) po = new SubscriptionPO();
        po.setApplyTime(entity.getApplyTime() != null
                ? entity.getApplyTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                : null);
        po.setApproveTime(entity.getApproveTime() != null
                ? entity.getApproveTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                : null);
        return po;
    }

    default SubscriptionEntity convert(SubscriptionPO po) {
        if (po == null) {
            return null;
        }
        return SubscriptionEntity.builder()
                .id(po.getId())
                .tenantId(po.getTenantId())
                .appId(po.getAppId())
                .apiAssetId(po.getApiAssetId())
                .status(po.getStatus() != null ? SubscriptionStatusEnum.getEnumByCode(po.getStatus()) : null)
                .applyTime(po.getApplyTime() != null ? java.sql.Timestamp.valueOf(po.getApplyTime()) : null)
                .approveTime(po.getApproveTime() != null ? java.sql.Timestamp.valueOf(po.getApproveTime()) : null)
                .remark(po.getRemark())
                .build();
    }
}
