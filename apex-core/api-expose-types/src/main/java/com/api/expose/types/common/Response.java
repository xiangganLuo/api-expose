package com.api.expose.types.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应封装类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = -7012586228221565507L;

    /** 响应码 */
    private String code;
    /** 响应信息 */
    private String info;
    /** 响应数据 */
    private T data;

    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .code("0000")
                .info("成功")
                .data(data)
                .build();
    }

    public static <T> Response<T> failure(String code, String info) {
        return Response.<T>builder()
                .code(code)
                .info(info)
                .build();
    }
}
