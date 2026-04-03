package com.api.expose.system.util;

import com.api.expose.system.annotation.PostNamesStrField;
import com.api.expose.system.dal.dataobject.dept.PostDO;
import com.api.expose.system.service.dept.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostNamesStrFieldProcessor {

    @Autowired
    private PostService postService;

    /**
     * 自动填充所有加了@PostNamesStrField注解的字段
     */
    public <T> void process(T vo) {
        if (vo == null) return;
        Class<?> clazz = vo.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            PostNamesStrField annotation = field.getAnnotation(PostNamesStrField.class);
            if (annotation != null) {
                String idFieldName = annotation.idField();
                try {
                    Field idField = clazz.getDeclaredField(idFieldName);
                    idField.setAccessible(true);
                    Object idValue = idField.get(vo);
                    List<Long> postIds = null;
                    if (idValue instanceof Collection) {
                        postIds = new ArrayList<>();
                        for (Object o : (Collection<?>) idValue) {
                            if (o != null) postIds.add(Long.valueOf(o.toString()));
                        }
                    }
                    if (postIds != null && !postIds.isEmpty()) {
                        List<PostDO> posts = postService.getPostList(postIds);
                        String namesStr = posts.stream().map(PostDO::getName).collect(Collectors.joining(","));
                        field.setAccessible(true);
                        field.set(vo, namesStr);
                    } else {
                        field.setAccessible(true);
                        field.set(vo, "");
                    }
                } catch (Exception e) {
                    // 你可以加日志
                    e.printStackTrace();
                }
            }
        }
    }
}