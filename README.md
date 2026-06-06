# CoinSec Backend

Spring Boot 4.0.6 + Java 25 RESTful 后端服务。

## 技术栈

- Spring Boot 4.0.6
- Java 25
- Spring Data JPA + PostgreSQL
- Sa-Token（认证授权）
- Lombok
- jBCrypt（密码加密）

## 项目结构

```
src/main/java/com/kody/coinsec/backend/
├── BackendApplication.java      # 启动类
├── common/
│   ├── exception/               # 全局异常处理
│   └── result/                  # 统一响应格式
├── config/                      # 配置类
├── controller/                  # 接口层
│   ├── AuthController.java
│   ├── UserController.java
│   ├── AccountController.java
│   ├── CategoryController.java
│   ├── RecordController.java
│   └── TransferController.java
├── dto/                         # 数据传输对象
├── entity/model/                # 数据库实体
├── mapper/dao/                  # 数据访问层
├── service/                     # 业务逻辑层
│   └── impl/
└── util/                        # 工具类
```

## 数据库

- PostgreSQL，默认连接 `localhost:5432/coinsec_db`
- 用户名/密码：`coinsec` / `097016@CoinSec`
- JPA `ddl-auto: update`，实体自动建表/更新

### 核心表

| 表 | 说明 |
|----|------|
| `users` | 用户 |
| `accounts` | 账户 |
| `categories` | 分类 |
| `records` | 记账记录 |
| `transfers` | 转账记录 |

## API

| 模块 | 路径 | 说明 |
|------|------|------|
| 认证 | `/auth/**` | 登录、注册、初始化 |
| 用户 | `/user/**` | 个人信息、修改密码 |
| 账户 | `/account/**` | 账户 CRUD |
| 分类 | `/category/**` | 分类 CRUD |
| 记录 | `/record/**` | 记账记录 CRUD |
| 转账 | `/transfer/**` | 转账记录 |

所有接口统一返回格式：`{ code, msg, data }`。

## 启动

```bash
./gradlew bootRun
```

默认端口 `8080`，dev 配置文件激活（`spring.profiles.active: dev`）。

## 测试

```bash
./gradlew test
```

JUnit 5 + Spring Boot Test。
