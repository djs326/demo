package com.example.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    RateLimitType type() default RateLimitType.DEFAULT;

    int limit() default 10;

    long refreshPeriod() default 1;

    String message() default "请求过于频繁，请稍后再试";
}
