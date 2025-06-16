# 系统服务数据库设计

## 1. 数据库概述

### 1.1 数据库信息
- 数据库名：mall_system
- 字符集：utf8mb4
- 排序规则：utf8mb4_general_ci
- 存储引擎：InnoDB

### 1.2 表清单
1. 系统用户表 (system_user)
2. 系统角色表 (system_role)
3. 系统权限表 (system_permission)
4. 用户角色关联表 (system_user_role)
5. 角色权限关联表 (system_role_permission)
6. 系统菜单表 (system_menu)
7. 系统部门表 (system_dept)
8. 系统岗位表 (system_position)
9. 系统字典表 (system_dict)
10. 系统字典项表 (system_dict_item)
11. 系统参数表 (system_param)
12. 系统日志表 (system_log)
13. 操作日志表 (system_operation_log)
14. 登录日志表 (system_login_log)
15. 系统通知表 (system_notice)
16. 系统通知用户表 (system_notice_user)

## 2. 表结构设计

### 2.1 系统用户表 (system_user)
```sql
CREATE TABLE `system_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码',
    `nickname` varchar(64) NOT NULL COMMENT '昵称',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
    `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
    `position_id` bigint DEFAULT NULL COMMENT '岗位ID',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `gender` tinyint DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_position_id` (`position_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
```

### 2.2 系统角色表 (system_role)
```sql
CREATE TABLE `system_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` varchar(64) NOT NULL COMMENT '角色名称',
    `code` varchar(64) NOT NULL COMMENT '角色编码',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';
```

### 2.3 系统权限表 (system_permission)
```sql
CREATE TABLE `system_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `name` varchar(64) NOT NULL COMMENT '权限名称',
    `code` varchar(64) NOT NULL COMMENT '权限编码',
    `type` tinyint NOT NULL COMMENT '类型：1-菜单，2-按钮，3-接口',
    `parent_id` bigint DEFAULT NULL COMMENT '父级ID',
    `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
    `perms` varchar(255) DEFAULT NULL COMMENT '权限标识',
    `icon` varchar(64) DEFAULT NULL COMMENT '图标',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';
```

### 2.4 用户角色关联表 (system_user_role)
```sql
CREATE TABLE `system_user_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

### 2.5 角色权限关联表 (system_role_permission)
```sql
CREATE TABLE `system_role_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `permission_id` bigint NOT NULL COMMENT '权限ID',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
```

### 2.6 系统菜单表 (system_menu)
```sql
CREATE TABLE `system_menu` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `name` varchar(64) NOT NULL COMMENT '菜单名称',
    `parent_id` bigint DEFAULT NULL COMMENT '父级ID',
    `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
    `perms` varchar(255) DEFAULT NULL COMMENT '权限标识',
    `type` tinyint NOT NULL COMMENT '类型：1-目录，2-菜单，3-按钮',
    `icon` varchar(64) DEFAULT NULL COMMENT '图标',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';
```

### 2.7 系统部门表 (system_dept)
```sql
CREATE TABLE `system_dept` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `name` varchar(64) NOT NULL COMMENT '部门名称',
    `parent_id` bigint DEFAULT NULL COMMENT '父级ID',
    `ancestors` varchar(255) DEFAULT NULL COMMENT '祖级列表',
    `leader` varchar(64) DEFAULT NULL COMMENT '负责人',
    `phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统部门表';
```

### 2.8 系统岗位表 (system_position)
```sql
CREATE TABLE `system_position` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `name` varchar(64) NOT NULL COMMENT '岗位名称',
    `code` varchar(64) NOT NULL COMMENT '岗位编码',
    `dept_id` bigint NOT NULL COMMENT '部门ID',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统岗位表';
```

### 2.9 系统字典表 (system_dict)
```sql
CREATE TABLE `system_dict` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    `name` varchar(64) NOT NULL COMMENT '字典名称',
    `code` varchar(64) NOT NULL COMMENT '字典编码',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典表';
```

### 2.10 系统字典项表 (system_dict_item)
```sql
CREATE TABLE `system_dict_item` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典项ID',
    `dict_id` bigint NOT NULL COMMENT '字典ID',
    `label` varchar(64) NOT NULL COMMENT '标签',
    `value` varchar(64) NOT NULL COMMENT '值',
    `color` varchar(32) DEFAULT NULL COMMENT '颜色',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_value` (`dict_id`, `value`),
    KEY `idx_dict_id` (`dict_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典项表';
```

### 2.11 系统参数表 (system_param)
```sql
CREATE TABLE `system_param` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数ID',
    `name` varchar(64) NOT NULL COMMENT '参数名称',
    `code` varchar(64) NOT NULL COMMENT '参数编码',
    `value` varchar(255) NOT NULL COMMENT '参数值',
    `type` tinyint NOT NULL COMMENT '类型：1-系统参数，2-业务参数',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';
```

### 2.12 系统日志表 (system_log)
```sql
CREATE TABLE `system_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '用户名',
    `operation` varchar(64) NOT NULL COMMENT '操作',
    `method` varchar(255) NOT NULL COMMENT '方法名',
    `params` text DEFAULT NULL COMMENT '参数',
    `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
    `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
    `location` varchar(255) DEFAULT NULL COMMENT '操作地点',
    `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
    `status` tinyint NOT NULL COMMENT '状态：0-失败，1-成功',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误消息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_operation` (`operation`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';
```

### 2.13 操作日志表 (system_operation_log)
```sql
CREATE TABLE `system_operation_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '用户名',
    `module` varchar(64) NOT NULL COMMENT '模块',
    `operation` varchar(64) NOT NULL COMMENT '操作',
    `method` varchar(255) NOT NULL COMMENT '方法名',
    `request_url` varchar(255) NOT NULL COMMENT '请求URL',
    `request_method` varchar(32) NOT NULL COMMENT '请求方式',
    `request_params` text DEFAULT NULL COMMENT '请求参数',
    `response_result` text DEFAULT NULL COMMENT '响应结果',
    `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
    `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
    `location` varchar(255) DEFAULT NULL COMMENT '操作地点',
    `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
    `status` tinyint NOT NULL COMMENT '状态：0-失败，1-成功',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '错误消息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_module` (`module`),
    KEY `idx_operation` (`operation`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
```

### 2.14 登录日志表 (system_login_log)
```sql
CREATE TABLE `system_login_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '用户名',
    `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
    `location` varchar(255) DEFAULT NULL COMMENT '登录地点',
    `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
    `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',
    `os` varchar(64) DEFAULT NULL COMMENT '操作系统',
    `status` tinyint NOT NULL COMMENT '状态：0-失败，1-成功',
    `msg` varchar(255) DEFAULT NULL COMMENT '提示消息',
    `login_time` datetime NOT NULL COMMENT '登录时间',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';
```

### 2.15 系统通知表 (system_notice)
```sql
CREATE TABLE `system_notice` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `title` varchar(128) NOT NULL COMMENT '标题',
    `content` text NOT NULL COMMENT '内容',
    `type` tinyint NOT NULL COMMENT '类型：1-通知，2-公告',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `created_by` bigint NOT NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';
```

### 2.16 系统通知用户表 (system_notice_user)
```sql
CREATE TABLE `system_notice_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `notice_id` bigint NOT NULL COMMENT '通知ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `read_status` tinyint NOT NULL DEFAULT '0' COMMENT '阅读状态：0-未读，1-已读',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`),
    KEY `idx_notice_id` (`notice_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_read_status` (`read_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知用户表';
```

## 3. 索引设计

### 3.1 系统用户表索引
1. 主键索引：id
2. 唯一索引：username, email, phone
3. 普通索引：dept_id, position_id, status, created_at

### 3.2 系统角色表索引
1. 主键索引：id
2. 唯一索引：code
3. 普通索引：status, sort

### 3.3 系统权限表索引
1. 主键索引：id
2. 唯一索引：code
3. 普通索引：parent_id, type, status, sort

### 3.4 用户角色关联表索引
1. 主键索引：id
2. 唯一索引：(user_id, role_id)
3. 普通索引：user_id, role_id

### 3.5 角色权限关联表索引
1. 主键索引：id
2. 唯一索引：(role_id, permission_id)
3. 普通索引：role_id, permission_id

### 3.6 系统菜单表索引
1. 主键索引：id
2. 普通索引：parent_id, type, status, sort

### 3.7 系统部门表索引
1. 主键索引：id
2. 普通索引：parent_id, status, sort

### 3.8 系统岗位表索引
1. 主键索引：id
2. 唯一索引：code
3. 普通索引：dept_id, status, sort

### 3.9 系统字典表索引
1. 主键索引：id
2. 唯一索引：code
3. 普通索引：status

### 3.10 系统字典项表索引
1. 主键索引：id
2. 唯一索引：(dict_id, value)
3. 普通索引：dict_id, status, sort

### 3.11 系统参数表索引
1. 主键索引：id
2. 唯一索引：code
3. 普通索引：type, status

### 3.12 系统日志表索引
1. 主键索引：id
2. 普通索引：user_id, username, operation, status, created_at

### 3.13 操作日志表索引
1. 主键索引：id
2. 普通索引：user_id, username, module, operation, status, created_at

### 3.14 登录日志表索引
1. 主键索引：id
2. 普通索引：user_id, username, status, login_time

### 3.15 系统通知表索引
1. 主键索引：id
2. 普通索引：type, status, sort, created_by

### 3.16 系统通知用户表索引
1. 主键索引：id
2. 唯一索引：(notice_id, user_id)
3. 普通索引：notice_id, user_id, read_status

## 4. 字段说明

### 4.1 通用字段
- id：主键，bigint，自增
- created_at：创建时间，datetime
- updated_at：更新时间，datetime
- deleted_at：删除时间，datetime
- status：状态，tinyint
- remark：备注，varchar(255)

### 4.2 系统用户表字段
- username：用户名，varchar(64)
- password：密码，varchar(128)
- nickname：昵称，varchar(64)
- avatar：头像，varchar(255)
- email：邮箱，varchar(64)
- phone：手机号，varchar(32)
- dept_id：部门ID，bigint
- position_id：岗位ID，bigint
- gender：性别，tinyint
- last_login_time：最后登录时间，datetime
- last_login_ip：最后登录IP，varchar(64)

### 4.3 系统角色表字段
- name：角色名称，varchar(64)
- code：角色编码，varchar(64)
- sort：排序，int

### 4.4 系统权限表字段
- name：权限名称，varchar(64)
- code：权限编码，varchar(64)
- type：类型，tinyint
- parent_id：父级ID，bigint
- path：路由路径，varchar(255)
- component：组件路径，varchar(255)
- perms：权限标识，varchar(255)
- icon：图标，varchar(64)
- sort：排序，int

### 4.5 用户角色关联表字段
- user_id：用户ID，bigint
- role_id：角色ID，bigint

### 4.6 角色权限关联表字段
- role_id：角色ID，bigint
- permission_id：权限ID，bigint

### 4.7 系统菜单表字段
- name：菜单名称，varchar(64)
- parent_id：父级ID，bigint
- path：路由路径，varchar(255)
- component：组件路径，varchar(255)
- perms：权限标识，varchar(255)
- type：类型，tinyint
- icon：图标，varchar(64)
- sort：排序，int

### 4.8 系统部门表字段
- name：部门名称，varchar(64)
- parent_id：父级ID，bigint
- ancestors：祖级列表，varchar(255)
- leader：负责人，varchar(64)
- phone：联系电话，varchar(32)
- email：邮箱，varchar(64)
- sort：排序，int

### 4.9 系统岗位表字段
- name：岗位名称，varchar(64)
- code：岗位编码，varchar(64)
- dept_id：部门ID，bigint
- sort：排序，int

### 4.10 系统字典表字段
- name：字典名称，varchar(64)
- code：字典编码，varchar(64)

### 4.11 系统字典项表字段
- dict_id：字典ID，bigint
- label：标签，varchar(64)
- value：值，varchar(64)
- color：颜色，varchar(32)
- sort：排序，int

### 4.12 系统参数表字段
- name：参数名称，varchar(64)
- code：参数编码，varchar(64)
- value：参数值，varchar(255)
- type：类型，tinyint

### 4.13 系统日志表字段
- user_id：用户ID，bigint
- username：用户名，varchar(64)
- operation：操作，varchar(64)
- method：方法名，varchar(255)
- params：参数，text
- time：执行时长，bigint
- ip：IP地址，varchar(64)
- location：操作地点，varchar(255)
- user_agent：用户代理，varchar(255)
- error_msg：错误消息，varchar(255)

### 4.14 操作日志表字段
- user_id：用户ID，bigint
- username：用户名，varchar(64)
- module：模块，varchar(64)
- operation：操作，varchar(64)
- method：方法名，varchar(255)
- request_url：请求URL，varchar(255)
- request_method：请求方式，varchar(32)
- request_params：请求参数，text
- response_result：响应结果，text
- time：执行时长，bigint
- ip：IP地址，varchar(64)
- location：操作地点，varchar(255)
- user_agent：用户代理，varchar(255)
- error_msg：错误消息，varchar(255)

### 4.15 登录日志表字段
- user_id：用户ID，bigint
- username：用户名，varchar(64)
- ip：IP地址，varchar(64)
- location：登录地点，varchar(255)
- user_agent：用户代理，varchar(255)
- browser：浏览器，varchar(64)
- os：操作系统，varchar(64)
- msg：提示消息，varchar(255)
- login_time：登录时间，datetime

### 4.16 系统通知表字段
- title：标题，varchar(128)
- content：内容，text
- type：类型，tinyint
- sort：排序，int
- created_by：创建人，bigint

### 4.17 系统通知用户表字段
- notice_id：通知ID，bigint
- user_id：用户ID，bigint
- read_status：阅读状态，tinyint
- read_time：阅读时间，datetime

## 5. 更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-xx | v1.0.0 | 初始版本 | - | 