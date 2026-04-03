package com.api.expose.system.service.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.api.expose.framework.common.enums.CommonStatusEnum;
import com.api.expose.framework.common.util.object.BeanUtils;
import com.api.expose.framework.datapermission.core.annotation.DataPermission;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.api.expose.system.dal.dataobject.dept.DeptDO;
import com.api.expose.system.dal.dataobject.user.AdminUserDO;
import com.api.expose.system.dal.mysql.dept.DeptMapper;
import com.api.expose.system.dal.mysql.dept.UserDeptMapper;
import com.api.expose.system.dal.mysql.user.AdminUserMapper;
import com.api.expose.system.dal.redis.RedisKeyConstants;
import com.api.expose.system.enums.BusinessModuleEnum;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.api.expose.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.api.expose.framework.common.util.collection.CollectionUtils.convertSet;
import static com.api.expose.system.enums.ErrorCodeConstants.*;

/**
 * 部门 Service 实现类
 *
 */
@Service
@Validated
@Slf4j
public class DeptServiceImpl implements DeptService {

    @Resource
    private DeptMapper deptMapper;

    @Resource
    private AdminUserMapper userMapper;

    @Resource
    private UserDeptMapper userDeptMapper;


    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public Long createDept(DeptSaveReqVO createReqVO) {
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(DeptDO.PARENT_ID_ROOT);
        }
        // 校验父部门的有效性
        validateParentDept(null, createReqVO.getParentId());
        // 校验部门名的唯一性
        validateDeptNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 插入部门
        DeptDO dept = BeanUtils.toBean(createReqVO, DeptDO.class);
        // 计算部门层级全称
        calculateDeptLevelName(dept, createReqVO);
        deptMapper.insert(dept);
        return dept.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public void updateDept(DeptSaveReqVO updateReqVO) {
        if (updateReqVO.getParentId() == null) {
            updateReqVO.setParentId(DeptDO.PARENT_ID_ROOT);
        }
        // 校验自己存在
        validateDeptExists(updateReqVO.getId());
        // 校验父部门的有效性
        validateParentDept(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验部门名的唯一性
        validateDeptNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());

        // 更新部门
        DeptDO updateObj = BeanUtils.toBean(updateReqVO, DeptDO.class);
        // 重新计算部门层级全称
        calculateDeptLevelName(updateObj, updateReqVO);
        deptMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public void deleteDept(Long id) {
        // 校验是否存在
        validateDeptExists(id);
        // 校验是否有子部门
        if (deptMapper.selectCountByParentId(id) > 0) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        // 删除部门
        deptMapper.deleteById(id);
        //删除部门下的用户
        deleteUserDeptBind(new LinkedHashSet<>(Collections.singleton(id)));
    }


    @VisibleForTesting
    void validateDeptExists(Long id) {
        if (id == null) {
            return;
        }
        DeptDO dept = deptMapper.selectById(id);
        if (dept == null) {
            throw exception(DEPT_NOT_FOUND);
        }
    }

    @VisibleForTesting
    void validateParentDept(Long id, Long parentId) {
        if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父部门
        if (Objects.equals(id, parentId)) {
            throw exception(DEPT_PARENT_ERROR);
        }
        // 2. 父部门不存在
        DeptDO parentDept = deptMapper.selectById(parentId);
        if (parentDept == null) {
            throw exception(DEPT_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父部门，如果父部门是自己的子部门，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentDept.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(DEPT_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父部门
            if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentDept = deptMapper.selectById(parentId);
            if (parentDept == null) {
                break;
            }
        }
    }

    @VisibleForTesting
    void validateDeptNameUnique(Long id, Long parentId, String name) {
        DeptDO dept = deptMapper.selectByParentIdAndName(parentId, name);
        if (dept == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的部门
        if (id == null) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
        if (ObjectUtil.notEqual(dept.getId(), id)) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
    }

    @Override
    public DeptDO getDept(Long id) {
        return deptMapper.selectById(id);
    }

    @Override
    public List<DeptRespVO> convertLeaderName(List<DeptDO> list, Map<Long, String> userIdNameMap) {
        List<DeptRespVO> voList = new ArrayList<>();
        for (DeptDO dept : list) {
            DeptRespVO vo = BeanUtils.toBean(dept, DeptRespVO.class);
            DeptDO parentDept = getDept(dept.getParentId());
            vo.setParentName(parentDept == null ? null : parentDept.getName());
            Set<Long> userIds = dept.getLeaderUserIds();
            if (userIds != null && !userIds.isEmpty()) {
                // 负责人姓名
                List<String> names = userIds.stream()
                        .map(userIdNameMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                vo.setLeaderUserNames(String.join(",", names));
                // 负责人ID字符串
                String leaderIdsStr = userIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                vo.setLeaderUserIdsStr(leaderIdsStr);
            } else {
                vo.setLeaderUserNames("");
                vo.setLeaderUserIdsStr("");
            }
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<DeptDO> getChildDept(Long pid) {
        // 查询当前层，所有的子部门
        List<DeptDO> depts = deptMapper.selectListByParentId(Collections.singleton(pid));
        return depts;
    }

    @Override
    public List<DeptDO> getDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return deptMapper.selectByIds(ids);
    }

    @Override
    public List<DeptDO> getDeptList(DeptListReqVO reqVO) {
        List<DeptDO> list = deptMapper.selectList(reqVO);
        list.sort(Comparator.comparing(DeptDO::getSort));
        return list;
    }

    @Override
    public List<DeptDO> getChildDeptList(Collection<Long> ids) {
        List<DeptDO> children = new LinkedList<>();
        // 遍历每一层
        Collection<Long> parentIds = ids;
        for (int i = 0; i < Short.MAX_VALUE; i++) { // 使用 Short.MAX_VALUE 避免 bug 场景下，存在死循环
            // 查询当前层，所有的子部门
            List<DeptDO> depts = deptMapper.selectListByParentId(parentIds);
            // 1. 如果没有子部门，则结束遍历
            if (CollUtil.isEmpty(depts)) {
                break;
            }
            // 2. 如果有子部门，继续遍历
            children.addAll(depts);
            parentIds = convertSet(depts, DeptDO::getId);
        }
        return children;
    }

    @Override
    public List<DeptDO> getDeptListByLeaderUserId(Long id) {
        return deptMapper.selectListByLeaderUserId(id);
    }

    @Override
    @DataPermission(enable = false) // 禁用数据权限，避免建立不正确的缓存
    @Cacheable(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, key = "#id")
    public Set<Long> getChildDeptIdListFromCache(Long id) {
        List<DeptDO> children = getChildDeptList(id);
        return convertSet(children, DeptDO::getId);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得科室信息
        Map<Long, DeptDO> deptMap = getDeptMap(ids);
        // 校验
        ids.forEach(id -> {
            DeptDO dept = deptMap.get(id);
            if (dept == null) {
                throw exception(DEPT_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
                throw exception(DEPT_NOT_ENABLE, dept.getName());
            }
        });
    }

    public List<DeptDO> getParentDeptList(Long deptId) {
        List<DeptDO> parents = new LinkedList<>();
        Long currentId = deptId;
        for (int i = 0; i < Short.MAX_VALUE; i++) { // 防止死循环
            DeptDO dept = deptMapper.selectById(currentId);
            if (dept == null || dept.getParentId() == null || dept.getParentId() == 0) {
                break;
            }
            DeptDO parentDept = deptMapper.selectById(dept.getParentId());
            if (parentDept == null) {
                break;
            }
            parents.add(parentDept);
            currentId = parentDept.getId();
        }
        // 如果你想从顶级到当前部门顺序，可以反转
        Collections.reverse(parents);
        return parents;
    }

    @Override
    public void deleteUserDeptBind(LinkedHashSet<Long> deptIds) {
        // 1. 删除部门关联
        List<AdminUserDO> users =  userMapper.selectListByDeptIds(deptIds);
        if(ObjectUtil.isNotEmpty(users)){
            // 准备批量更新的用户列表
            List<AdminUserDO> updateUsers = new ArrayList<>();
            // 遍历所有受影响的用户
            for (AdminUserDO user : users) {
                // 获取用户当前的部门ID集合
                LinkedHashSet<Long> userDeptIds = user.getDeptIds();
                if (userDeptIds != null) {
                    // 从用户的部门集合中移除指定的部门ID
                    userDeptIds.removeAll(deptIds);
                    // 只更新deptIds字段
                    AdminUserDO updateUser = new AdminUserDO();
                    updateUser.setId(user.getId());
                    updateUser.setDeptIds(userDeptIds);
                    updateUsers.add(updateUser);
                }
            }
            // 批量更新用户信息
            if (!updateUsers.isEmpty()) {
                userMapper.updateBatch(updateUsers);
            }
        }
        // 2. 同时从用户部门映射表中删除指定的部门ID
        userDeptMapper.deleteByDeptId(deptIds);
    }

    @Override
    public List<DeptDO> getDeptsByBusinessModule(String moduleCode) {
        // 根据业务模块编码获取对应的表名
        BusinessModuleEnum moduleEnum = BusinessModuleEnum.getByCode(moduleCode);
        // 连表查询业务表中实际使用的部门，去重且只返回启用的部门
        return deptMapper.selectDeptsByTable(moduleEnum.getTableName());
    }

    @Override
    public DeptDO findDeptByHierarchicalName(String hierarchicalName) {
        if (StrUtil.isBlank(hierarchicalName)) {
            return null;
        }

        String cleanedName = hierarchicalName.trim();

        // 预先加载所有部门信息
        Map<String, DeptDO> deptNameMap = this.deptMapper.selectList().stream().collect(Collectors.toMap(DeptDO::getName, dept -> dept, (existing, replacement) -> existing));

        // 1. 先尝试直接匹配完整名称
        DeptDO directMatch = deptNameMap.get(cleanedName);
        if (directMatch != null) {
            log.debug("直接匹配部门成功：{}", cleanedName);
            return directMatch;
        }

        // 2. 如果包含'-'，则按层级结构逐级查找
        if (cleanedName.contains("-")) {
            String[] deptLevels = cleanedName.split("-");
            if (deptLevels.length < 2) {
                log.warn("层级部门格式不正确：{}", cleanedName);
                return null;
            }

            return findDeptByLevels(deptLevels, deptNameMap);
        }

        log.warn("无法匹配部门：{}", cleanedName);
        return null;
    }

    /**
     * 计算部门层级全称
     * @param dept 部门实体
     * @param reqVO 请求VO
     */
    private void calculateDeptLevelName(DeptDO dept, DeptSaveReqVO reqVO) {
        if (StrUtil.isNotBlank(reqVO.getDeptType()) && ("1".equals(reqVO.getDeptType()) || "2".equals(reqVO.getDeptType()))) {
            // 部门类型为1或2时：层级全称等于部门名称
            dept.setDeptLevelName(reqVO.getName());
        } else {
            // 其他类型：层级全称等于父级部门层级全称-自身部门名称
            if (reqVO.getParentId() != null && !DeptDO.PARENT_ID_ROOT.equals(reqVO.getParentId())) {
                DeptDO parentDept = deptMapper.selectById(reqVO.getParentId());
                if (parentDept != null && StrUtil.isNotBlank(parentDept.getDeptLevelName())) {
                    dept.setDeptLevelName(parentDept.getDeptLevelName() + "-" + reqVO.getName());
                } else {
                    dept.setDeptLevelName(reqVO.getName());
                }
            } else {
                dept.setDeptLevelName(reqVO.getName());
            }
        }
    }

    /**
     * 按层级数组逐级查找部门
     * 算法：从根部门开始，逐级向下查找，每一级都验证父子关系
     *
     * @param deptLevels 部门层级数组，如 ["研发部", "前端组", "UI组"]
     * @param deptNameMap 部门名称映射
     * @return 找到的最底层部门，找不到返回null
     */
    private DeptDO findDeptByLevels(String[] deptLevels, Map<String, DeptDO> deptNameMap) {
        // 清理每一级的部门名称
        for (int i = 0; i < deptLevels.length; i++) {
            deptLevels[i] = deptLevels[i].trim();
            if (StrUtil.isBlank(deptLevels[i])) {
                log.warn("部门层级中存在空名称：{}", Arrays.toString(deptLevels));
                return null;
            }
        }

        // 1. 查找根部门（第一级）
        String rootDeptName = deptLevels[0];
        DeptDO currentDept = deptNameMap.get(rootDeptName);
        if (currentDept == null) {
            log.warn("找不到根部门：{}", rootDeptName);
            return null;
        }

        log.debug("找到根部门：{} (ID: {})", rootDeptName, currentDept.getId());

        // 2. 逐级向下查找子部门
        for (int i = 1; i < deptLevels.length; i++) {
            String targetChildName = deptLevels[i];
            Long parentDeptId = currentDept.getId();

            // 获取当前父部门下的所有子部门
            List<DeptDO> childDepts = getChildDeptList(parentDeptId);
            if (CollUtil.isEmpty(childDepts)) {
                log.warn("父部门 {} (ID: {}) 下没有子部门，无法找到：{}", currentDept.getName(), parentDeptId, targetChildName);
                return null;
            }

            // 在子部门中查找目标部门
            DeptDO targetChild = null;
            for (DeptDO childDept : childDepts) {
                if (targetChildName.equals(childDept.getName())) {
                    targetChild = childDept;
                    break;
                }
            }

            if (targetChild == null) {
                log.warn("在父部门 {} (ID: {}) 下找不到子部门：{}", currentDept.getName(), parentDeptId, targetChildName);
                return null;
            }

            // 更新当前部门为找到的子部门，继续下一级查找
            currentDept = targetChild;
            log.debug("找到第{}级部门：{} (ID: {}, 父部门ID: {})", i + 1, targetChild.getName(), targetChild.getId(), parentDeptId);
        }

        log.info("层级查找成功：{} -> 最终部门：{} (ID: {})", Arrays.toString(deptLevels), currentDept.getName(), currentDept.getId());
        return currentDept;
    }

    @Override
    public int fixDeptLevelNames() {
        log.info("开始修复部门层级全称...");
        int totalUpdated = 0;

        try {
            // 1. 获取所有部门，按层级排序（父部门在前）
            List<DeptDO> allDepts = deptMapper.selectList();

            // 2. 构建部门层级结构映射
            Map<Long, DeptDO> deptMap = allDepts.stream()
                .collect(Collectors.toMap(DeptDO::getId, dept -> dept));

            // 3. 按层级顺序处理部门
            List<DeptDO> rootDepts = allDepts.stream()
                .filter(dept -> dept.getParentId() == null || DeptDO.PARENT_ID_ROOT.equals(dept.getParentId()))
                .collect(Collectors.toList());

            // 4. 递归处理每个根部门及其子部门
            for (DeptDO rootDept : rootDepts) {
                totalUpdated += fixDeptLevelNameRecursive(rootDept, deptMap, "");
            }

            log.info("部门层级全称修复完成，共更新 {} 个部门", totalUpdated);

        } catch (Exception e) {
            log.error("修复部门层级全称时发生错误", e);
            throw e;
        }

        return totalUpdated;
    }

    /**
     * 递归修复部门层级全称
     */
    private int fixDeptLevelNameRecursive(DeptDO dept, Map<Long, DeptDO> deptMap, String parentLevelName) {
        int updated = 0;

        // 计算当前部门的层级全称
        String deptLevelName;
        if (StrUtil.isNotBlank(dept.getDeptType()) && ("1".equals(dept.getDeptType()) || "2".equals(dept.getDeptType()))) {
            // 部门类型为1或2时：层级全称等于部门名称
            deptLevelName = dept.getName();
        } else {
            // 其他类型：层级全称等于父级部门层级全称-自身部门名称
            if (StrUtil.isNotBlank(parentLevelName)) {
                deptLevelName = parentLevelName + "-" + dept.getName();
            } else {
                deptLevelName = dept.getName();
            }
        }

        // 更新数据库中的部门层级全称（只有当前值为空或不同时才更新）
        if (StrUtil.isBlank(dept.getDeptLevelName()) || !deptLevelName.equals(dept.getDeptLevelName())) {
            DeptDO updateObj = new DeptDO();
            updateObj.setId(dept.getId());
            updateObj.setDeptLevelName(deptLevelName);
            deptMapper.updateById(updateObj);

            // 更新内存中的对象
            dept.setDeptLevelName(deptLevelName);
            updated++;

            log.debug("更新部门 [{}] 的层级全称为: {}", dept.getName(), deptLevelName);
        }

        // 递归处理子部门
        List<DeptDO> children = deptMap.values().stream()
            .filter(child -> dept.getId().equals(child.getParentId()))
            .collect(Collectors.toList());

        for (DeptDO child : children) {
            updated += fixDeptLevelNameRecursive(child, deptMap, deptLevelName);
        }

        return updated;
    }

}
