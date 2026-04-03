package com.api.expose.system.api.user;

import com.api.expose.framework.common.pojo.CommonResult;
import com.api.expose.framework.common.util.collection.CollectionUtils;
import com.api.expose.system.api.user.dto.AdminUserRespDTO;
import com.api.expose.system.api.user.dto.BindStaffCodeReqDTO;
import com.api.expose.system.controller.admin.user.vo.user.UserSaveReqVO;

import java.util.*;

/**
 * Admin 用户 API 接口
 *
 */
public interface AdminUserApi {

    /**
     * 创建用户
     *
     * @param createReqVO 用户信息
     * @return 用户编号
     */
    Long createUser(UserSaveReqVO createReqVO);

    /**
     * 绑定工号
     */
    CommonResult<Boolean> bindStaffCode(BindStaffCodeReqDTO reqVO);

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    AdminUserRespDTO getUser(Long id);

    /**
     * 通过用户 ID 查询用户下属
     *
     * @param id 用户编号
     * @return 用户下属用户列表
     */
    List<AdminUserRespDTO> getUserListBySubordinate(Long id);

    /**
     * 通过用户 ID 查询用户们
     *
     * @param ids 用户 ID 们
     * @return 用户对象信息
     */
    List<AdminUserRespDTO> getUserList(Collection<Long> ids);

    /**
     * 获得指定部门的用户数组
     *
     * @param deptIds 部门数组
     * @return 用户数组
     */
    List<AdminUserRespDTO> getUserListByDeptIds(Collection<Long> deptIds);

    /**
     * 获得指定岗位的用户数组
     *
     * @param postIds 岗位数组
     * @return 用户数组
     */
    List<AdminUserRespDTO> getUserListByPostIds(Collection<Long> postIds);

    /**
     * 统计指定岗位的用户数量
     *
     * @param postIds 岗位ID集合
     * @return Map<岗位ID, 用户数量>
     */
    Map<Long, Long> countUsersByPostIds(Collection<Long> postIds);

    /**
     * 获得用户 Map
     *
     * @param ids 用户编号数组
     * @return 用户 Map
     */
    default Map<Long, AdminUserRespDTO> getUserMap(Collection<Long> ids) {
        List<AdminUserRespDTO> users = getUserList(ids);
        return CollectionUtils.convertMap(users, AdminUserRespDTO::getId);
    }

    /**
     * 校验用户是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用
     *
     * @param id 用户编号
     */
    default void validateUser(Long id) {
        validateUserList(Collections.singleton(id));
    }

    /**
     * 校验用户们是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用
     *
     * @param ids 用户编号数组
     */
    void validateUserList(Collection<Long> ids);



    /**
     * 查询部门id下符合岗位的用户
     *
     * @param id 用户编号
     * @return 用户下属用户列表
     */
    List<AdminUserRespDTO> getUserByDeptIdAndPostId(Long id, Set<Long> postId);

    /**
     * 通过用户昵称精确查询用户
     *
     * @param username 用户昵称
     * @return 用户对象信息，如果不存在则返回null
     */
    AdminUserRespDTO getUserByUserName(String username);

    /**
     * 通过用户昵称模糊查询用户
     */
    List<AdminUserRespDTO> getUserByNickName(String nickName);

    /**
     * 通过工号查询用户
     *
     * @param staffCode 工号
     * @return 用户对象信息，如果不存在则返回null
     */
    AdminUserRespDTO getUserByStaffCode(String staffCode);

}
