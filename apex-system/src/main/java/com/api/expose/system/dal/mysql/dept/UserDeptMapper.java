package com.api.expose.system.dal.mysql.dept;

import cn.hutool.core.collection.CollUtil;
import com.api.expose.framework.mybatis.core.mapper.BaseMapperX;
import com.api.expose.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.api.expose.system.dal.dataobject.dept.UserDeptDO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserDeptMapper extends BaseMapperX<UserDeptDO> {

    default List<UserDeptDO> selectListByUserId(Long userId) {
        return selectList(UserDeptDO::getUserId, userId);
    }

    default void insertBatchByDeptIds(LinkedHashSet<Long> deptIds, Long userId) {
        // 快速检查参数
        if (CollUtil.isEmpty(deptIds) || userId == null) {
            return;
        }

        // 直接从Set构建对象列表，无需转换为ArrayList
        List<UserDeptDO> userDeptList = deptIds.stream()
                .map(deptId -> new UserDeptDO()
                        .setUserId(userId)
                        .setDeptId(deptId))
                .collect(Collectors.toList());

        // 只在列表非空时执行插入操作
        if (!userDeptList.isEmpty()) {
            insertBatch(userDeptList);
        }
    }



    default void deleteByUserIdAndDeptId(Long userId, Collection<Long> deptIds) {
        delete(new LambdaQueryWrapperX<UserDeptDO>()
                .eq(UserDeptDO::getUserId, userId)
                .in(UserDeptDO::getDeptId, deptIds));
    }

    default void deleteByDeptId(Collection<Long> deptIds) {
        delete(new LambdaQueryWrapperX<UserDeptDO>()
                .in(UserDeptDO::getDeptId, deptIds));
    }

    default List<UserDeptDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(UserDeptDO::getDeptId, deptIds);
    }

    default void deleteByUserId(Long userId) {
        delete(Wrappers.lambdaUpdate(UserDeptDO.class).eq(UserDeptDO::getUserId, userId));
    }
}
