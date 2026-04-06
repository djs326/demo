package com.example.system.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.system.permission.entity.SysPermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {
    List<SysPermission> listPermissions(String name, String code, String resourceType, Integer status);

    boolean savePermission(SysPermission permission);

    boolean updatePermission(SysPermission permission);

    boolean deletePermission(Long id);

    List<SysPermission> getPermissionsByUserId(Long userId);

    List<SysPermission> getPermissionsByRoleId(Long roleId);
}