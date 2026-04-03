package com.api.expose.system.convert.user;

import cn.hutool.core.collection.CollUtil;
import com.api.expose.framework.common.util.collection.CollectionUtils;
import com.api.expose.framework.common.util.object.BeanUtils;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.api.expose.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.api.expose.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.api.expose.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.api.expose.system.controller.admin.user.vo.user.UserRespVO;
import com.api.expose.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.api.expose.system.dal.dataobject.dept.DeptDO;
import com.api.expose.system.dal.dataobject.dept.PostDO;
import com.api.expose.system.dal.dataobject.permission.RoleDO;
import com.api.expose.system.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface UserConverts {

    UserConverts INSTANCE = Mappers.getMapper(UserConverts.class);

    default List<UserRespVO> convertList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            // 获取用户的所有部门
            UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
            if (CollUtil.isNotEmpty(user.getDeptIds())) {
                // 构建部门名称（多个部门用逗号分隔）
                String deptNames = user.getDeptIds().stream()
                        .map(deptId -> {
                            DeptDO dept = deptMap.get(deptId);
                            return dept != null ? dept.getName() : "";
                        })
                        .filter(deptName -> !deptName.isEmpty())
                        .collect(Collectors.joining(", "));
                userVO.setDeptNames(deptNames);

                // 设置第一个部门的名称到deptName字段
                Long firstDeptId = user.getDeptIds().iterator().next();
                DeptDO firstDept = deptMap.get(firstDeptId);
                if (firstDept != null) {
                    userVO.setDeptName(firstDept.getName());
                }
            }
            return userVO;
        });
    }

    default UserRespVO convert(AdminUserDO user,  Map<Long, DeptDO> deptMap) {
        UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
        if (CollUtil.isNotEmpty(user.getDeptIds())) {
            // 构建部门名称（多个部门用逗号分隔）
            String deptNames = user.getDeptIds().stream()
                    .map(deptId -> {
                        DeptDO dept = deptMap.get(deptId);
                        return dept != null ? dept.getName() : "";
                    })
                    .filter(deptName -> !deptName.isEmpty())
                    .collect(Collectors.joining(", "));
            userVO.setDeptName(deptNames);
        }
        return userVO;
    }

    default List<UserSimpleRespVO> convertSimpleList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleRespVO userVO = BeanUtils.toBean(user, UserSimpleRespVO.class);
            // 获取用户的所有部门
            if (CollUtil.isNotEmpty(user.getDeptIds())) {
                // 构建部门名称（多个部门用逗号分隔）
                String deptNames = user.getDeptIds().stream()
                        .map(deptId -> {
                            DeptDO dept = deptMap.get(deptId);
                            return dept != null ? dept.getName() : "";
                        })
                        .filter(deptName -> !deptName.isEmpty())
                        .collect(Collectors.joining(", "));
                userVO.setDeptName(deptNames);

                // 设置第一个部门的名称到deptName字段
                Long firstDeptId = user.getDeptIds().iterator().next();
                DeptDO firstDept = deptMap.get(firstDeptId);
                if (firstDept != null) {
                    userVO.setDeptName(firstDept.getName());
                }
            }
            return userVO;
        });
    }

    default UserProfileRespVO convert(AdminUserDO user, List<RoleDO> userRoles,
                                      List<DeptDO> userDepts, List<PostDO> posts) {
        UserProfileRespVO userVO = BeanUtils.toBean(user, UserProfileRespVO.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleRespVO.class));
        userVO.setDepts(BeanUtils.toBean(userDepts, DeptSimpleRespVO.class));
        userVO.setPosts(BeanUtils.toBean(posts, PostSimpleRespVO.class));
        return userVO;
    }

}
