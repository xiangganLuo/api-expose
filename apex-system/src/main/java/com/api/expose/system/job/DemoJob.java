package com.api.expose.system.job;

import com.api.expose.framework.quartz.core.handler.JobHandler;
import com.api.expose.framework.tenant.core.context.TenantContextHolder;
import com.api.expose.framework.tenant.core.job.TenantJob;
import com.api.expose.system.dal.dataobject.user.AdminUserDO;
import com.api.expose.system.dal.mysql.user.AdminUserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class DemoJob implements JobHandler {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    @TenantJob // 标记多租户
    public String execute(String param) {
        System.out.println("当前租户：" + TenantContextHolder.getTenantId());
        List<AdminUserDO> users = adminUserMapper.selectList();
        return "用户数量：" + users.size();
    }

}
