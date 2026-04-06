package com.example.common.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.annotation.DataScope;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
public class DataScopeAspect {

    public static final String DATA_SCOPE_ALL = "ALL";
    public static final String DATA_SCOPE_CUSTOM = "CUSTOM";
    public static final String DATA_SCOPE_DEPT = "DEPT";
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "DEPT_AND_CHILD";
    public static final String DATA_SCOPE_SELF = "SELF";

    @Pointcut("@annotation(com.example.common.annotation.DataScope)")
    public void dataScopePointCut() {
    }

    @Before("dataScopePointCut()")
    public void doBefore(JoinPoint joinPoint) {
        handleDataScope(joinPoint);
    }

    private void handleDataScope(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataScope dataScope = method.getAnnotation(DataScope.class);

        if (dataScope == null) {
            return;
        }

        Long currentUserId = getCurrentUserId();
        Long currentDeptId = getCurrentDeptId();
        String scopeType = getDataScopeType();
        List<Long> customDeptIds = getCustomDeptIds();

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof LambdaQueryWrapper<?>) {
                LambdaQueryWrapper<?> wrapper = (LambdaQueryWrapper<?>) arg;
                applyDataScope(wrapper, currentUserId, currentDeptId, scopeType, customDeptIds, dataScope);
            } else if (arg instanceof IPage<?>) {
                IPage<?> page = (IPage<?>) arg;
                if (page.getCondition() instanceof LambdaQueryWrapper<?>) {
                    LambdaQueryWrapper<?> wrapper = (LambdaQueryWrapper<?>) page.getCondition();
                    applyDataScope(wrapper, currentUserId, currentDeptId, scopeType, customDeptIds, dataScope);
                }
            }
        }
    }

    private void applyDataScope(LambdaQueryWrapper<?> wrapper, Long currentUserId, Long currentDeptId, 
                                String scopeType, List<Long> customDeptIds, DataScope dataScope) {
        String deptAlias = dataScope.deptAlias();
        String userAlias = dataScope.userAlias();

        switch (scopeType) {
            case DATA_SCOPE_ALL:
                break;
            case DATA_SCOPE_CUSTOM:
                if (!deptAlias.isEmpty() && customDeptIds != null && !customDeptIds.isEmpty()) {
                    wrapper.in(deptAlias + ".dept_id", customDeptIds);
                }
                break;
            case DATA_SCOPE_DEPT:
                if (!deptAlias.isEmpty()) {
                    wrapper.eq(deptAlias + ".dept_id", currentDeptId);
                }
                break;
            case DATA_SCOPE_DEPT_AND_CHILD:
                if (!deptAlias.isEmpty()) {
                    wrapper.apply(deptAlias + ".dept_id IN (SELECT descendant_id FROM sys_dept_closure WHERE ancestor_id = {0})", currentDeptId);
                }
                break;
            case DATA_SCOPE_SELF:
                if (!userAlias.isEmpty()) {
                    wrapper.eq(userAlias + ".create_by", currentUserId);
                }
                break;
            default:
                break;
        }
    }

    private Long getCurrentUserId() {
        return 1L;
    }

    private Long getCurrentDeptId() {
        return 1L;
    }

    private String getDataScopeType() {
        return DATA_SCOPE_ALL;
    }

    private List<Long> getCustomDeptIds() {
        return null;
    }
}