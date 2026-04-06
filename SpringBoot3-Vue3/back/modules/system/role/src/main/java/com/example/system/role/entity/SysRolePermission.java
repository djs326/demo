package com.example.system.role.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {
    @TableId("role_id")
    private Long roleId;

    @TableId("permission_id")
    private Long permissionId;
}