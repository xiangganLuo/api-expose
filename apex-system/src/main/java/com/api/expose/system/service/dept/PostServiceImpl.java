package com.api.expose.system.service.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.api.expose.framework.common.enums.CommonStatusEnum;
import com.api.expose.framework.common.exception.ServiceException;
import com.api.expose.framework.common.pojo.PageResult;
import com.api.expose.framework.common.util.object.BeanUtils;
import com.api.expose.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.api.expose.system.controller.admin.dept.vo.post.PostImportExcelVO;
import com.api.expose.system.controller.admin.dept.vo.post.PostImportRespVO;
import com.api.expose.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.api.expose.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.api.expose.system.dal.dataobject.dept.PostDO;
import com.api.expose.system.dal.mysql.dept.PostMapper;
import com.api.expose.system.enums.BusinessModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;

import static com.api.expose.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.api.expose.framework.common.util.collection.CollectionUtils.convertMap;
import static com.api.expose.system.enums.ErrorCodeConstants.*;

/**
 * 岗位 Service 实现类
 *
 */
@Service
@Validated
@Slf4j
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Override
    public Long createPost(PostSaveReqVO createReqVO) {
        // 校验正确性
        validatePostForCreateOrUpdate(null, createReqVO.getName(), createReqVO.getCode());

        // 插入岗位
        PostDO post = BeanUtils.toBean(createReqVO, PostDO.class);
        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public void updatePost(PostSaveReqVO updateReqVO) {
        // 校验正确性
        validatePostForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getCode());

        // 更新岗位
        PostDO updateObj = BeanUtils.toBean(updateReqVO, PostDO.class);
        postMapper.updateById(updateObj);
    }

    @Override
    public void deletePost(Long id) {
        // 校验是否存在
        validatePostExists(id);
        // 删除岗位
        postMapper.deleteById(id);
    }

    @Override
    public void deletePostList(List<Long> ids) {
        postMapper.deleteByIds(ids);
    }

    private void validatePostForCreateOrUpdate(Long id, String name, String code) {
        // 校验自己存在
        validatePostExists(id);
        // 校验岗位名的唯一性
        validatePostNameUnique(id, name);
        // 校验岗位编码的唯一性
        validatePostCodeUnique(id, code);
    }

    private void validatePostNameUnique(Long id, String name) {
        PostDO post = postMapper.selectByName(name);
        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(POST_NAME_DUPLICATE);
        }
        if (!post.getId().equals(id)) {
            throw exception(POST_NAME_DUPLICATE);
        }
    }

    private void validatePostCodeUnique(Long id, String code) {
        PostDO post = postMapper.selectByCode(code);
        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(POST_CODE_DUPLICATE);
        }
        if (!post.getId().equals(id)) {
            throw exception(POST_CODE_DUPLICATE);
        }
    }

    private void validatePostExists(Long id) {
        if (id == null) {
            return;
        }
        if (postMapper.selectById(id) == null) {
            throw exception(POST_NOT_FOUND);
        }
    }

    @Override
    public List<PostDO> getPostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return postMapper.selectByIds(ids);
    }

    @Override
    public List<PostDO> getPostList(Collection<Long> ids, Collection<Integer> statuses) {
        return postMapper.selectList(ids, statuses);
    }

    @Override
    public PageResult<PostDO> getPostPage(PostPageReqVO reqVO) {
        return postMapper.selectPage(reqVO);
    }

    @Override
    public PostDO getPost(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public void validatePostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<PostDO> posts = postMapper.selectByIds(ids);
        Map<Long, PostDO> postMap = convertMap(posts, PostDO::getId);
        // 校验
        ids.forEach(id -> {
            PostDO post = postMap.get(id);
            if (post == null) {
                throw exception(POST_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(post.getStatus())) {
                throw exception(POST_NOT_ENABLE, post.getName());
            }
        });
    }

    @Override
    public List<PostDO> selectListByCodes(String codes) {
        List<String> codeList = StrUtil.split(codes, StrUtil.COMMA);
        return postMapper.selectList(new LambdaQueryWrapperX<PostDO>()
                .inIfPresent(PostDO::getCode, codeList)
                .inIfPresent(PostDO::getStatus, 0));
    }

    /**
     * 导入岗位列表
     *
     * @param importPosts 岗位列表
     * @param isUpdateSupport 是否支持更新
     * @return 导入结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务，异常则回滚所有导入
    public PostImportRespVO importPostList(List<PostImportExcelVO> importPosts, boolean isUpdateSupport) {
        // 1.1 参数校验
        if (CollUtil.isEmpty(importPosts) ||ObjUtil.isEmpty(importPosts)) {
            throw exception(POST_NOT_FOUND); // 使用框架的异常工具
        }

        // 2. 遍历，逐个创建 or 更新
        PostImportRespVO respVO = PostImportRespVO.builder()
                .createNames(new ArrayList<>())
                .updateNames(new ArrayList<>())
                .failureNames(new ArrayList<>())
                .build();

        importPosts.forEach(importPost -> {
            // 2.1.1 校验字段是否符合要求
            try {
                validatePostData(importPost);
            } catch (ServiceException ex) {
                respVO.getFailureNames().add(importPost.getName() + "（" + ex.getMessage() + "）");
                return;
            }

            // 2.2.2 如果存在，判断是否允许更新
            if (!isUpdateSupport) {
                // 2.1.2 校验，判断是否有不符合的原因
                try {
                    validatePostForCreateOrUpdate(null, importPost.getName(), importPost.getCode());
                } catch (ServiceException ex) {
                    respVO.getFailureNames().add(importPost.getName() + "（" + ex.getMessage() + "）");
                    return;
                }
            }

            // 2.2.1 判断如果不存在，在进行插入
            PostDO existPost = postMapper.selectByCode(importPost.getCode());
            if (existPost == null) {
                // 创建岗位对象
                PostDO newPost = BeanUtils.toBean(importPost, PostDO.class);
                // 插入岗位
                postMapper.insert(newPost);
                respVO.getCreateNames().add(importPost.getName());
                return;
            }
            // 更新岗位
            PostDO updatePost = BeanUtils.toBean(importPost, PostDO.class);
            updatePost.setId(existPost.getId());
            postMapper.updateById(updatePost);
            respVO.getUpdateNames().add(importPost.getName());
        });
        return respVO;
    }

    /**
     * 验证岗位数据
     */
    private void validatePostData(PostImportExcelVO vo) {
        if (ObjUtil.isEmpty(vo.getCode()) || ObjUtil.isEmpty(vo.getName())) {
            throw exception(POST_CODE_NOBALCK);
        }
        // 验证编码格式（只能包含字母、数字、下划线）
        if (!vo.getCode().matches("^[a-zA-Z0-9_]+$")) {
            throw exception(POST_CODE_DUPLICATE);
        }
    }

    @Override
    public List<PostDO> getPostsByBusinessModule(String moduleCode) {
        // 根据业务模块编码获取对应的表名
        BusinessModuleEnum moduleEnum = BusinessModuleEnum.getByCode(moduleCode);
        // 连表查询业务表中实际使用的岗位，去重且只返回启用的岗位
        return postMapper.selectPostsByTable(moduleEnum.getTableName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostDO findOrCreatePost(String postName) {
        String trimmedPostName = postName.trim();
        
        // 1. 先完全匹配（不区分大小写）
        List<PostDO> allPosts = this.postMapper.selectList();
        for (PostDO post : allPosts) {
            if (trimmedPostName.equalsIgnoreCase(post.getName())) {
                log.info("岗位完全匹配成功：{} -> {}", postName, post.getName());
                return post;
            }
        }
        
        // 2. 模糊匹配（包含关系）
        for (PostDO post : allPosts) {
            if (post.getName().contains(trimmedPostName)) {
                log.info("岗位模糊匹配成功：{} -> {}", postName, post.getName());
                return post;
            }
        }
        
        // 3. 都没有找到，新增岗位
        log.info("未找到匹配的岗位，将新增岗位：{}", postName);
        return createNewPost(trimmedPostName);
    }
    
    /**
     * 创建新岗位
     * 
     * @param postName 岗位名称
     * @return 新创建的岗位信息
     */
    private PostDO createNewPost(String postName) {
        try {
            // 构造岗位创建请求
            PostSaveReqVO createReqVO = new PostSaveReqVO();
            createReqVO.setName(postName);
            createReqVO.setCode(generatePostCode(postName));
            createReqVO.setSort(999);
            createReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
            createReqVO.setRemark("由招聘需求导入自动创建");

            // 调用PostService创建岗位
            Long postId = createPost(createReqVO);
            
            // 查询并返回创建的岗位信息
            PostDO newPost = getPost(postId);
            log.info("新岗位创建成功：ID={}, 名称={}", postId, postName);
            return newPost;
        } catch (Exception e) {
            log.error("创建岗位失败：{}", postName, e);
            throw exception(POST_CODE_DUPLICATE, postName, e.getMessage());
        }
    }
    
    /**
     * 生成岗位编码
     * 
     * @param postName 岗位名称
     * @return 岗位编码
     */
    private String generatePostCode(String postName) {
        // 简单的编码生成策略：去除特殊字符 + 时间戳
        String baseCode = postName.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "");
        if (baseCode.length() > 10) {
            baseCode = baseCode.substring(0, 10);
        }
        return baseCode + "_" + System.currentTimeMillis() % 10000;
    }

}
