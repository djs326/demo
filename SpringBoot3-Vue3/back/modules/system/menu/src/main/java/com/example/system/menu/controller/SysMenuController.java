package com.example.system.menu.controller;

import com.example.system.menu.entity.SysMenu;
import com.example.system.menu.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService menuService;

    @GetMapping("/list")
    public List<SysMenu> listMenus(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return menuService.listMenus(name, type, status);
    }

    @GetMapping("/tree")
    public List<SysMenu> getMenuTree() {
        return menuService.getMenuTree();
    }

    @GetMapping("/get/{id}")
    public SysMenu getMenu(@PathVariable Long id) {
        return menuService.getById(id);
    }

    @PostMapping("/add")
    public boolean addMenu(@RequestBody SysMenu menu) {
        return menuService.saveMenu(menu);
    }

    @PutMapping("/update")
    public boolean updateMenu(@RequestBody SysMenu menu) {
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteMenu(@PathVariable Long id) {
        return menuService.deleteMenu(id);
    }

    @GetMapping("/user/{userId}")
    public List<SysMenu> getMenusByUserId(@PathVariable Long userId) {
        return menuService.getMenusByUserId(userId);
    }
}