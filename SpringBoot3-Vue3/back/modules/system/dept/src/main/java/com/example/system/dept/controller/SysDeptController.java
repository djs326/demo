package com.example.system.dept.controller;

import com.example.system.dept.entity.SysDept;
import com.example.system.dept.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dept")
public class SysDeptController {
    
    @Autowired
    private SysDeptService deptService;
    
    @GetMapping("/list")
    public List<SysDept> listDepts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        return deptService.listDepts(name, status);
    }
    
    @GetMapping("/tree")
    public List<SysDept> getDeptTree(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        List<SysDept> depts = deptService.listDepts(name, status);
        return deptService.buildDeptTree(depts);
    }
    
    @GetMapping("/get/{id}")
    public SysDept getDept(@PathVariable Long id) {
        return deptService.getById(id);
    }
    
    @PostMapping("/add")
    public boolean addDept(@RequestBody SysDept dept) {
        return deptService.saveDept(dept);
    }
    
    @PutMapping("/update")
    public boolean updateDept(@RequestBody SysDept dept) {
        return deptService.updateDept(dept);
    }
    
    @DeleteMapping("/delete/{id}")
    public boolean deleteDept(@PathVariable Long id) {
        return deptService.deleteDept(id);
    }
    
    @GetMapping("/descendants/{deptId}")
    public List<Long> getDescendantIds(@PathVariable Long deptId) {
        return deptService.getDescendantIds(deptId);
    }
    
    @GetMapping("/ancestors/{deptId}")
    public List<Long> getAncestorIds(@PathVariable Long deptId) {
        return deptService.getAncestorIds(deptId);
    }
}
