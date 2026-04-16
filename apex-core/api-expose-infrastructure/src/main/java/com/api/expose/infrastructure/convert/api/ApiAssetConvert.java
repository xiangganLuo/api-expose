package com.api.expose.infrastructure.convert.api;

import com.api.expose.domain.api.model.aggregate.ApiAssetAggregate;
import com.api.expose.domain.api.model.entity.ApiEndpointEntity;
import com.api.expose.domain.api.model.entity.ApiVersionEntity;
import com.api.expose.infrastructure.dao.po.ApiAssetPO;
import com.api.expose.infrastructure.dao.po.ApiEndpointPO;
import com.api.expose.infrastructure.dao.po.ApiVersionPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/*
 * @author xiangganluo
 */

@Mapper
public interface ApiAssetConvert {

    ApiAssetConvert INSTANCE = Mappers.getMapper(ApiAssetConvert.class);

    default ApiAssetPO convert(ApiAssetAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        ApiAssetPO po = new ApiAssetPO();
        po.setId(aggregate.getAssetId());
        po.setTenantId(aggregate.getTenantId());
        po.setName(aggregate.getName());
        po.setDescription(aggregate.getDescription());
        po.setGroupName(aggregate.getGroupName());
        po.setProtocolType(aggregate.getProtocolType() != null ? aggregate.getProtocolType().name() : null);
        po.setStatus(aggregate.getStatus() != null ? aggregate.getStatus().getCode() : null);
        po.setBasePath(aggregate.getBasePath());
        return po;
    }

    default ApiEndpointPO convert(ApiEndpointEntity entity, Long tenantId, Long assetId) {
        if (entity == null) {
            return null;
        }
        ApiEndpointPO po = new ApiEndpointPO();
        po.setId(entity.getEndpointId());
        po.setTenantId(tenantId);
        po.setAssetId(assetId);
        po.setPath(entity.getPath());
        po.setHttpMethod(entity.getHttpMethod() != null ? entity.getHttpMethod().getCode() : null);
        po.setName(entity.getName());
        po.setSummary(entity.getSummary());
        po.setRequestSchema(entity.getDefinition() != null ? entity.getDefinition().getRequestSchema() : null);
        po.setResponseSchema(entity.getDefinition() != null ? entity.getDefinition().getResponseSchema() : null);
        po.setUpstreamUrl(entity.getUpstreamUrl());
        po.setTimeoutMs(entity.getTimeoutMs());
        return po;
    }

    default ApiVersionPO convert(ApiVersionEntity entity, Long tenantId, Long assetId) {
        if (entity == null) {
            return null;
        }
        ApiVersionPO po = new ApiVersionPO();
        po.setId(entity.getVersionId());
        po.setTenantId(tenantId);
        po.setAssetId(assetId);
        po.setVersion(entity.getVersion());
        po.setActive(entity.isActive() ? 1 : 0);
        po.setReleaseNote(entity.getReleaseNote());
        return po;
    }

}

