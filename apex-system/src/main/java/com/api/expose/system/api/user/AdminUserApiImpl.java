package com.api.expose.system.api.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.util.object.BeanUtils;
import com.api.expose.framework.datapermission.core.util.DataPermissionUtils;
import com.api.expose.system.api.user.dto.AdminUserRespDTO;
import com.api.expose.system.api.user.dto.BindStaffCodeReqDTO;
import com.api.expose.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.api.expose.system.dal.dataobject.dept.DeptDO;
import com.api.expose.system.dal.dataobject.user.AdminUserDO;
import com.api.expose.system.service.dept.DeptService;
import com.api.expose.system.service.user.AdminUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.api.expose.framework.common.pojo.CommonResult.success;
import static com.api.expose.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * Admin 用户 API 实现类
 *
 */
@Service
public class AdminUserApiImpl implements AdminUserApi {

    @Resource
    private AdminUserService userService;

    @Resource
    private DeptService deptService;

    @Override
    public Long createUser(UserSaveReqVO createReqVO) {
        return userService.createUser(createReqVO);
    }

    @Override
    public CommonResult<Boolean> bindStaffCode(BindStaffCodeReqDTO reqVO) {
        AdminUserDO user = userService.getUser(reqVO.getId());
        if (ObjUtil.isNull(user)) {
            return success(false);
        }
        UserSaveReqVO req = BeanUtils.toBean(reqVO, UserSaveReqVO.class);
        req.setDeptIds(user.getDeptIds());
        req.setPostIds(user.getPostIds());
        userService.updateUser(req);
        return success(true);
    }
    @Override
    public AdminUserRespDTO getUser(Long id) {
        AdminUserDO user = userService.getUser(id);
        return BeanUtils.toBean(user, AdminUserRespDTO.class);
    }

    @Override
    public List<AdminUserRespDTO> getUserListBySubordinate(Long id) {
        // 1.1 获取用户负责的部门
        List<DeptDO> depts = deptService.getDeptListByLeaderUserId(id);
        if (CollUtil.isEmpty(depts)) {
            return Collections.emptyList();
        }
        // 1.2 获取所有子部门
        Set<Long> deptIds = convertSet(depts, DeptDO::getId);
        List<DeptDO> childDeptList = deptService.getChildDeptList(deptIds);
        if (CollUtil.isNotEmpty(childDeptList)) {
            deptIds.addAll(convertSet(childDeptList, DeptDO::getId));
        }

        // 2. 获取部门对应的用户信息
        List<AdminUserDO> users = userService.getUserListByDeptIds(deptIds);
        users.removeIf(item -> ObjUtil.equal(item.getId(), id)); // 排除自己
        return BeanUtils.toBean(users, AdminUserRespDTO.class);
    }

    @Override
    public List<AdminUserRespDTO> getUserList(Collection<Long> ids) {
        return DataPermissionUtils.executeIgnore(() -> { // 禁用数据权限。原因是，一般基于指定 id 的 API 查询，都是数据拼接为主
            List<AdminUserDO> users = userService.getUserList(ids);
            return BeanUtils.toBean(users, AdminUserRespDTO.class);
        });
    }

    @Override
    public List<AdminUserRespDTO> getUserListByDeptIds(Collection<Long> deptIds) {
        List<AdminUserDO> users = userService.getUserListByDeptIds(deptIds);
        return BeanUtils.toBean(users, AdminUserRespDTO.class);
    }

    @Override
    public List<AdminUserRespDTO> getUserListByPostIds(Collection<Long> postIds) {
        List<AdminUserDO> users = userService.getUserListByPostIds(postIds);
        return BeanUtils.toBean(users, AdminUserRespDTO.class);
    }

    @Override
    public Map<Long, Long> countUsersByPostIds(Collection<Long> postIds) {
        return userService.countUsersByPostIds(postIds);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        userService.validateUserList(ids);
    }

    @Override
    public List<AdminUserRespDTO> getUserByDeptIdAndPostId(Long deptId, Set<Long> postIds) {
        // 1. 获取所有上级部门
        List<DeptDO> parentDeptList = deptService.getParentDeptList(deptId);
        // 2. 获取所有下级部门
        List<DeptDO> childDeptList = deptService.getChildDeptList(deptId);

        Set<Long> deptIds = parentDeptList.stream().map(DeptDO::getId).collect(Collectors.toSet());
        deptIds.addAll(childDeptList.stream().map(DeptDO::getId).collect(Collectors.toSet()));
        deptIds.add(deptId); // 包含本部门

        // 3. 一次性查询同时满足部门和岗位条件的用户（高效的数据库层面过滤）
        List<AdminUserDO> users = userService.getUserListByDeptIdsAndPostIds(deptIds, postIds);

        return BeanUtils.toBean(users, AdminUserRespDTO.class);
    }

    @Override
    public AdminUserRespDTO getUserByUserName(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        AdminUserDO user = userService.getUserByUsername(username);
        return BeanUtils.toBean(user, AdminUserRespDTO.class);
    }

    @Override
    public List<AdminUserRespDTO> getUserByNickName(String nickName) {
        if (StrUtil.isBlank(nickName)) {
            return null;
        }
        List<AdminUserDO> users = userService.getUserByNickName(nickName);
        return BeanUtils.toBean(users, AdminUserRespDTO.class);
    }

    @Override
    public AdminUserRespDTO getUserByStaffCode(String staffCode) {
        if (StrUtil.isBlank(staffCode)) {
            return null;
        }
        AdminUserDO user = userService.getUserByStaffCode(staffCode);
        return BeanUtils.toBean(user, AdminUserRespDTO.class);
    }


}
