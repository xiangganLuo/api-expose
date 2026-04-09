package com.api.expose.system.api.logger;

import com.api.expose.framework.common.biz.system.logger.OperateLogCommonApi;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.system.api.logger.dto.OperateLogPageReqDTO;
import com.api.expose.system.api.logger.dto.OperateLogRespDTO;

/**
 * 操作日志 API 接口
 *
 * @author apex
 */
public interface OperateLogApi extends OperateLogCommonApi {

    /**
     * 获取指定模块的指定数据的操作日志分页
     *
     * @param pageReqDTO 请求
     * @return 操作日志分页
     */
    PageResult<OperateLogRespDTO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO);

}
