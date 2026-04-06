package com.example.system.user.controller;

import com.example.system.user.entity.SysUser;
import com.example.system.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class SysUserController {
    @Autowired
    private SysUserService userService;

    @GetMapping("/list")
    public List<SysUser> listUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        return userService.listUsers(name, username, deptId, status);
    }

    @GetMapping("/get/{id}")
    public SysUser getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/add")
    public boolean addUser(@RequestBody SysUser user) {
        return userService.saveUser(user);
    }

    @PutMapping("/update")
    public boolean updateUser(@RequestBody SysUser user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/roles/{userId}")
    public List<Long> getUserRoles(@PathVariable Long userId) {
        return userService.getRoleIdsByUserId(userId);
    }

    @PutMapping("/roles")
    public boolean updateUserRoles(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        return userService.updateUserRoles(userId, roleIds);
    }
}