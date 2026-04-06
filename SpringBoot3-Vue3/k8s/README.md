# Kubernetes部署配置

本目录包含完整的Kubernetes部署配置文件，用于部署SpringBoot3-Vue3应用的Kubernetes集群部署。

## 目录结构

```
k8s/
├── 01-namespace.yaml          # Namespace配置
├── 02-configmap.yaml           # ConfigMap配置（应用配置和Nginx配置
├── 03-secret.yaml              # Secret配置示例（敏感信息）
├── 04-backend-deployment.yaml   # 后端Deployment配置
├── 05-frontend-deployments.yaml  # 前端Deployment配置（front-admin和front-user）
├── 06-services.yaml             # Service配置
├── 07-ingress.yaml            # Ingress配置
├── 08-hpa.yaml                # HPA配置（自动扩缩容）
└── README.md                   # 说明文档
```

## 前置要求

1. Kubernetes集群（Minikube或其他Kubernetes集群）
2. kubectl配置完成
3. 已安装nginx ingress controller
4. Docker镜像已构建并推送到镜像仓库

## 使用步骤

### 1. 准备Docker镜像

首先需要构建并推送Docker镜像：

```bash
# 构建后端镜像
cd ../back
# 根据项目结构构建镜像
docker build -t your-backend-image:latest -f Dockerfile .
docker push your-backend-image:latest

# 构建front-admin镜像
cd ../front-admin
docker build -t your-front-admin-image:latest -f Dockerfile .
docker push your-front-admin-image:latest

# 构建front-user镜像
cd ../front-user
docker build -t your-front-user-image:latest -f Dockerfile .
docker push your-front-user-image:latest
```

### 2. 更新配置Secret中的镜像地址

在部署之前，需要更新以下配置文件中的镜像地址：

- `04-backend-deployment.yaml` 中的 `image: your-backend-image:latest`
- `05-frontend-deployments.yaml` 中的 `your-front-admin-image:latest` 和 `your-front-user-image:latest`

### 3. 更新Secret中的敏感信息

当前Secret中使用的是base64编码的示例值，请根据实际情况更新：

```bash
# 生成新的base64编码值
echo -n "your-db-password" | base64
echo -n "your-redis-password" | base64
echo -n "your-jwt-secret" | base64
```

将生成的值更新到 `03-secret.yaml` 文件中。

### 4. 部署应用

按顺序执行以下命令部署应用：

```bash
# 1. 创建Namespace
kubectl apply -f 01-namespace.yaml

# 2. 创建ConfigMap
kubectl apply -f 02-configmap.yaml

# 3. 创建Secret
kubectl apply -f 03-secret.yaml

# 4. 创建后端Deployment
kubectl apply -f 04-backend-deployment.yaml

# 5. 创建前端Deployments
kubectl apply -f 05-frontend-deployments.yaml

# 6. 创建Services
kubectl apply -f 06-services.yaml

# 7. 创建Ingress
kubectl apply -f 07-ingress.yaml

# 8. 创建HPA
kubectl apply -f 08-hpa.yaml
```

或者使用一条命令部署所有资源：

```bash
kubectl apply -f .
```

### 5. 验证部署

```bash
# 查看命名空间
kubectl get namespace

# 查看Pod状态
kubectl get pods -n springboot-vue-demo

# 查看Service
kubectl get svc -n springboot-vue-demo

# 查看Deployment
kubectl get deployments -n springboot-vue-demo

# 查看Ingress
kubectl get ingress -n springboot-vue-demo

# 查看HPA
kubectl get hpa -n springboot-vue-demo

# 查看Pod详细信息
kubectl describe pod <pod-name> -n springboot-vue-demo

# 查看Pod日志
kubectl logs <pod-name> -n springboot-vue-demo
```

### 6. 配置Hosts（本地测试）

在本地 `/etc/hosts` 文件中添加以下配置（将IP替换为Ingress Controller的IP）：

```
<ingress-ip> admin.example.com
<ingress-ip> user.example.com
<ingress-ip> api.example.com
```

获取Ingress Controller IP：

```bash
kubectl get ingress -n springboot-vue-demo
```

## 健康检查

- **后端健康检查**：
  - Liveness: `/actuator/health/liveness`
  - Readiness: `/actuator/health/readiness`
  
- **前端健康检查**：
  - Liveness和Readiness: `/`

## 资源限制

- **后端**：
  - CPU请求：500m
  - 内存请求：512Mi
  - CPU限制：1000m
  - 内存限制：1Gi

- **前端**：
  - CPU请求：100m
  - 内存请求：128Mi
  - CPU限制：200m
  - 内存限制：256Mi

## 自动扩缩容（HPA）

- **后端**：
  - 最小副本数：2
  - 最大副本数：10
  - CPU目标利用率：70%
  - 内存目标利用率：80%

- **前端**：
  - 最小副本数：2
  - 最大副本数：5
  - CPU目标利用率：60%
  - 内存目标利用率：70%

## 清理资源

如需清理所有部署的资源：

```bash
kubectl delete -f .
```

或者删除整个Namespace：

```bash
kubectl delete namespace springboot-vue-demo
```

## 注意事项

1. 确保镜像名称和镜像仓库配置正确
2. Secret中的敏感信息需要使用base64编码
3. 确保nginx ingress controller已正确安装
4. 根据实际需求调整资源限制和HPA配置
5. 生产环境建议使用HTTPS配置
