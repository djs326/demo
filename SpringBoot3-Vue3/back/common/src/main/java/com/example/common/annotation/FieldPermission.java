package com.example.common.annotation;

import com.example.common.enums.MaskType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldPermission {
    String tableName() default "";
    String fieldName() default "";
    boolean hidden() default false;
    boolean mask() default false;
    MaskType maskType() default MaskType.DEFAULT;
    String[] roles() default {};
}