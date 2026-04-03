package com.api.expose.system.api.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.api.expose.framework.common.util.collection.CollectionUtils;
import com.api.expose.system.api.dept.dto.PostRespDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 岗位 API 接口
 *
 */
public interface PostApi {

    /**
     * 校验岗位们是否有效。如下情况，视为无效：
     * 1. 岗位编号不存在
     * 2. 岗位被禁用
     *
     * @param ids 岗位编号数组
     */
    void validPostList(Collection<Long> ids);

    List<PostRespDTO> getPostList(Collection<Long> ids);

    default Map<Long, PostRespDTO> getPostMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return MapUtil.empty();
        }

        List<PostRespDTO> list = getPostList(ids);
        return CollectionUtils.convertMap(list, PostRespDTO::getId);
    }

    default PostRespDTO getPostByPostName(String postName) {
        Map<String, PostRespDTO> postNameMap = getPostList(null).stream().collect(Collectors.toMap(PostRespDTO::getName, post -> post, (existing, replacement) -> existing));
        return postNameMap.get(postName);
    }

    /**
     * 岗位查找或创建：三级匹配策略
     * 1. 完全匹配（不区分大小写）
     * 2. 模糊匹配
     * 3. 新增岗位
     * 
     * @param postName 岗位名称
     * @return 岗位信息
     */
    PostRespDTO findOrCreatePost(String postName);
}
