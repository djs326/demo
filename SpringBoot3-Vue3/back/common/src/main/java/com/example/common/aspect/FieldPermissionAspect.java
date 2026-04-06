package com.example.common.aspect;

import com.example.common.annotation.FieldPermission;
import com.example.common.enums.MaskType;
import com.example.common.utils.MaskUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Aspect
@Component
public class FieldPermissionAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Object getBean(String beanName) {
        if (applicationContext != null && applicationContext.containsBean(beanName)) {
            return applicationContext.getBean(beanName);
        }
        return null;
    }

    @Pointcut("execution(* com.example..*Controller.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object handleFieldPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result != null) {
            List<Long> roleIds = getCurrentUserRoleIds();
            Map<String, Map<String, String>> fieldPermissionMap = getFieldPermissionMap(roleIds);
            processObject(result, fieldPermissionMap, roleIds);
        }
        return result;
    }

    private List<Long> getCurrentUserRoleIds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            Long userId = (Long) authentication.getPrincipal();
            Object sysUserRoleMapper = getBean("sysUserRoleMapper");
            if (sysUserRoleMapper != null) {
                try {
                    java.lang.reflect.Method method = sysUserRoleMapper.getClass().getMethod("selectRoleIdsByUserId", Long.class);
                    @SuppressWarnings("unchecked")
                    List<Long> result = (List<Long>) method.invoke(sysUserRoleMapper, userId);
                    return result != null ? result : Collections.emptyList();
                } catch (Exception e) {
                    return Collections.emptyList();
                }
            }
        }
        return Collections.emptyList();
    }

    private Map<String, Map<String, String>> getFieldPermissionMap(List<Long> roleIds) {
        if (!roleIds.isEmpty()) {
            Object fieldPermissionCacheService = getBean("fieldPermissionCacheService");
            if (fieldPermissionCacheService != null) {
                try {
                    java.lang.reflect.Method method = fieldPermissionCacheService.getClass().getMethod("getFieldPermissionsByRoleIds", List.class);
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, String>> result = (Map<String, Map<String, String>>) method.invoke(fieldPermissionCacheService, roleIds);
                    return result != null ? result : Collections.emptyMap();
                } catch (Exception e) {
                    return Collections.emptyMap();
                }
            }
        }
        return Collections.emptyMap();
    }

    private void processObject(Object obj, Map<String, Map<String, String>> fieldPermissionMap, List<Long> roleIds) throws IllegalAccessException {
        if (obj == null) {
            return;
        }

        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            for (Object item : list) {
                processObject(item, fieldPermissionMap, roleIds);
            }
            return;
        }

        Class<?> clazz = obj.getClass();
        String tableName = getTableName(clazz);
        Map<String, String> tableFieldPermissions = fieldPermissionMap.getOrDefault(tableName, Collections.emptyMap());
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            FieldPermission annotation = field.getAnnotation(FieldPermission.class);
            String fieldName = field.getName();
            
            if (annotation != null) {
                String permissionType = getPermissionType(annotation, tableFieldPermissions, fieldName, roleIds);
                
                switch (permissionType) {
                    case "hidden":
                        field.set(obj, null);
                        break;
                    case "mask":
                        applyMask(field, obj, annotation);
                        break;
                    case "readonly":
                    case "visible":
                    default:
                        break;
                }
            }
            
            Object value = field.get(obj);
            if (value != null && !isPrimitive(value.getClass())) {
                processObject(value, fieldPermissionMap, roleIds);
            }
        }
    }

    private String getTableName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        return camelToUnderscore(className);
    }

    private String camelToUnderscore(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(str.charAt(0)));
        for (int i = 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String getPermissionType(FieldPermission annotation, Map<String, String> tableFieldPermissions, String fieldName, List<Long> roleIds) {
        String dbPermissionType = tableFieldPermissions.get(fieldName);
        if (dbPermissionType != null) {
            return dbPermissionType;
        }
        
        if (annotation.hidden()) {
            return "hidden";
        }
        if (annotation.mask()) {
            return "mask";
        }
        
        String[] allowedRoles = annotation.roles();
        if (allowedRoles.length > 0) {
            boolean hasRole = false;
            for (String role : allowedRoles) {
                if (roleIds.contains(Long.parseLong(role))) {
                    hasRole = true;
                    break;
                }
            }
            if (!hasRole) {
                return "hidden";
            }
        }
        
        return "visible";
    }

    private void applyMask(Field field, Object obj, FieldPermission annotation) throws IllegalAccessException {
        Object value = field.get(obj);
        if (value != null && value instanceof String) {
            MaskType maskType = annotation.maskType();
            String maskedValue = MaskUtils.mask((String) value, maskType);
            field.set(obj, maskedValue);
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