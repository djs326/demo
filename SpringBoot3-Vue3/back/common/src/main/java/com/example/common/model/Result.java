package com.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String message;
    private T data;
    private String traceId;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return success("0", "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return success("0", message, data);
    }

    public static <T> Result<T> success(String code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setTraceId(MDC.get("traceId"));
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error("0003001", message);
    }

    public static <T> Result<T> error(String code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        result.setTraceId(MDC.get("traceId"));
        return result;
    }

    public static <T> Result<T> error(String code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setTraceId(MDC.get("traceId"));
        return result;
    }
}
