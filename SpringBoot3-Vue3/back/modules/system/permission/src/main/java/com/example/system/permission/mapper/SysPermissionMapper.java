package com.example.system.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.permission.entity.SysPermission;

import java.util.List;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    List<SysPermission> selectPermissionsByUserId(Long userId);
    List<SysPermission> selectPermissionsByRoleId(Long roleId);
}