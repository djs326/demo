package com.example.system.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.system.permission.entity.SysFieldPermission;

import java.util.List;
import java.util.Map;

public interface SysFieldPermissionService extends IService<SysFieldPermission> {
    List<SysFieldPermission> getFieldPermissionsByRoleIds(List<Long> roleIds);
    Map<String, Map<String, String>> getFieldPermissionMapByRoleIds(List<Long> roleIds);
    List<SysFieldPermission> getFieldPermissionsByRoleIdAndTableName(Long roleId, String tableName);
    boolean saveFieldPermission(SysFieldPermission fieldPermission);
    boolean updateFieldPermission(SysFieldPermission fieldPermission);
    boolean deleteFieldPermission(Long id);
}
