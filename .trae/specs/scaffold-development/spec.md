# Spring Boot + MyBatis-Plus + Vue3 通用开发脚手架 - Product Requirement Document

## Overview
- **Summary**: 开发一个基于Spring Boot 3.2.x + MyBatis-Plus 3.5.x + Vue 3.x的企业级通用开发脚手架，包含前后端分离架构、RBAC权限体系、多级缓存、可观测性、容器化部署等完整功能。
- **Purpose**: 为企业项目提供一个可直接大规模上线的脚手架框架，减少重复开发工作，提升开发效率和代码质量。
- **Target Users**: 企业开发团队、技术架构师、全栈开发工程师

## Goals
- 搭建完整的前后端分离架构（front-admin、front-user、back）
- 实现企业级RBAC权限体系（菜单、按钮、API、数据、字段权限）
- 实现JWT认证机制（Access Token + Refresh Token + 版本号撤销）
- 实现多级缓存（Caffeine本地缓存 + Redis分布式缓存）
- 实现可观测性体系（日志、指标、链路追踪、告警）
- 实现容器化部署（Docker + Kubernetes）
- 提供代码生成工具，提升开发效率

## Non-Goals (Out of Scope)
- 不实现具体的业务功能（如订单、商品等）
- 不实现微服务架构（当前采用Modular Monolith）
- 不实现第三方SSO集成（仅提供框架支持）
- 不实现复杂的数据分析功能

## Background & Context
- 用户提供了参考架构文档（/workspace/技术架构设计文档.md）
- 技术栈已确定：Spring Boot 3.2.x + MyBatis-Plus 3.5.x + Vue 3.x
- 需要支持容器化部署，可直接大规模上线
- 采用Modular Monolith架构，便于后续演进

## Functional Requirements
- **FR-1**: 实现用户认证与授权（登录、登出、Token刷新、JWT版本号撤销）
- **FR-2**: 实现RBAC权限管理（用户、角色、菜单、权限、部门管理）
- **FR-3**: 实现数据权限控制（ALL/DEPT/DEPT_AND_CHILD/SELF/CUSTOM）
- **FR-4**: 实现字段级权限控制（visible/readonly/mask/hidden）
- **FR-5**: 实现多级缓存机制（Caffeine + Redis）
- **FR-6**: 实现审计日志（操作日志、登录日志）
- **FR-7**: 实现限流、降级、熔断机制
- **FR-8**: 实现幂等设计
- **FR-9**: 实现可观测性（日志、指标、Trace）
- **FR-10**: 提供代码生成工具
- **FR-11**: 实现Docker容器化
- **FR-12**: 实现Kubernetes部署配置

## Non-Functional Requirements
- **NFR-1**: 系统支持水平扩展
- **NFR-2**: 响应时间P95 < 2秒
- **NFR-3**: 支持1000+ QPS
- **NFR-4**: 数据库查询性能优化（使用部门闭包表）
- **NFR-5**: 安全加固（XSS、CSRF、SQL注入防护）
- **NFR-6**: 完善的错误处理和异常码体系
- **NFR-7**: 统一响应结构

## Constraints
- **Technical**: 必须使用Spring Boot 3.2.x、MyBatis-Plus 3.5.x、Vue 3.x、MySQL 8.0+、Redis 7.0+
- **Business**: 需要在3个月内完成核心功能开发
- **Dependencies**: 依赖Redis、MySQL、Kubernetes环境

## Assumptions
- 开发团队熟悉Spring Boot和Vue技术栈
- 生产环境有Kubernetes集群
- 有完善的CI/CD流程
- 开发人员具备容器化部署经验

## Acceptance Criteria

### AC-1: 用户登录与认证
- **Given**: 用户已注册且账号启用
- **When**: 用户提交正确的用户名和密码
- **Then**: 系统返回Access Token和Refresh Token（存储在HttpOnly Cookie中）
- **Verification**: programmatic
- **Notes**: Token需包含userId和version字段

### AC-2: Token版本号撤销机制
- **Given**: 用户已登录
- **When**: 管理员封禁用户或变更用户权限
- **Then**: Redis中用户的Token版本号递增，旧Token立即失效
- **Verification**: programmatic

### AC-3: 数据权限控制
- **Given**: 用户有"DEPT_AND_CHILD"数据权限
- **When**: 用户查询用户列表
- **Then**: 只返回本部门及子部门的用户数据
- **Verification**: programmatic
- **Notes**: 使用部门闭包表优化查询性能

### AC-4: 字段权限脱敏
- **Given**: 用户角色配置了手机号字段脱敏权限
- **When**: 用户查询包含手机号的用户信息
- **Then**: 手机号显示为脱敏格式（如138****1234）
- **Verification**: human-judgment

### AC-5: 多级缓存机制
- **Given**: 系统已配置Caffeine和Redis缓存
- **When**: 第一次查询用户权限
- **Then**: 数据从数据库加载并缓存到Redis和Caffeine
- **Verification**: programmatic

### AC-6: 容器化部署
- **Given**: 代码已构建完成
- **When**: 执行Docker build和kubectl apply
- **Then**: 应用成功部署到Kubernetes集群并可访问
- **Verification**: programmatic

### AC-7: 可观测性
- **Given**: 系统正在运行
- **When**: 发起请求
- **Then**: 日志包含traceId、spanId、耗时等字段，Prometheus指标可采集
- **Verification**: human-judgment

## Open Questions
- [ ] 消息队列是否需要实现（RocketMQ/Kafka）？
- [ ] 是否需要实现文件上传功能？
- [ ] 代码生成工具的模板定制程度？
