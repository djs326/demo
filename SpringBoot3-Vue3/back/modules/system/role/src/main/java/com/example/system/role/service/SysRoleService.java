package com.example.system.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.system.role.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    List<SysRole> listRoles(String name, String code, Integer status);

    boolean saveRole(SysRole role);

    boolean updateRole(SysRole role);

    boolean deleteRole(Long id);

    List<Long> getPermissionIdsByRoleId(Long roleId);

    boolean updateRolePermissions(Long roleId, List<Long> permissionIds);
}