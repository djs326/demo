-- 创建用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（加密）',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（1-启用，0-禁用）',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '软删除（0-未删除，1-已删除）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_email` (`email`),
  UNIQUE KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建部门表
CREATE TABLE IF NOT EXISTS `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '部门名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父部门ID',
  `dept_code` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（1-启用，0-禁用）',
  `ancestors` varchar(500) DEFAULT NULL COMMENT '祖级路径，如：0,1,3,5',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_dept_code` (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 创建部门闭包表
CREATE TABLE IF NOT EXISTS `sys_dept_closure` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ancestor_id` bigint NOT NULL COMMENT '祖先部门ID',
  `descendant_id` bigint NOT NULL COMMENT '后代部门ID',
  `distance` int NOT NULL COMMENT '距离（0表示自己）',
  PRIMARY KEY (`id`),
  INDEX `idx_ancestor` (`ancestor_id`),
  INDEX `idx_descendant` (`descendant_id`),
  INDEX `idx_ancestor_descendant` (`ancestor_id`, `descendant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门闭包表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（1-启用，0-禁用）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `permission_code` varchar(100) DEFAULT NULL COMMENT '权限编码，关联sys_permission.code',
  `type` tinyint(1) NOT NULL COMMENT '类型（0-目录，1-菜单，2-按钮）',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（1-启用，0-禁用）',
  `is_external` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否外部链接（0-否，1-是）',
  `is_cache` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否缓存（0-否，1-是）',
  `visible` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可见（0-否，1-是）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '权限名称',
  `code` varchar(100) NOT NULL COMMENT '权限编码',
  `description` varchar(200) DEFAULT NULL COMMENT '权限描述',
  `resource_type` varchar(20) NOT NULL COMMENT '资源类型（menu/button/api）',
  `resource_id` bigint DEFAULT NULL COMMENT '资源ID',
  `path` varchar(200) DEFAULT NULL COMMENT 'API路径',
  `method` varchar(10) DEFAULT NULL COMMENT '请求方法（GET/POST/PUT/DELETE）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（1-启用，0-禁用）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`, `permission_id`),
  INDEX `idx_role_id` (`role_id`),
  INDEX `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 创建操作日志表
CREATE TABLE IF NOT EXISTS `sys_oper_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '操作标题',
  `business_type` int NOT NULL COMMENT '业务类型',
  `method` varchar(100) NOT NULL COMMENT '方法名',
  `request_method` varchar(10) NOT NULL COMMENT '请求方式',
  `operator_type` int NOT NULL COMMENT '操作类型',
  `oper_name` varchar(50) NOT NULL COMMENT '操作人',
  `dept_name` varchar(50) DEFAULT NULL COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT NULL COMMENT '操作URL',
  `oper_ip` varchar(128) DEFAULT NULL COMMENT '操作IP',
  `oper_location` varchar(255) DEFAULT NULL COMMENT '操作地点',
  `oper_param` text COMMENT '操作参数',
  `json_result` text COMMENT '返回结果',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态（0-成功，1-失败）',
  `error_msg` text COMMENT '错误信息',
  `oper_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 创建登录日志表
CREATE TABLE IF NOT EXISTS `sys_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `status` int NOT NULL COMMENT '状态（0-成功，1-失败）',
  `ipaddr` varchar(128) NOT NULL COMMENT '登录IP',
  `login_location` varchar(255) DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `msg` varchar(255) DEFAULT NULL COMMENT '登录信息',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 创建角色数据权限表
CREATE TABLE IF NOT EXISTS `sys_role_data_scope` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `module_code` varchar(50) NOT NULL COMMENT '模块编码（如：order, user）',
  `scope_type` varchar(20) NOT NULL COMMENT '数据范围类型（ALL/DEPT/DEPT_AND_CHILD/SELF/CUSTOM）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_role_module` (`role_id`, `module_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色数据权限表';

-- 创建角色部门关联表
CREATE TABLE IF NOT EXISTS `sys_role_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`),
  INDEX `idx_role_id` (`role_id`),
  INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色部门关联表';

-- 创建字段权限表
CREATE TABLE IF NOT EXISTS `sys_field_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `table_name` varchar(100) NOT NULL COMMENT '表名',
  `field_name` varchar(100) NOT NULL COMMENT '字段名',
  `permission_type` varchar(20) NOT NULL COMMENT '权限类型（visible/readonly/mask/hidden）',
  `mask_rule` varchar(200) DEFAULT NULL COMMENT '脱敏规则',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_role_id` (`role_id`),
  INDEX `idx_table_field` (`table_name`, `field_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段权限表';

-- 初始化默认数据
-- 初始化管理员用户
INSERT INTO `sys_user` (`id`, `username`, `password`, `name`, `email`, `phone`, `dept_id`, `status`, `deleted`, `create_time`, `update_time`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '管理员', 'admin@example.com', '13800138000', NULL, 1, 0, NOW(), NOW());

-- 初始化默认角色
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `status`, `create_time`, `update_time`) VALUES
(1, '超级管理员', 'admin', '拥有所有权限', 1, NOW(), NOW()),
(2, '普通用户', 'user', '拥有基本权限', 1, NOW(), NOW());

-- 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- 初始化默认权限
INSERT INTO `sys_permission` (`id`, `name`, `code`, `description`, `resource_type`, `status`, `create_time`, `update_time`) VALUES
(1, '用户管理', 'user:manage', '用户管理权限', 'menu', 1, NOW(), NOW()),
(2, '角色管理', 'role:manage', '角色管理权限', 'menu', 1, NOW(), NOW()),
(3, '菜单管理', 'menu:manage', '菜单管理权限', 'menu', 1, NOW(), NOW()),
(4, '权限管理', 'permission:manage', '权限管理权限', 'menu', 1, NOW(), NOW());

-- 初始化角色权限关联
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4);

-- 初始化默认菜单
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `permission_code`, `type`, `icon`, `order_num`, `status`, `is_external`, `is_cache`, `visible`, `create_time`, `update_time`) VALUES
(1, '系统管理', 0, '/system', NULL, NULL, 0, 'system', 1, 1, 0, 0, 1, NOW(), NOW()),
(2, '用户管理', 1, '/system/user', 'system/user/index', 'user:manage', 1, 'user', 1, 1, 0, 0, 1, NOW(), NOW()),
(3, '角色管理', 1, '/system/role', 'system/role/index', 'role:manage', 1, 'role', 2, 1, 0, 0, 1, NOW(), NOW()),
(4, '菜单管理', 1, '/system/menu', 'system/menu/index', 'menu:manage', 1, 'menu', 3, 1, 0, 0, 1, NOW(), NOW()),
(5, '权限管理', 1, '/system/permission', 'system/permission/index', 'permission:manage', 1, 'permission', 4, 1, 0, 0, 1, NOW(), NOW());