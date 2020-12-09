package com.yonyou.ucf.mdf.app.mobile.web.controller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {

    private String message;
    private String code;

    /**
     * 200 success
     */
    private Boolean success;

    private Object data;

    public static CommonResponse success() {
        return new CommonResponse("", "200", true, Collections.emptyMap());
    }

    public static CommonResponse noOp() {
        return new CommonResponse("", "NO_OPERATION", true, Collections.emptyMap());
    }

    public static CommonResponse success(Object data) {
        return new CommonResponse("", "200", true, data);
    }

    public static CommonResponse failed(String message, String code) {
        return new CommonResponse(message, code, false, Collections.emptyMap());
    }

}
