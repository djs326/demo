package com.example.system.dept.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_dept")
public class SysRoleDept {
    @TableId
    private Long id;

    private Long roleId;

    private Long deptId;
}
