package com.example.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    String key() default "";

    String prefix() default "idempotent:";

    String delimiter() default ":";

    long expire() default 60;

    String message() default "重复请求，请稍后再试";

    Type type() default Type.DEFAULT;

    KeyLocation keyLocation() default KeyLocation.DEFAULT;

    String keyName() default "";

    enum Type {
        DEFAULT,
        SPEL,
        CUSTOM
    }

    enum KeyLocation {
        DEFAULT,
        HEADER,
        PARAM,
        PATH,
        BODY
    }
}