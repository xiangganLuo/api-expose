package com.api.expose.system.convert.user;

import com.api.expose.system.controller.admin.dept.vo.post.PostImportExcelVO;
import com.api.expose.system.dal.dataobject.dept.PostDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 岗位 Convert
 */
@Mapper
public interface PostConvert {

    PostConvert INSTANCE = Mappers.getMapper(PostConvert.class);

    PostDO convert(PostImportExcelVO bean);

} 