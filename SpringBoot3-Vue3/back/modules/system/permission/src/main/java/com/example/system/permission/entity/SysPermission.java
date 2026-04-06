package com.example.system.permission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
public class SysPermission {
    @TableId
    private Long id;

    private String name;

    private String code;

    private String description;

    private String resourceType;

    private Long resourceId;

    private String path;

    private String method;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}