package com.api.expose.system.dal.mysql.dept;

import com.api.expose.framework.mybatis.core.mapper.BaseMapperX;
import com.api.expose.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.api.expose.system.dal.dataobject.dept.UserPostDO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserPostMapper extends BaseMapperX<UserPostDO> {

    default List<UserPostDO> selectListByUserId(Long userId) {
        return selectList(UserPostDO::getUserId, userId);
    }

    default void deleteByUserIdAndPostId(Long userId, Collection<Long> postIds) {
        delete(new LambdaQueryWrapperX<UserPostDO>()
                .eq(UserPostDO::getUserId, userId)
                .in(UserPostDO::getPostId, postIds));
    }

    default List<UserPostDO> selectListByPostIds(Collection<Long> postIds) {
        return selectList(UserPostDO::getPostId, postIds);
    }

    default void deleteByUserId(Long userId) {
        delete(Wrappers.lambdaUpdate(UserPostDO.class).eq(UserPostDO::getUserId, userId));
    }

    /**
     * 根据岗位名称模糊查询用户ID列表
     *
     * @param postname 岗位名称
     * @return 用户ID列表
     */
    @Select("SELECT DISTINCT up.user_id FROM system_user_post up " +
            "INNER JOIN system_post p ON p.id = up.post_id " +
            "WHERE p.name LIKE CONCAT('%', #{postname}, '%') " +
            "AND up.deleted = 0 AND p.deleted = 0 and up.tenant_id = 1 and p.tenant_id = 1")
    List<Long> selectUserIdsByPostName(String postname);
}
