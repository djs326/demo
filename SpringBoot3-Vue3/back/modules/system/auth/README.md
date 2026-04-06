# 认证模块 (Auth Module)

## 功能说明

本模块实现了完整的用户认证与授权功能，包括：

1. **JWT工具类**：生成、解析Token
2. **Token版本号撤销机制**：使用Redis存储版本号，支持版本号递增撤销
3. **登录接口**：账号密码登录
4. **Token刷新机制**：使用Refresh Token刷新Access Token
5. **登出接口**：用户登出并撤销Token
6. **认证过滤器**：JWT认证过滤器
7. **Spring Security配置**：安全配置

## Token配置

- **Access Token**：有效期30分钟
- **Refresh Token**：有效期7天
- **存储方式**：HttpOnly Cookie（防止XSS攻击）

## API接口

### 1. 用户登录

**接口**：`POST /api/auth/login`

**请求参数**：
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应**：
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "name": "管理员"
  },
  "traceId": "xxx"
}
```

### 2. 刷新Token

**接口**：`POST /api/auth/refresh`

**说明**：自动从Cookie中读取Refresh Token

**响应**：
```json
{
  "code": "0",
  "message": "success",
  "data": null,
  "traceId": "xxx"
}
```

### 3. 用户登出

**接口**：`POST /api/auth/logout`

**说明**：递增Token版本号，使所有旧Token失效

**响应**：
```json
{
  "code": "0",
  "message": "success",
  "data": null,
  "traceId": "xxx"
}
```

## 数据库初始化

执行 `src/main/resources/init.sql` 初始化数据库和测试用户：

- 用户名：`admin`
- 密码：`123456`

## 配置说明

在 `application.yml` 中配置：

```yaml
jwt:
  secret: defaultSecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong
  access-token-expiration: 1800000  # 30分钟（毫秒）
  refresh-token-expiration: 604800000  # 7天（毫秒）
  cookie:
    domain: localhost
    path: /
    secure: false
    http-only: true
```

## Token撤销机制

使用Redis存储Token版本号，Key格式：`{env}:user:token:version:{userId}`

- 登出时：版本号+1
- 权限变更时：版本号+1
- 用户封禁时：版本号+1

所有旧Token的版本号小于Redis中的版本号时，认证失败。

## 依赖

- Spring Boot 3.2.x
- Spring Security
- Spring Data Redis
- MyBatis-Plus
- JJWT 0.12.3
