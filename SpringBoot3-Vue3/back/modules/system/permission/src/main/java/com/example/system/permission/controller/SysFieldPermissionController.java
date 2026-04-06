package com.example.system.permission.controller;

import com.example.common.model.Result;
import com.example.system.permission.cache.FieldPermissionCacheService;
import com.example.system.permission.entity.SysFieldPermission;
import com.example.system.permission.service.SysFieldPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字段权限管理", description = "字段权限相关接口")
@RestController
@RequestMapping("/system/field-permission")
public class SysFieldPermissionController {

    @Autowired
    private SysFieldPermissionService fieldPermissionService;

    @Autowired
    private FieldPermissionCacheService fieldPermissionCacheService;

    @Operation(summary = "获取字段权限列表")
    @GetMapping("/list")
    public Result<List<SysFieldPermission>> list(@RequestParam(required = false) Long roleId) {
        if (roleId != null) {
            return Result.success(fieldPermissionService.lambdaQuery()
                    .eq(SysFieldPermission::getRoleId, roleId)
                    .list());
        }
        return Result.success(fieldPermissionService.list());
    }

    @Operation(summary = "获取角色字段权限")
    @GetMapping("/role/{roleId}")
    public Result<List<SysFieldPermission>> getByRoleId(@PathVariable Long roleId) {
        return Result.success(fieldPermissionService.lambdaQuery()
                .eq(SysFieldPermission::getRoleId, roleId)
                .list());
    }

    @Operation(summary = "新增字段权限")
    @PostMapping
    public Result<Boolean> save(@RequestBody SysFieldPermission fieldPermission) {
        boolean success = fieldPermissionService.saveFieldPermission(fieldPermission);
        if (success) {
            fieldPermissionCacheService.clearFieldPermissionCache(List.of(fieldPermission.getRoleId()));
        }
        return Result.success(success);
    }

    @Operation(summary = "修改字段权限")
    @PutMapping
    public Result<Boolean> update(@RequestBody SysFieldPermission fieldPermission) {
        boolean success = fieldPermissionService.updateFieldPermission(fieldPermission);
        if (success) {
            fieldPermissionCacheService.clearFieldPermissionCache(List.of(fieldPermission.getRoleId()));
        }
        return Result.success(success);
    }

    @Operation(summary = "删除字段权限")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        SysFieldPermission fieldPermission = fieldPermissionService.getById(id);
        boolean success = fieldPermissionService.deleteFieldPermission(id);
        if (success && fieldPermission != null) {
            fieldPermissionCacheService.clearFieldPermissionCache(List.of(fieldPermission.getRoleId()));
        }
        return Result.success(success);
    }

    @Operation(summary = "刷新字段权限缓存")
    @PostMapping("/refresh-cache")
    public Result<Void> refreshCache(@RequestParam(required = false) List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            fieldPermissionCacheService.clearFieldPermissionCache(roleIds);
        } else {
            fieldPermissionCacheService.clearAllFieldPermissionCache();
        }
        return Result.success();
    }
}
