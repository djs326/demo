package com.example.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.system.user.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);

    List<SysUser> listUsers(String name, String username, Long deptId, Integer status);

    boolean saveUser(SysUser user);

    boolean updateUser(SysUser user);

    boolean deleteUser(Long id);

    List<Long> getRoleIdsByUserId(Long userId);

    boolean updateUserRoles(Long userId, List<Long> roleIds);
}