package com.api.expose.system.api.dept;

import com.api.expose.framework.common.util.collection.CollectionUtils;
import com.api.expose.system.api.dept.dto.DeptRespDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 部门 API 接口
 *
 */
public interface DeptApi {

    /**
     * 获得部门信息
     *
     * @param id 部门编号
     * @return 部门信息
     */
    DeptRespDTO getDept(Long id);

    /**
     * 获得部门信息数组
     *
     * @param ids 部门编号数组
     * @return 部门信息数组
     */
    List<DeptRespDTO> getDeptList(Collection<Long> ids);

    /**
     * 校验部门们是否有效。如下情况，视为无效：
     * 1. 部门编号不存在
     * 2. 部门被禁用
     *
     * @param ids 角色编号数组
     */
    void validateDeptList(Collection<Long> ids);

    /**
     * 获得指定编号的部门 Map
     *
     * @param ids 部门编号数组
     * @return 部门 Map
     */
    default Map<Long, DeptRespDTO> getDeptMap(Collection<Long> ids) {
        List<DeptRespDTO> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, DeptRespDTO::getId);
    }

    /**
     * 获得指定部门的所有子部门
     *
     * @param id 部门编号
     * @return 子部门列表
     */
    List<DeptRespDTO> getChildDeptList(Long id);

    /**
     * 根据层级部门名称查找部门（支持父子级映射）
     * 支持格式：
     * 1. 单一部门名称："前端组"
     * 2. 层级部门名称："研发部-前端组-UI组"
     *
     * @param hierarchicalName 层级部门名称，如 "研发部-前端组-UI组" 或 "前端组"
     * @return 找到的部门，找不到返回null
     */
    DeptRespDTO findDeptByHierarchicalName(String hierarchicalName);

}
