package com.example.system.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.permission.entity.SysFieldPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysFieldPermissionMapper extends BaseMapper<SysFieldPermission> {
    List<SysFieldPermission> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
    List<SysFieldPermission> selectByRoleIdAndTableName(@Param("roleId") Long roleId, @Param("tableName") String tableName);
}
