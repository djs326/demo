package com.example.system.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.menu.entity.SysMenu;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
    List<SysMenu> selectMenusByUserId(Long userId);
}