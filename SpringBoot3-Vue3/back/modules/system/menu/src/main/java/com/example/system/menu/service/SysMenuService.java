package com.example.system.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.system.menu.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> listMenus(String name, Integer type, Integer status);

    List<SysMenu> getMenuTree();

    boolean saveMenu(SysMenu menu);

    boolean updateMenu(SysMenu menu);

    boolean deleteMenu(Long id);

    List<SysMenu> getMenusByUserId(Long userId);
}