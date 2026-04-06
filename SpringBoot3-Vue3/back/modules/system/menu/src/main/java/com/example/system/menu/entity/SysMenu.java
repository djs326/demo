package com.example.system.menu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId
    private Long id;
    private String name;
    private Long parentId;
    private String path;
    private String component;
    private String permissionCode;
    private Integer type;
    private String icon;
    private Integer orderNum;
    private Integer status;
    private Integer isExternal;
    private Integer isCache;
    private Integer visible;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private List<SysMenu> children;
}