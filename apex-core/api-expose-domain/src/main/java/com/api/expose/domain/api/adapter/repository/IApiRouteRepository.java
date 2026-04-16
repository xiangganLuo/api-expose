package com.api.expose.domain.api.adapter.repository;

import com.api.expose.domain.api.model.valobj.RouteRuleVO;
import com.api.expose.framework.common.pojo.PageParam;
import com.api.expose.framework.common.pojo.PageResult;

/**
 * 路由规则查询仓储接口
 */
public interface IApiRouteRepository {

    /**
     * 分页查询当前生效的网关路由规则
     * @param keywords 关键词 (网关路径/上游地址)
     * @param pageParam 分页参数
     * @return 路由规则分页结果
     */
    PageResult<RouteRuleVO> pageRouteRules(String keywords, PageParam pageParam);
}
