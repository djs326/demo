package com.example.common.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

        // 获取当前用户的部门ID和角色信息
        // 这里需要根据实际的用户获取方式进行调整
        Long currentUserId = 1L; // 暂时使用固定值
        Long currentDeptId = 1L; // 暂时使用固定值

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof LambdaQueryWrapper<?>) {
                LambdaQueryWrapper<?> wrapper = (LambdaQueryWrapper<?>) arg;
                applyDataScope(wrapper, currentUserId, currentDeptId, dataScope);
            } else if (arg instanceof IPage<?>) {
                IPage<?> page = (IPage<?>) arg;
                if (page.getCondition() instanceof LambdaQueryWrapper<?>) {
                    LambdaQueryWrapper<?> wrapper = (LambdaQueryWrapper<?>) page.getCondition();
                    applyDataScope(wrapper, currentUserId, currentDeptId, dataScope);
                }
            }
        }
    }

    private void applyDataScope(LambdaQueryWrapper<?> wrapper, Long currentUserId, Long currentDeptId, DataScope dataScope) {
        String deptAlias = dataScope.deptAlias();
        String userAlias = dataScope.userAlias();

        // 这里需要根据实际的权限获取逻辑进行调整
        // 暂时使用固定的权限类型
        String scopeType = "ALL";

        switch (scopeType) {
            case "DEPT":
                if (!deptAlias.isEmpty()) {
                    wrapper.eq(deptAlias + ".dept_id", currentDeptId);
                }
                break;
            case "DEPT_AND_CHILD":
                if (!deptAlias.isEmpty()) {
                    wrapper.apply(deptAlias + ".dept_id IN (SELECT descendant_id FROM sys_dept_closure WHERE ancestor_id = {0})", currentDeptId);
                }
                break;
            case "SELF":
                if (!userAlias.isEmpty()) {
                    wrapper.eq(userAlias + ".create_by", currentUserId);
                }
                break;
            case "CUSTOM":
                // 这里需要根据实际的自定义部门权限逻辑进行调整
                break;
            case "ALL":
            default:
                // 不需要添加条件
                break;
        }
    }
}