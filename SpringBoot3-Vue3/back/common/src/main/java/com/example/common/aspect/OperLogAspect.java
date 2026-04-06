package com.example.common.aspect;

import com.example.common.annotation.OperLog;
import com.example.system.audit.entity.SysOperLog;
import com.example.system.audit.service.SysOperLogService;
import com.example.system.user.entity.SysUser;
import com.example.system.user.service.SysUserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class OperLogAspect {

    private final SysOperLogService sysOperLogService;
    private final SysUserService sysUserService;

    public OperLogAspect(SysOperLogService sysOperLogService, SysUserService sysUserService) {
        this.sysOperLogService = sysOperLogService;
        this.sysUserService = sysUserService;
    }

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

        SysOperLog log = new SysOperLog();
        log.setTitle(operLog.title());
        log.setBusinessType(operLog.businessType());
        log.setMethod(method.getDeclaringClass().getName() + "." + method.getName());
        log.setRequestMethod(request.getMethod());
        log.setOperatorType(operLog.operatorType());
        log.setOperUrl(request.getRequestURI());
        log.setOperIp(request.getRemoteAddr());
        log.setOperParam(Arrays.toString(joinPoint.getArgs()));
        log.setOperTime(LocalDateTime.now());

        // 获取当前用户信息
        // 这里需要根据实际的用户获取方式进行调整
        // 暂时使用固定值
        log.setOperName("admin");
        log.setDeptName("技术部");

        if (e != null) {
            log.setStatus(1);
            log.setErrorMsg(e.getMessage());
        } else {
            log.setStatus(0);
            log.setJsonResult(result != null ? result.toString() : "");
        }

        sysOperLogService.save(log);
    }
}