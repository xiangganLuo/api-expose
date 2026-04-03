package com.api.expose.system.dal.mysql.mail;

import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.framework.mybatis.core.mapper.BaseMapperX;
import com.api.expose.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.api.expose.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import com.api.expose.system.dal.dataobject.mail.MailAccountDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailAccountMapper extends BaseMapperX<MailAccountDO> {

    default PageResult<MailAccountDO> selectPage(MailAccountPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MailAccountDO>()
                .likeIfPresent(MailAccountDO::getMail, pageReqVO.getMail())
                .likeIfPresent(MailAccountDO::getUsername , pageReqVO.getUsername())
                .orderByDesc(MailAccountDO::getId));
    }

}
