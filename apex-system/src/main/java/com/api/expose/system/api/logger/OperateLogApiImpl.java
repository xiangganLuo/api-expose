package com.api.expose.system.api.logger;

import com.api.expose.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.framework.common.util.object.BeanUtils;
import com.api.expose.system.api.logger.dto.OperateLogPageReqDTO;
import com.api.expose.system.api.logger.dto.OperateLogRespDTO;
import com.api.expose.system.dal.dataobject.logger.OperateLogDO;
import com.api.expose.system.service.logger.OperateLogService;
import com.fhs.core.trans.anno.TransMethodResult;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 操作日志 API 实现类
 *
 */
@Service
@Validated
public class OperateLogApiImpl implements OperateLogApi {

    @Resource
    private OperateLogService operateLogService;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        operateLogService.createOperateLog(createReqDTO);
    }

    @Override
    @TransMethodResult
    public PageResult<OperateLogRespDTO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        PageResult<OperateLogDO> operateLogPage = operateLogService.getOperateLogPage(pageReqDTO);
        return BeanUtils.toBean(operateLogPage, OperateLogRespDTO.class);
    }

}
