package com.example.system.permission.controller;

import com.example.system.permission.entity.SysPermission;
import com.example.system.permission.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
public class SysPermissionController {
    @Autowired
    private SysPermissionService permissionService;

    @GetMapping("/list")
    public List<SysPermission> listPermissions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) Integer status) {
        return permissionService.listPermissions(name, code, resourceType, status);
    }

    @GetMapping("/get/{id}")
    public SysPermission getPermission(@PathVariable Long id) {
        return permissionService.getById(id);
    }

    @PostMapping("/add")
    public boolean addPermission(@RequestBody SysPermission permission) {
        return permissionService.savePermission(permission);
    }

    @PutMapping("/update")
    public boolean updatePermission(@RequestBody SysPermission permission) {
        return permissionService.updatePermission(permission);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deletePermission(@PathVariable Long id) {
        return permissionService.deletePermission(id);
    }

    @GetMapping("/user/{userId}")
    public List<SysPermission> getPermissionsByUserId(@PathVariable Long userId) {
        return permissionService.getPermissionsByUserId(userId);
    }

    @GetMapping("/role/{roleId}")
    public List<SysPermission> getPermissionsByRoleId(@PathVariable Long roleId) {
        return permissionService.getPermissionsByRoleId(roleId);
    }
}