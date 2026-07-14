# 爱出行 - 自助旅行规划系统 部署指南

## 一、环境要求

| 软件 | 版本 | 下载地址 |
|------|------|----------|
| JDK | 21+ | https://jdk.java.net/21/ |
| MySQL | 8.0+ | https://dev.mysql.com/downloads/mysql/ |
| Redis | Windows版 | https://github.com/microsoftarchive/redis/releases |
| Nacos | 2.4+ | https://github.com/alibaba/nacos/releases |

## 二、环境配置

### 1. JDK
安装后设置环境变量 `JAVA_HOME`，指向 JDK 安装目录。

### 2. MySQL
- 安装时记住 **root 密码**（建议设为 `123456` 方便学习）
- 安装后执行建库脚本：
```
mysql -u root -p < sql/schema.sql
```

### 3. Redis
- 安装 Windows 版 Redis，默认端口 6379
- **配置文件中注释掉 `requirepass` 行**（本项目不使用密码）

### 4. Nacos
- 解压到任意目录
- 启动（CMD）：
```
set JAVA_HOME=你的JDK路径
cd nacos\bin
startup.cmd -m standalone
```
- 访问 http://localhost:8848/nacos 确认启动成功

## 三、启动服务

按以下顺序启动（每个开一个 CMD 窗口）：

### 1. 网关 (端口 8080)
```
java -jar jars/ailvxing-gateway-1.0-SNAPSHOT.jar
```

### 2. 用户服务 (端口 8001)
```
java -jar jars/ailvxing-user-1.0-SNAPSHOT.jar
```

### 3. 产品服务 (端口 8002)
```
java -jar jars/ailvxing-product-1.0-SNAPSHOT.jar
```

### 4. 规划服务 (端口 8003)
```
java -jar jars/ailvxing-plan-1.0-SNAPSHOT.jar
```

### 5. 订单服务 (端口 8004)
```
java -jar jars/ailvxing-order-1.0-SNAPSHOT.jar
```

## 四、启动前端

前端静态文件在 `frontend/` 目录。

**方式一：Nginx 部署**
```
将 frontend/ 目录配置为 Nginx 的静态文件目录，设置 /api 反向代理到 localhost:8080
```

**方式二：用 serve 快速启动**
```
npm install -g serve
cd frontend
serve -s . -l 5173
```
然后访问 http://localhost:5173

**方式三：Python 简易服务器**
```
cd frontend
python -m http.server 5173
```
(需要配置 API 代理，或修改 index.html 中的 base URL)

## 五、访问地址

- **用户端**：http://localhost:5173
- **管理员后台**：用 `admin / 123456` 登录后可见
- **接口文档**：http://localhost:8001/swagger-ui/index.html
- **Nacos 管理**：http://localhost:8848/nacos

---

### 技术栈
- 后端：Spring Cloud + Nacos + Gateway + OpenFeign + MyBatis + Redis
- 前端：Vue3 + Element Plus + Vite
- AI：DeepSeek 大模型
- 向量数据库：Qdrant Cloud
