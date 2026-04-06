package com.example.system.audit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.audit.entity.SysOperLog;
import com.example.system.audit.entity.SysLoginLog;
import com.example.system.audit.dto.OperLogQueryDTO;
import com.example.system.audit.dto.LoginLogQueryDTO;

public interface SysOperLogService {

    void saveOperLog(SysOperLog operLog);

    Page<SysOperLog> queryOperLogList(OperLogQueryDTO queryDTO);

    void saveLoginLog(SysLoginLog loginLog);

    Page<SysLoginLog> queryLoginLogList(LoginLogQueryDTO queryDTO);
}
