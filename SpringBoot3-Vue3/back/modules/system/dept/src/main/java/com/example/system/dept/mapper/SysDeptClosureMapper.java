package com.example.system.dept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.dept.entity.SysDeptClosure;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDeptClosureMapper extends BaseMapper<SysDeptClosure> {
    
    List<Long> selectDescendantIds(@Param("ancestorId") Long ancestorId);
    
    List<Long> selectAncestorIds(@Param("descendantId") Long descendantId);
    
    int insertClosureRecords(@Param("ancestorId") Long ancestorId, 
                            @Param("descendantId") Long descendantId,
                            @Param("distance") int distance);
    
    int deleteByDescendantId(@Param("descendantId") Long descendantId);
}
