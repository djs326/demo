package com.example.system.role.controller;

import com.example.system.role.entity.SysRole;
import com.example.system.role.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class SysRoleController {
    @Autowired
    private SysRoleService roleService;

    @GetMapping("/list")
    public List<SysRole> listRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer status) {
        return roleService.listRoles(name, code, status);
    }

    @GetMapping("/get/{id}")
    public SysRole getRole(@PathVariable Long id) {
        return roleService.getById(id);
    }

    @PostMapping("/add")
    public boolean addRole(@RequestBody SysRole role) {
        return roleService.saveRole(role);
    }

    @PutMapping("/update")
    public boolean updateRole(@RequestBody SysRole role) {
        return roleService.updateRole(role);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteRole(@PathVariable Long id) {
        return roleService.deleteRole(id);
    }

    @GetMapping("/permissions/{roleId}")
    public List<Long> getRolePermissions(@PathVariable Long roleId) {
        return roleService.getPermissionIdsByRoleId(roleId);
    }

    @PutMapping("/permissions")
    public boolean updateRolePermissions(@RequestParam Long roleId, @RequestBody List<Long> permissionIds) {
        return roleService.updateRolePermissions(roleId, permissionIds);
    }
}