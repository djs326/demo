package com.example.system.dept.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_dept")
public class SysDept {
    @TableId
    private Long id;

    private String name;

    private Long parentId;

    private String deptCode;

    private String leader;

    private Integer orderNum;

    private Integer status;

    private String ancestors;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private List<SysDept> children;
}
