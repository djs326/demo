package com.example.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.role.entity.SysRolePermission;

import java.util.List;

public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
    List<Long> selectPermissionIdsByRoleId(Long roleId);
    void deleteByRoleId(Long roleId);
}