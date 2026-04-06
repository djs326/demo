package com.example.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CachePut {
    String cacheName() default "default";
    String key() default "";
    long expire() default 30;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
