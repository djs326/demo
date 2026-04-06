package com.example.system.permission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_field_permission")
public class SysFieldPermission {
    @TableId
    private Long id;

    private Long roleId;

    private String tableName;

    private String fieldName;

    private String permissionType;

    private String maskRule;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
