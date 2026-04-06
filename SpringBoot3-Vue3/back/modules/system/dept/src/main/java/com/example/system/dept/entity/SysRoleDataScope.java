package com.example.system.dept.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_data_scope")
public class SysRoleDataScope {
    @TableId
    private Long id;

    private Long roleId;

    private String moduleCode;

    private String scopeType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
