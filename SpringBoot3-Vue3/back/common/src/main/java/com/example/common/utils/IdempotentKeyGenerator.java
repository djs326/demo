package com.example.common.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.example.common.annotation.Idempotent;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class IdempotentKeyGenerator {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    public static String generate(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HttpServletRequest request = getRequest();

        String key;
        switch (idempotent.type()) {
            case SPEL:
                key = parseSpel(joinPoint, idempotent.key());
                break;
            case CUSTOM:
                key = idempotent.key();
                break;
            case DEFAULT:
            default:
                key = generateDefaultKey(joinPoint, idempotent, request);
                break;
        }

        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Idempotent key cannot be empty");
        }

        String prefix = idempotent.prefix();
        String delimiter = idempotent.delimiter();

        String fullKey = prefix + key;
        return DigestUtil.md5Hex(fullKey);
    }

    private static String generateDefaultKey(ProceedingJoinPoint joinPoint, Idempotent idempotent, HttpServletRequest request) {
        Idempotent.KeyLocation keyLocation = idempotent.keyLocation();
        String keyName = idempotent.keyName();

        if (StringUtils.hasText(keyName)) {
            switch (keyLocation) {
                case HEADER:
                    return request.getHeader(keyName);
                case PARAM:
                    return request.getParameter(keyName);
                case PATH:
                    return getPathVariable(request, keyName);
                case BODY:
                    return getBodyParameter(joinPoint, keyName);
                case DEFAULT:
                default:
                    break;
            }
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        String uri = request.getRequestURI();
        String token = request.getHeader("Authorization");
        Object[] args = joinPoint.getArgs();

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(methodName)
                  .append(":")
                  .append(uri);

        if (StringUtils.hasText(token)) {
            keyBuilder.append(":").append(token);
        }

        if (!ObjectUtils.isEmpty(args)) {
            for (Object arg : args) {
                if (arg != null) {
                    keyBuilder.append(":").append(arg.hashCode());
                }
            }
        }

        return keyBuilder.toString();
    }

    private static String parseSpel(ProceedingJoinPoint joinPoint, String spel) {
        if (!StringUtils.hasText(spel)) {
            throw new IllegalArgumentException("SpEL expression cannot be empty");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameters.length; i++) {
            context.setVariable(parameters[i].getName(), args[i]);
        }

        Expression expression = PARSER.parseExpression(spel);
        Object value = expression.getValue(context);
        return value != null ? value.toString() : "";
    }

    private static String getPathVariable(HttpServletRequest request, String keyName) {
        return request.getAttribute(keyName) != null ? request.getAttribute(keyName).toString() : "";
    }

    private static String getBodyParameter(ProceedingJoinPoint joinPoint, String keyName) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                try {
                    Method getter = arg.getClass().getMethod("get" + capitalize(keyName));
                    Object value = getter.invoke(arg);
                    if (value != null) {
                        return value.toString();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return "";
    }

    private static String capitalize(String str) {
        if (!StringUtils.hasText(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No HttpServletRequest available");
        }
        return attributes.getRequest();
    }
}
