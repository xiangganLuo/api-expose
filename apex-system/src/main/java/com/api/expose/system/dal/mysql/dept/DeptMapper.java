package com.api.expose.system.dal.mysql.dept;

import com.api.expose.framework.mybatis.core.mapper.BaseMapperX;
import com.api.expose.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.api.expose.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.api.expose.system.dal.dataobject.dept.DeptDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface DeptMapper extends BaseMapperX<DeptDO> {

    default List<DeptDO> selectList(DeptListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<DeptDO>()
                .likeIfPresent(DeptDO::getName, reqVO.getName())
                .eqIfPresent(DeptDO::getStatus, reqVO.getStatus()));
    }

    default DeptDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(DeptDO::getParentId, parentId, DeptDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(DeptDO::getParentId, parentId);
    }

    default List<DeptDO> selectListByParentId(Collection<Long> parentIds) {
        return selectList(DeptDO::getParentId, parentIds);
    }

    default List<DeptDO> selectListByLeaderUserId(Long id) {
        return selectList(DeptDO::getLeaderUserIds, id);
    }

    /**
     * 根据业务表查询实际使用的部门
     * 连表查询业务表中存在的部门，去重且只返回启用的部门
     * 
     * @param tableName 业务表名
     * @return 部门列表
     */
    List<DeptDO> selectDeptsByTable(@Param("tableName") String tableName);

}
