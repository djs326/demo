package com.example.system.audit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.model.Result;
import com.example.system.audit.entity.SysOperLog;
import com.example.system.audit.entity.SysLoginLog;
import com.example.system.audit.service.SysOperLogService;
import com.example.system.audit.dto.OperLogQueryDTO;
import com.example.system.audit.dto.LoginLogQueryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "日志管理", description = "操作日志和登录日志相关接口")
@RestController
@RequestMapping("/audit")
public class SysOperLogController {

    @Autowired
    private SysOperLogService sysOperLogService;

    @Operation(summary = "查询操作日志列表")
    @GetMapping("/oper-log/list")
    public Result<Page<SysOperLog>> listOperLog(OperLogQueryDTO queryDTO) {
        Page<SysOperLog> page = sysOperLogService.queryOperLogList(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "查询登录日志列表")
    @GetMapping("/login-log/list")
    public Result<Page<SysLoginLog>> listLoginLog(LoginLogQueryDTO queryDTO) {
        Page<SysLoginLog> page = sysOperLogService.queryLoginLogList(queryDTO);
        return Result.success(page);
    }
}
