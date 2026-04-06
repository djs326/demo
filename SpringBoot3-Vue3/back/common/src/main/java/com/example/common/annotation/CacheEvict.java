package com.example.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {
    String cacheName() default "default";
    String key() default "";
    boolean allEntries() default false;
    boolean beforeInvocation() default false;
}
