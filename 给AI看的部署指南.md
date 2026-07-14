# 爱出行-自助旅行规划系统 完整部署指南

## 背景
这是一个 Spring Cloud 微服务 + Vue3 的旅行规划系统。需要依次安装环境、初始化数据库、启动服务。

## 你的角色
你现在是部署工程师，请按以下步骤一步步完成部署。每完成一步告诉我结果，遇到报错贴给我我来分析。

---

## 步骤1：确认 JDK 21 已安装
执行命令：
```cmd
java -version
```
期望看到：`java version "21"` 或更高版本。
如果没装，去 https://jdk.java.net/21/ 下载安装，设置 JAVA_HOME 环境变量。

## 步骤2：安装 MySQL 8.0
已有则跳过。确保 root 密码设为 `123456`。

确认连接：
```cmd
mysql -u root -p123456 -e "SELECT VERSION();"
```

## 步骤3：初始化数据库
解压后的目录里有个 `sql/schema.sql`，执行它：
```cmd
mysql -u root -p123456 < sql/schema.sql
```
这会创建 `demo` 数据库和所有表。验证：
```cmd
mysql -u root -p123456 -e "USE demo; SHOW TABLES;"
```
期望看到 16 张表。

然后导入预置数据（管理员账号、测试用户、100个旅游产品、产品标签）：
```cmd
mysql -u root -p123456 < sql/seed_data.sql
```
验证：
```cmd
mysql -u root -p123456 -e "SELECT COUNT(*) FROM demo.product; SELECT username,role FROM demo.user;"
```
期望：100个产品，admin(管理员)、demo(普通用户)等测试账号。

## 步骤4：安装 Redis
去 https://github.com/microsoftarchive/redis/releases 下载 Windows 版 Redis-x64.msi，安装。

打开 `redis.windows.conf`（通常在 `C:\Program Files\Redis\` 或安装目录），找到 `requirepass` 那行，前面加 `#`注释掉。

启动 Redis（如果没自动启动的话）：
```cmd
redis-cli ping
```
期望返回 `PONG`。

## 步骤5：安装 Nacos
去 https://github.com/alibaba/nacos/releases 下载 `nacos-server-2.4.3.zip`，解压到比如 `D:\nacos`。

启动（新开一个 CMD 窗口）：
```cmd
set JAVA_HOME=你的JDK路径（如 C:\Program Files\Java\jdk-21）
cd D:\nacos\bin
startup.cmd -m standalone
```
等 20 秒，浏览器打开 http://localhost:8848/nacos ，看到登录页就行。

## 步骤6：启动后端服务
**按顺序**新开 5 个 CMD 窗口，逐个执行：

窗口1 - 网关：
```cmd
java -jar jars/ailvxing-gateway-1.0-SNAPSHOT.jar
```

窗口2 - 用户服务：
```cmd
java -jar jars/ailvxing-user-1.0-SNAPSHOT.jar
```

窗口3 - 产品服务：
```cmd
java -jar jars/ailvxing-product-1.0-SNAPSHOT.jar
```

窗口4 - 规划服务：
```cmd
java -jar jars/ailvxing-plan-1.0-SNAPSHOT.jar
```

窗口5 - 订单服务：
```cmd
java -jar jars/ailvxing-order-1.0-SNAPSHOT.jar
```

每个窗口看到类似 `Started xxxApplication in X seconds` 就是启动成功。
全部启动后，浏览器打开 http://localhost:8848/nacos 服务列表，应看到 5 个服务注册。

## 步骤7：启动前端
方式一（推荐）：
```cmd
npm install -g serve
cd frontend
serve -s . -l 5173
```

方式二（需要 Node.js，适合开发时用）：
```cmd
npm run dev
```

浏览器打开 http://localhost:5173

## 步骤8：验证
- 打开 http://localhost:5173 → 自动跳转欢迎页
- 点"立即登录" → 使用 demo / 123456 登录
- 试试"智能规划"生成行程
- 试试浏览产品和下单

## 常见问题

| 问题 | 解决 |
|------|------|
| mysql 命令找不到 | 把 MySQL bin 目录加到 PATH |
| jar 启动报 Unable to connect to Redis | Redis 没启动或密码没注释掉 |
| jar 启动报 DataSource | MySQL 没启动或密码不对 |
| Nacos 页面打不开 | Nacos 没启动或端口被占 |
| 生成规划失败 | DeepSeek API Key 余额不足 |
| 产品列表为空 | Redis 缓存里是旧数据，清一下 |
