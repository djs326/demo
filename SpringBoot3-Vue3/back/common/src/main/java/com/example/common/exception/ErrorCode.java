package com.example.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS("0", "success"),

    PARAM_ERROR("0001001", "请求参数无效"),
    BUSINESS_ERROR("0002001", "业务逻辑错误"),
    SYSTEM_ERROR("0003001", "系统内部错误"),
    AUTH_ERROR("0004001", "权限错误"),
    RESOURCE_ERROR("0005001", "资源错误"),
    NETWORK_ERROR("0006001", "网络错误"),

    USER_PARAM_ERROR("0101001", "用户名格式错误"),
    USER_NOT_FOUND("0102001", "用户不存在"),
    USER_DISABLED("0102002", "用户已禁用"),
    PASSWORD_ERROR("0102003", "密码错误"),
    USER_AUTH_ERROR("0104001", "用户无权限"),
    ROLE_AUTH_ERROR("0204001", "角色权限不足"),
    LOGIN_PARAM_ERROR("0301001", "登录参数错误"),
    TOKEN_EXPIRED("0304001", "Token过期"),
    TOKEN_INVALID("0304002", "Token无效"),
    
    RATE_LIMIT_EXCEEDED("0400001", "请求过于频繁，请稍后再试"),
    IP_RATE_LIMIT_EXCEEDED("0401001", "IP请求过于频繁，请稍后再试"),
    USER_RATE_LIMIT_EXCEEDED("0402001", "用户请求过于频繁，请稍后再试"),
    API_RATE_LIMIT_EXCEEDED("0403001", "接口请求过于频繁，请稍后再试"),
    IDEMPOTENT_ERROR("0404001", "重复请求，请稍后再试");

    private final String code;
    private final String message;
}
