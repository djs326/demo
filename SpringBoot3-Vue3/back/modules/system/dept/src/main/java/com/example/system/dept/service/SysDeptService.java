package com.example.system.dept.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.system.dept.entity.SysDept;

import java.util.List;

public interface SysDeptService extends IService<SysDept> {
    
    List<SysDept> listDepts(String name, Integer status);
    
    List<SysDept> buildDeptTree(List<SysDept> depts);
    
    boolean saveDept(SysDept dept);
    
    boolean updateDept(SysDept dept);
    
    boolean deleteDept(Long id);
    
    List<Long> getDescendantIds(Long deptId);
    
    List<Long> getAncestorIds(Long deptId);
}
