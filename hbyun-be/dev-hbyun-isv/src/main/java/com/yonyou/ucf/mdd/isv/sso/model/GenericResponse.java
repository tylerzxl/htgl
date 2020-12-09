package com.yonyou.ucf.mdd.isv.sso.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * <p>开放平台接口响应</p>
 * <p>json 定义:
 *
 * <pre>
 * {
 *      "code": "00000",
 *      "message": "成功！",
 *      "data": {}
 * }
 * </pre>
 *
 * 请根据 {@code code} 判断是否请求成功，若 {@code code} 为 {@code "00000"} 则请求成功，请求数据在 {@code data} 中；
 * 若请求失败，{@code message} 中有详细错误信息，此时 {@code data} 字段不存在。
 *
 * @param <T> 具体业务数据
 * </p>
 *
 * @Author chouhl
 * @Date 2020-03-31$ 14:34$
 * @Version 1.0
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse<T> {

    public static final String SUCCESS_CODE = "00000";

    private String code;

    private String message;

    private T data;

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

}
