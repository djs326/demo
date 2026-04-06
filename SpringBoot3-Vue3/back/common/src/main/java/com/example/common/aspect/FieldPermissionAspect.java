package com.example.common.aspect;

import com.example.common.annotation.FieldPermission;
import com.example.common.utils.MaskUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class FieldPermissionAspect {

    @Pointcut("execution(* com.example..*Controller.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object handleFieldPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result != null) {
            processObject(result);
        }
        return result;
    }

    private void processObject(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return;
        }

        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            for (Object item : list) {
                processObject(item);
            }
            return;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            FieldPermission annotation = field.getAnnotation(FieldPermission.class);
            if (annotation != null) {
                // 处理隐藏字段
                if (annotation.hidden()) {
                    field.set(obj, null);
                }
                // 处理脱敏字段
                else if (annotation.mask()) {
                    Object value = field.get(obj);
                    if (value != null && value instanceof String) {
                        String maskedValue = MaskUtils.mask((String) value, annotation.maskType());
                        field.set(obj, maskedValue);
                    }
                }
            }
            // 递归处理嵌套对象
            Object value = field.get(obj);
            if (value != null && !isPrimitive(value.getClass())) {
                processObject(value);
            }
        }
    }

    private boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == String.class || 
               clazz == Integer.class || 
               clazz == Long.class || 
               clazz == Double.class || 
               clazz == Float.class || 
               clazz == Boolean.class || 
               clazz == Byte.class || 
               clazz == Short.class || 
               clazz == Character.class || 
               clazz.isEnum() || 
               clazz == java.util.Date.class || 
               clazz == java.time.LocalDateTime.class || 
               clazz == java.time.LocalDate.class || 
               clazz == java.time.LocalTime.class;
    }
}