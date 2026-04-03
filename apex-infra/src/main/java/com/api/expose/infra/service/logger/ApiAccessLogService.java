package com.api.expose.infra.service.logger;

import com.api.expose.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogPageReqVO;
import com.api.expose.infra.dal.dataobject.logger.ApiAccessLogDO;

/**
 * API 访问日志 Service 接口
 *
 */
public interface ApiAccessLogService {

    /**
     * 创建 API 访问日志
     *
     * @param createReqDTO API 访问日志
     */
    void createApiAccessLog(ApiAccessLogCreateReqDTO createReqDTO);

    /**
     * 获得 API 访问日志分页
     *
     * @param pageReqVO 分页查询
     * @return API 访问日志分页
     */
    PageResult<ApiAccessLogDO> getApiAccessLogPage(ApiAccessLogPageReqVO pageReqVO);

    /**
     * 清理 exceedDay 天前的访问日志
     *
     * @param exceedDay   超过多少天就进行清理
     * @param deleteLimit 清理的间隔条数
     */
    Integer cleanAccessLog(Integer exceedDay, Integer deleteLimit);

}
