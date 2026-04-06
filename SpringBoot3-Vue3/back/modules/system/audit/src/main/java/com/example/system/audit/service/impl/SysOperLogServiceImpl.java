package com.example.system.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.audit.entity.SysOperLog;
import com.example.system.audit.entity.SysLoginLog;
import com.example.system.audit.mapper.SysOperLogMapper;
import com.example.system.audit.mapper.SysLoginLogMapper;
import com.example.system.audit.service.SysOperLogService;
import com.example.system.audit.dto.OperLogQueryDTO;
import com.example.system.audit.dto.LoginLogQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Autowired
    private SysOperLogMapper sysOperLogMapper;

    @Autowired
    private SysLoginLogMapper sysLoginLogMapper;

    @Override
    public void saveOperLog(SysOperLog operLog) {
        operLog.setOperTime(LocalDateTime.now());
        sysOperLogMapper.insert(operLog);
    }

    @Override
    public Page<SysOperLog> queryOperLogList(OperLogQueryDTO queryDTO) {
        Page<SysOperLog> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getOperName() != null && !queryDTO.getOperName().isEmpty()) {
            wrapper.like(SysOperLog::getOperName, queryDTO.getOperName());
        }

        if (queryDTO.getTitle() != null && !queryDTO.getTitle().isEmpty()) {
            wrapper.like(SysOperLog::getTitle, queryDTO.getTitle());
        }

        if (queryDTO.getBusinessType() != null) {
            wrapper.eq(SysOperLog::getBusinessType, queryDTO.getBusinessType());
        }

        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysOperLog::getStatus, queryDTO.getStatus());
        }

        if (queryDTO.getStartTime() != null) {
            wrapper.ge(SysOperLog::getOperTime, queryDTO.getStartTime());
        }

        if (queryDTO.getEndTime() != null) {
            wrapper.le(SysOperLog::getOperTime, queryDTO.getEndTime());
        }

        wrapper.orderByDesc(SysOperLog::getOperTime);
        return sysOperLogMapper.selectPage(page, wrapper);
    }

    @Override
    public void saveLoginLog(SysLoginLog loginLog) {
        loginLog.setLoginTime(LocalDateTime.now());
        sysLoginLogMapper.insert(loginLog);
    }

    @Override
    public Page<SysLoginLog> queryLoginLogList(LoginLogQueryDTO queryDTO) {
        Page<SysLoginLog> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
            wrapper.like(SysLoginLog::getUsername, queryDTO.getUsername());
        }

        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysLoginLog::getStatus, queryDTO.getStatus());
        }

        if (queryDTO.getIpaddr() != null && !queryDTO.getIpaddr().isEmpty()) {
            wrapper.like(SysLoginLog::getIpaddr, queryDTO.getIpaddr());
        }

        if (queryDTO.getStartTime() != null) {
            wrapper.ge(SysLoginLog::getLoginTime, queryDTO.getStartTime());
        }

        if (queryDTO.getEndTime() != null) {
            wrapper.le(SysLoginLog::getLoginTime, queryDTO.getEndTime());
        }

        wrapper.orderByDesc(SysLoginLog::getLoginTime);
        return sysLoginLogMapper.selectPage(page, wrapper);
    }
}
