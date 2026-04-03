package com.api.expose.system.api.dict;

import com.api.expose.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.api.expose.framework.common.util.object.BeanUtils;
import com.api.expose.system.dal.dataobject.dict.DictDataDO;
import com.api.expose.system.service.dict.DictDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 字典数据 API 实现类
 *
 */
@Service
public class DictDataApiImpl implements DictDataApi {

    @Resource
    private DictDataService dictDataService;

    @Override
    public void validateDictDataList(String dictType, Collection<String> values) {
        dictDataService.validateDictDataList(dictType, values);
    }

    @Override
    public String getLabelByTypeAndCode(String dictType, String value) {
        DictDataDO dictData = dictDataService.getDictData(dictType, value);
        return dictData != null ? dictData.getLabel() : null;
    }

    @Override
    public List<DictDataRespDTO> getDictDataList(String dictType) {
        List<DictDataDO> list = dictDataService.getDictDataListByDictType(dictType);
        return BeanUtils.toBean(list, DictDataRespDTO.class);
    }

}