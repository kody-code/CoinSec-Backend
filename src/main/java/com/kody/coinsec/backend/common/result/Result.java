package com.kody.coinsec.backend.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Result<T> {

    @Schema(description = "状态码: 200-成功, 401-未登录, 404-未找到, 500-服务器错误")
    private int code;

    @Schema(description = "提示信息")
    private String msg;

    @Schema(description = "返回数据")
    private T data;

    private Result() {}

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }

    public static <T> Result<T> unauthorized(String msg) {
        return error(401, msg);
    }

    public static <T> Result<T> notFound(String msg) {
        return error(404, msg);
    }
}
