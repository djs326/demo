package com.example.system.dept.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.system.dept.entity.SysDept;
import com.example.system.dept.entity.SysDeptClosure;
import com.example.system.dept.mapper.SysDeptClosureMapper;
import com.example.system.dept.mapper.SysDeptMapper;
import com.example.system.dept.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {
    
    @Autowired
    private SysDeptClosureMapper deptClosureMapper;
    
    @Override
    public List<SysDept> listDepts(String name, Integer status) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(SysDept::getName, name);
        }
        if (status != null) {
            wrapper.eq(SysDept::getStatus, status);
        }
        wrapper.orderByAsc(SysDept::getOrderNum);
        return list(wrapper);
    }
    
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> rootDepts = depts.stream()
                .filter(dept -> dept.getParentId() == null || dept.getParentId() == 0)
                .collect(Collectors.toList());
        
        for (SysDept rootDept : rootDepts) {
            buildChildren(rootDept, depts);
        }
        
        return rootDepts;
    }
    
    private void buildChildren(SysDept parent, List<SysDept> allDepts) {
        List<SysDept> children = allDepts.stream()
                .filter(dept -> parent.getId().equals(dept.getParentId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (SysDept child : children) {
                buildChildren(child, allDepts);
            }
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDept(SysDept dept) {
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
            dept.setAncestors("0");
        } else {
            SysDept parentDept = getById(dept.getParentId());
            if (parentDept != null) {
                dept.setAncestors(parentDept.getAncestors() + "," + dept.getParentId());
            } else {
                dept.setAncestors("0," + dept.getParentId());
            }
        }
        
        boolean success = save(dept);
        if (success) {
            maintainClosureTableOnInsert(dept);
        }
        return success;
    }
    
    private void maintainClosureTableOnInsert(SysDept dept) {
        long closureId = System.currentTimeMillis();
        
        SysDeptClosure selfClosure = new SysDeptClosure();
        selfClosure.setId(closureId++);
        selfClosure.setAncestorId(dept.getId());
        selfClosure.setDescendantId(dept.getId());
        selfClosure.setDistance(0);
        deptClosureMapper.insert(selfClosure);
        
        if (dept.getParentId() != null && dept.getParentId() > 0) {
            List<Long> ancestorIds = getAncestorIds(dept.getParentId());
            ancestorIds.add(dept.getParentId());
            
            for (Long ancestorId : ancestorIds) {
                SysDeptClosure closure = new SysDeptClosure();
                closure.setId(closureId++);
                closure.setAncestorId(ancestorId);
                closure.setDescendantId(dept.getId());
                
                SysDeptClosure ancestorClosure = deptClosureMapper.selectOne(
                        new LambdaQueryWrapper<SysDeptClosure>()
                                .eq(SysDeptClosure::getAncestorId, ancestorId)
                                .eq(SysDeptClosure::getDescendantId, dept.getParentId())
                );
                
                int distance = ancestorClosure != null ? ancestorClosure.getDistance() + 1 : 1;
                closure.setDistance(distance);
                deptClosureMapper.insert(closure);
            }
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDept(SysDept dept) {
        SysDept oldDept = getById(dept.getId());
        if (oldDept == null) {
            return false;
        }
        
        dept.setUpdateTime(LocalDateTime.now());
        
        if (!oldDept.getParentId().equals(dept.getParentId())) {
            if (dept.getParentId() == null) {
                dept.setParentId(0L);
                dept.setAncestors("0");
            } else {
                SysDept newParent = getById(dept.getParentId());
                if (newParent != null) {
                    dept.setAncestors(newParent.getAncestors() + "," + dept.getParentId());
                } else {
                    dept.setAncestors("0," + dept.getParentId());
                }
            }
            
            maintainClosureTableOnUpdate(dept, oldDept);
            updateDescendantAncestors(dept.getId(), oldDept.getAncestors(), dept.getAncestors());
        }
        
        return updateById(dept);
    }
    
    private void maintainClosureTableOnUpdate(SysDept dept, SysDept oldDept) {
        List<Long> descendantIds = getDescendantIds(dept.getId());
        for (Long descendantId : descendantIds) {
            deptClosureMapper.deleteByDescendantId(descendantId);
        }
        
        maintainClosureTableOnInsert(dept);
        
        List<SysDept> children = list(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getParentId, dept.getId()));
        
        for (SysDept child : children) {
            maintainClosureTableOnInsert(child);
        }
    }
    
    private void updateDescendantAncestors(Long deptId, String oldAncestors, String newAncestors) {
        List<Long> descendantIds = getDescendantIds(deptId);
        descendantIds.remove(deptId);
        
        for (Long descendantId : descendantIds) {
            SysDept descendant = getById(descendantId);
            if (descendant != null) {
                String updatedAncestors = descendant.getAncestors().replaceFirst(oldAncestors, newAncestors);
                descendant.setAncestors(updatedAncestors);
                updateById(descendant);
            }
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDept(Long id) {
        List<Long> descendantIds = getDescendantIds(id);
        for (Long descendantId : descendantIds) {
            deptClosureMapper.deleteByDescendantId(descendantId);
            removeById(descendantId);
        }
        return true;
    }
    
    @Override
    public List<Long> getDescendantIds(Long deptId) {
        return deptClosureMapper.selectList(
                new LambdaQueryWrapper<SysDeptClosure>()
                        .eq(SysDeptClosure::getAncestorId, deptId)
        ).stream().map(SysDeptClosure::getDescendantId).collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getAncestorIds(Long deptId) {
        return deptClosureMapper.selectList(
                new LambdaQueryWrapper<SysDeptClosure>()
                        .eq(SysDeptClosure::getDescendantId, deptId)
                        .ne(SysDeptClosure::getDistance, 0)
        ).stream().map(SysDeptClosure::getAncestorId).collect(Collectors.toList());
    }
}
