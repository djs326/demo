# API 使用指南

本指南详细介绍了如何使用项目的 API 接口，包括接口文档访问、统一响应格式、认证方式和常见接口示例。

## 目录

- [API 文档访问](#api-文档访问)
- [统一响应格式](#统一响应格式)
- [公共参数](#公共参数)
- [认证方式](#认证方式)
- [接口示例](#接口示例)
- [错误码说明](#错误码说明)

---

## API 文档访问

### Knife4j 文档

项目已集成 Knife4j（增强版 Swagger），可通过以下地址访问：

- **开发环境**: http://localhost:8080/doc.html
- **生产环境**: http://your-domain.com/doc.html

### OpenAPI 规范

原始 OpenAPI 3.0 规范文档地址：

- **JSON 格式**: http://localhost:8080/v3/api-docs
- **YAML 格式**: http://localhost:8080/v3/api-docs.yaml

---

## 统一响应格式

所有 API 接口均采用统一的响应格式：

### 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据
  }
}
```

### 失败响应

```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      // 数据列表
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200 表示成功 |
| message | string | 响应消息 |
| data | any | 响应数据 |

---

## 公共参数

### 请求头参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Content-Type | string | 是 | 请求内容类型，通常为 `application/json` |
| Authorization | string | 否 | 认证令牌，格式为 `Bearer {token}` |
| X-Trace-Id | string | 否 | 请求追踪 ID，用于链路追踪 |

---

## 认证方式

### JWT Token 认证

项目使用 JWT (JSON Web Token) 进行身份认证。

#### 1. 获取 Token

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

响应：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

#### 2. 使用 Token 访问接口

在请求头中添加 Authorization：

```bash
GET /api/users/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 3. Token 刷新

```bash
POST /api/auth/refresh
Authorization: Bearer {old_token}
```

#### 4. 登出

```bash
POST /api/auth/logout
Authorization: Bearer {token}
```

---

## 接口示例

### 示例接口 - Hello World

#### 接口描述

返回 Hello World 字符串，用于测试接口连通性。

#### 请求信息

```
GET /api/example/hello
```

#### 请求示例

```bash
curl -X GET http://localhost:8080/api/example/hello
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "Hello World!"
}
```

---

### 示例接口 - 获取用户信息

#### 接口描述

根据用户 ID 获取用户详情。

#### 请求信息

```
GET /api/example/user/{id}
```

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 用户 ID |

#### 请求示例

```bash
curl -X GET http://localhost:8080/api/example/user/1
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testUser1",
    "email": "test1@example.com"
  }
}
```

---

### 示例接口 - 获取用户列表

#### 接口描述

获取所有用户列表。

#### 请求信息

```
GET /api/example/users
```

#### 请求示例

```bash
curl -X GET http://localhost:8080/api/example/users
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "username": "user1",
      "email": "user1@example.com"
    },
    {
      "id": 2,
      "username": "user2",
      "email": "user2@example.com"
    }
  ]
}
```

---

### 示例接口 - 创建用户

#### 接口描述

创建一个新用户。

#### 请求信息

```
POST /api/example/user
Content-Type: application/json
```

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名 |
| email | string | 是 | 邮箱 |

#### 请求示例

```bash
curl -X POST http://localhost:8080/api/example/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newUser",
    "email": "newuser@example.com"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1234567890,
    "username": "newUser",
    "email": "newuser@example.com"
  }
}
```

---

### 示例接口 - 更新用户

#### 接口描述

根据用户 ID 更新用户信息。

#### 请求信息

```
PUT /api/example/user/{id}
Content-Type: application/json
```

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 用户 ID |

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 否 | 用户名 |
| email | string | 否 | 邮箱 |

#### 请求示例

```bash
curl -X PUT http://localhost:8080/api/example/user/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updatedUser",
    "email": "updated@example.com"
  }'
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "updatedUser",
    "email": "updated@example.com"
  }
}
```

---

### 示例接口 - 删除用户

#### 接口描述

根据用户 ID 删除用户。

#### 请求信息

```
DELETE /api/example/user/{id}
```

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 用户 ID |

#### 请求示例

```bash
curl -X DELETE http://localhost:8080/api/example/user/1
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

## 错误码说明

### HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 10001 | 参数校验失败 |
| 10002 | 用户不存在 |
| 10003 | 用户名或密码错误 |
| 10004 | Token 无效 |
| 10005 | Token 已过期 |
| 10006 | 无权限操作 |
| 20001 | 数据不存在 |
| 20002 | 数据已存在 |
| 50001 | 系统异常 |

---

## 使用 SDK 调用 (可选)

### JavaScript/Node.js 示例

```javascript
const axios = require('axios');

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
});

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// 调用示例
async function getUser(id) {
  try {
    const result = await api.get(`/api/example/user/${id}`);
    if (result.code === 200) {
      console.log('用户信息:', result.data);
    } else {
      console.error('错误:', result.message);
    }
  } catch (error) {
    console.error('请求失败:', error);
  }
}
```

### Python 示例

```python
import requests

BASE_URL = 'http://localhost:8080'

class ApiClient:
    def __init__(self, token=None):
        self.token = token
        self.session = requests.Session()
        if token:
            self.session.headers['Authorization'] = f'Bearer {token}'
    
    def get(self, path, params=None):
        response = self.session.get(f'{BASE_URL}{path}', params=params)
        response.raise_for_status()
        return response.json()
    
    def post(self, path, json=None):
        response = self.session.post(f'{BASE_URL}{path}', json=json)
        response.raise_for_status()
        return response.json()

# 使用示例
client = ApiClient()

# 获取用户
result = client.get('/api/example/user/1')
if result['code'] == 200:
    print('用户信息:', result['data'])
```

---

## 常见问题

### 1. 如何解决跨域问题？

项目已配置 CORS，开发环境允许所有来源访问。生产环境请在配置文件中限制允许的来源。

### 2. 接口返回 401 怎么办？

检查是否已登录并获取 Token，确保在请求头中正确添加了 `Authorization: Bearer {token}`。

### 3. 如何查看接口请求日志？

可以通过 Knife4j 文档页面的"调试"功能直接测试接口，或使用浏览器开发者工具查看网络请求。
