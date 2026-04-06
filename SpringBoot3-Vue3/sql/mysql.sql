-- =============================================
-- Spring Boot + MyBatis-Plus + Vue3 通用开发脚手架
-- 数据库初始化脚本
-- MySQL 8.0+
-- =============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 用户表 (sys_user)
-- =============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（加密）',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1-启用，0-禁用）',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '软删除（0-未删除，1-已删除）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 部门表 (sys_dept)
-- =============================================
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL COMMENT '部门ID',
  `name` varchar(50) NOT NULL COMMENT '部门名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父部门ID',
  `dept_code` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1-启用，0-禁用）',
  `ancestors` varchar(500) DEFAULT NULL COMMENT '祖级路径，如：0,1,3,5',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_code` (`dept_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- =============================================
-- 3. 部门闭包表 (sys_dept_closure)
-- =============================================
DROP TABLE IF EXISTS `sys_dept_closure`;
CREATE TABLE `sys_dept_closure` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `ancestor_id` bigint NOT NULL COMMENT '祖先部门ID',
  `descendant_id` bigint NOT NULL COMMENT '后代部门ID',
  `distance` int NOT NULL COMMENT '距离（0表示自己）',
  PRIMARY KEY (`id`),
  KEY `idx_ancestor` (`ancestor_id`),
  KEY `idx_descendant` (`descendant_id`),
  KEY `idx_ancestor_descendant` (`ancestor_id`, `descendant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门闭包表';

-- =============================================
-- 4. 角色表 (sys_role)
-- =============================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1-启用，0-禁用）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =============================================
-- 5. 菜单表 (sys_menu)
-- =============================================
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL COMMENT '菜单ID',
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `permission_code` varchar(100) DEFAULT NULL COMMENT '权限编码，关联sys_permission.code',
  `type` tinyint NOT NULL COMMENT '类型（0-目录，1-菜单，2-按钮）',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1-启用，0-禁用）',
  `is_external` tinyint NOT NULL DEFAULT 0 COMMENT '是否外部链接（0-否，1-是）',
  `is_cache` tinyint NOT NULL DEFAULT 0 COMMENT '是否缓存（0-否，1-是）',
  `visible` tinyint NOT NULL DEFAULT 1 COMMENT '是否可见（0-否，1-是）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- =============================================
-- 6. 用户角色关联表 (sys_user_role)
-- =============================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =============================================
-- 7. 权限表 (sys_permission)
-- =============================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL COMMENT '权限ID',
  `name` varchar(50) NOT NULL COMMENT '权限名称',
  `code` varchar(100) NOT NULL COMMENT '权限编码',
  `description` varchar(200) DEFAULT NULL COMMENT '权限描述',
  `resource_type` varchar(20) NOT NULL COMMENT '资源类型（menu/button/api）',
  `resource_id` bigint DEFAULT NULL COMMENT '资源ID',
  `path` varchar(200) DEFAULT NULL COMMENT 'API路径',
  `method` varchar(10) DEFAULT NULL COMMENT '请求方法（GET/POST/PUT/DELETE）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1-启用，0-禁用）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_resource_type` (`resource_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- =============================================
-- 8. 角色权限关联表 (sys_role_permission)
-- =============================================
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- =============================================
-- 9. 操作日志表 (sys_oper_log)
-- =============================================
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id` bigint NOT NULL COMMENT '日志ID',
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
  `status` int NOT NULL DEFAULT 0 COMMENT '状态（0-成功，1-失败）',
  `error_msg` text COMMENT '错误信息',
  `oper_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_oper_time` (`oper_time`),
  KEY `idx_oper_name` (`oper_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =============================================
-- 10. 登录日志表 (sys_login_log)
-- =============================================
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL COMMENT '日志ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `status` int NOT NULL COMMENT '状态（0-成功，1-失败）',
  `ipaddr` varchar(128) NOT NULL COMMENT '登录IP',
  `login_location` varchar(255) DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `msg` varchar(255) DEFAULT NULL COMMENT '登录信息',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- =============================================
-- 11. 角色数据权限表 (sys_role_data_scope)
-- =============================================
DROP TABLE IF EXISTS `sys_role_data_scope`;
CREATE TABLE `sys_role_data_scope` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `module_code` varchar(50) NOT NULL COMMENT '模块编码（如：order, user）',
  `scope_type` varchar(20) NOT NULL COMMENT '数据范围类型（ALL/DEPT/DEPT_AND_CHILD/SELF/CUSTOM）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_module` (`role_id`, `module_code`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色数据权限表';

-- =============================================
-- 12. 角色部门关联表 (sys_role_dept)
-- =============================================
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色部门关联表';

-- =============================================
-- 13. 字段权限表 (sys_field_permission)
-- =============================================
DROP TABLE IF EXISTS `sys_field_permission`;
CREATE TABLE `sys_field_permission` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `table_name` varchar(100) NOT NULL COMMENT '表名',
  `field_name` varchar(100) NOT NULL COMMENT '字段名',
  `permission_type` varchar(20) NOT NULL COMMENT '权限类型（visible/readonly/mask/hidden）',
  `mask_rule` varchar(200) DEFAULT NULL COMMENT '脱敏规则',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_table_field` (`table_name`, `field_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段权限表';

-- =============================================
-- 初始化基础数据
-- =============================================

-- 当前时间
SET @now = NOW();

-- =============================================
-- 初始化部门数据
-- =============================================
INSERT INTO `sys_dept` (`id`, `name`, `parent_id`, `dept_code`, `leader`, `order_num`, `status`, `ancestors`, `create_time`, `update_time`) VALUES
(1, '总公司', NULL, 'HQ', '张三', 1, 1, '0', @now, @now),
(2, '技术部', 1, 'TECH', '李四', 1, 1, '0,1', @now, @now),
(3, '市场部', 1, 'MARKET', '王五', 2, 1, '0,1', @now, @now);

-- =============================================
-- 初始化部门闭包表数据
-- =============================================
INSERT INTO `sys_dept_closure` (`id`, `ancestor_id`, `descendant_id`, `distance`) VALUES
(1, 1, 1, 0),
(2, 1, 2, 1),
(3, 1, 3, 1),
(4, 2, 2, 0),
(5, 3, 3, 0);

-- =============================================
-- 初始化角色数据
-- =============================================
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `status`, `create_time`, `update_time`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1, @now, @now),
(2, '普通用户', 'NORMAL_USER', '普通用户权限', 1, @now, @now);

-- =============================================
-- 初始化用户数据
-- 密码 admin123 使用 BCrypt 加密后的结果
-- =============================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `name`, `email`, `phone`, `dept_id`, `status`, `deleted`, `create_time`, `update_time`, `last_login_time`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '管理员', 'admin@example.com', '13800138000', 1, 1, 0, @now, @now, NULL);

-- =============================================
-- 初始化用户角色关联数据
-- =============================================
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- =============================================
-- 初始化菜单数据
-- =============================================
INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `path`, `component`, `permission_code`, `type`, `icon`, `order_num`, `status`, `is_external`, `is_cache`, `visible`, `create_time`, `update_time`) VALUES
(1, '系统管理', NULL, '/system', NULL, NULL, 0, 'system', 1, 1, 0, 0, 1, @now, @now),
(2, '用户管理', 1, 'user', 'system/user/index', 'system:user:list', 1, 'user', 1, 1, 0, 0, 1, @now, @now),
(3, '角色管理', 1, 'role', 'system/role/index', 'system:role:list', 1, 'role', 2, 1, 0, 0, 1, @now, @now),
(4, '菜单管理', 1, 'menu', 'system/menu/index', 'system:menu:list', 1, 'menu', 3, 1, 0, 0, 1, @now, @now),
(5, '部门管理', 1, 'dept', 'system/dept/index', 'system:dept:list', 1, 'tree', 4, 1, 0, 0, 1, @now, @now),
(6, '操作日志', 1, 'operlog', 'system/operlog/index', 'system:operlog:list', 1, 'log', 5, 1, 0, 0, 1, @now, @now),
(7, '登录日志', 1, 'loginlog', 'system/loginlog/index', 'system:loginlog:list', 1, 'logininfor', 6, 1, 0, 0, 1, @now, @now),
(8, '用户查询', 2, NULL, NULL, 'system:user:query', 2, NULL, 1, 1, 0, 0, 1, @now, @now),
(9, '用户新增', 2, NULL, NULL, 'system:user:add', 2, NULL, 2, 1, 0, 0, 1, @now, @now),
(10, '用户修改', 2, NULL, NULL, 'system:user:edit', 2, NULL, 3, 1, 0, 0, 1, @now, @now),
(11, '用户删除', 2, NULL, NULL, 'system:user:delete', 2, NULL, 4, 1, 0, 0, 1, @now, @now);

-- =============================================
-- 初始化权限数据
-- =============================================
INSERT INTO `sys_permission` (`id`, `name`, `code`, `description`, `resource_type`, `resource_id`, `path`, `method`, `status`, `create_time`, `update_time`) VALUES
(1, '用户列表', 'system:user:list', '查看用户列表', 'menu', 2, '/system/user/list', 'GET', 1, @now, @now),
(2, '用户查询', 'system:user:query', '查询用户', 'button', 8, '/system/user/query', 'GET', 1, @now, @now),
(3, '用户新增', 'system:user:add', '新增用户', 'button', 9, '/system/user/add', 'POST', 1, @now, @now),
(4, '用户修改', 'system:user:edit', '修改用户', 'button', 10, '/system/user/edit', 'PUT', 1, @now, @now),
(5, '用户删除', 'system:user:delete', '删除用户', 'button', 11, '/system/user/delete', 'DELETE', 1, @now, @now),
(6, '角色列表', 'system:role:list', '查看角色列表', 'menu', 3, '/system/role/list', 'GET', 1, @now, @now),
(7, '菜单列表', 'system:menu:list', '查看菜单列表', 'menu', 4, '/system/menu/list', 'GET', 1, @now, @now),
(8, '部门列表', 'system:dept:list', '查看部门列表', 'menu', 5, '/system/dept/list', 'GET', 1, @now, @now),
(9, '操作日志', 'system:operlog:list', '查看操作日志', 'menu', 6, '/system/operlog/list', 'GET', 1, @now, @now),
(10, '登录日志', 'system:loginlog:list', '查看登录日志', 'menu', 7, '/system/loginlog/list', 'GET', 1, @now, @now);

-- =============================================
-- 初始化角色权限关联数据
-- 超级管理员拥有所有权限
-- =============================================
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(1, 6), (1, 7), (1, 8), (1, 9), (1, 10);

-- =============================================
-- 初始化角色数据权限
-- 超级管理员拥有所有数据权限
-- =============================================
INSERT INTO `sys_role_data_scope` (`id`, `role_id`, `module_code`, `scope_type`, `create_time`, `update_time`) VALUES
(1, 1, 'user', 'ALL', @now, @now),
(2, 1, 'role', 'ALL', @now, @now),
(3, 1, 'menu', 'ALL', @now, @now),
(4, 1, 'dept', 'ALL', @now, @now);

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 初始化完成
-- =============================================
