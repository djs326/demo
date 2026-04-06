package com.example.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.user.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    List<Long> selectRoleIdsByUserId(Long userId);
    void deleteByUserId(Long userId);
}