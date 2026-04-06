package com.example.common.aspect;

import com.example.common.annotation.OperLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class OperLogAspect {

    @Pointcut("@annotation(com.example.common.annotation.OperLog)")
    public void operLogPointCut() {
    }

    @AfterReturning(pointcut = "operLogPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        recordLog(joinPoint, null, result);
    }

    @AfterThrowing(pointcut = "operLogPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        recordLog(joinPoint, e, null);
    }

    private void recordLog(JoinPoint joinPoint, Exception e, Object result) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperLog operLog = method.getAnnotation(OperLog.class);

        // 记录日志信息到控制台（暂时不保存到数据库，避免循环依赖）
        System.out.println("=== 操作日志 ===");
        System.out.println("标题: " + operLog.title());
        System.out.println("业务类型: " + operLog.businessType());
        System.out.println("方法: " + method.getDeclaringClass().getName() + "." + method.getName());
        System.out.println("请求方法: " + request.getMethod());
        System.out.println("操作人类型: " + operLog.operatorType());
        System.out.println("操作URL: " + request.getRequestURI());
        System.out.println("操作IP: " + request.getRemoteAddr());
        System.out.println("操作参数: " + Arrays.toString(joinPoint.getArgs()));
        System.out.println("操作时间: " + LocalDateTime.now());
        System.out.println("操作人: admin");
        System.out.println("部门: 技术部");

        if (e != null) {
            System.out.println("状态: 失败");
            System.out.println("错误信息: " + e.getMessage());
        } else {
            System.out.println("状态: 成功");
            System.out.println("返回结果: " + (result != null ? result.toString() : ""));
        }
    }
}