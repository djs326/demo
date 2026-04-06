package com.example.system.dept.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_dept_closure")
public class SysDeptClosure {
    @TableId
    private Long id;

    private Long ancestorId;

    private Long descendantId;

    private Integer distance;
}
