package com.api.expose.system.api.dict;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.api.expose.framework.common.biz.system.dict.DictDataCommonApi;
import com.api.expose.framework.common.biz.system.dict.dto.DictDataRespDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典数据 API 接口
 *
 */
public interface DictDataApi extends DictDataCommonApi {

    /**
     * 校验字典数据们是否有效。如下情况，视为无效：
     * 1. 字典数据不存在
     * 2. 字典数据被禁用
     *
     * @param dictType 字典类型
     * @param values   字典数据值的数组
     */
    void validateDictDataList(String dictType, Collection<String> values);

    /**
     * 加载数据字典为 Map 格式（标签 -> 值）
     *
     * @param dictType 字典类型
     * @return Map<String, Integer> 标签到值的映射
     */
    default Map<String, String> getDictLabelToValueMap(String dictType) {
        try {
            List<DictDataRespDTO> dictDataList = getDictDataList(dictType);
            if (CollUtil.isEmpty(dictDataList)) {
                return MapUtil.empty();
            }

            return dictDataList.stream().collect(Collectors.toMap(DictDataRespDTO::getLabel, DictDataRespDTO::getValue));
        } catch (Exception e) {
            return MapUtil.empty();
        }
    }

    String getLabelByTypeAndCode(String hrmPostNature, String employeeType);
}
