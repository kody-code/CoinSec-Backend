# CoinSec Backend

Spring Boot 4.0.6 + Java 25 RESTful 后端服务，为个人财务管理应用 CoinSec 提供 API 支持。

## 技术栈

- **Spring Boot 4.0.6** + Spring Data JPA
- **Java 25** (toolchain)
- **PostgreSQL** (数据库)
- **Sa-Token 1.45.0** (认证授权)
- **Flyway** (生产环境数据库迁移)
- **Lombok** (简化代码)
- **jBCrypt 0.4** (密码加密)

## 项目结构

```
src/main/java/com/kody/coinsec/backend/
├── BackendApplication.java          # 启动类
├── common/
│   ├── exception/                   # 全局异常处理 (BusinessException, GlobalExceptionHandler)
│   └── result/                      # 统一响应格式 Result<T>
├── config/                          # 配置类 (CORS, Sa-Token, Flyway)
├── controller/                      # 接口层
├── dto/                             # 数据传输对象
├── entity/model/                    # JPA 实体
├── mapper/dao/                      # Repository 数据访问层
├── service/                         # 业务逻辑接口
│   └── impl/                        # 业务逻辑实现
└── util/                            # 工具类 (密码编码, 时间工具)
```

## API 概览

所有接口以 `/api` 为前缀，统一返回格式 `{ code, msg, data }`。除登录和初始化接口外，其余需在请求头携带 `satoken`。

| 模块 | 路径 | 说明 | 认证 |
|------|------|------|------|
| 认证 | `POST /api/auth/setup` | 首次初始化 | 否 |
| 认证 | `POST /api/auth/login` | 登录 | 否 |
| 认证 | `POST /api/auth/logout` | 登出 | 是 |
| 用户 | `GET /api/user/info` | 获取个人信息 | 是 |
| 用户 | `PUT /api/user/nickname` | 修改昵称 | 是 |
| 用户 | `PUT /api/user/password` | 修改密码 | 是 |
| 用户 | `POST /api/user/avatar` | 上传头像 | 是 |
| 账户 | `GET /api/accounts` | 账户列表 | 是 |
| 账户 | `POST /api/accounts` | 新增账户 | 是 |
| 账户 | `PUT /api/accounts/{id}` | 编辑账户 | 是 |
| 账户 | `DELETE /api/accounts/{id}` | 删除账户(逻辑) | 是 |
| 分类 | `GET /api/categories` | 分类列表(支持 type 过滤) | 是 |
| 分类 | `POST /api/categories` | 新增分类 | 是 |
| 分类 | `PUT /api/categories/{id}` | 编辑分类 | 是 |
| 分类 | `DELETE /api/categories/{id}` | 删除分类(逻辑) | 是 |
| 记录 | `GET /api/records` | 分页记录列表(多维度筛选) | 是 |
| 记录 | `POST /api/records` | 新增记账 | 是 |
| 记录 | `PUT /api/records/{id}` | 编辑记账 | 是 |
| 记录 | `DELETE /api/records/{id}` | 删除记账(逻辑) | 是 |
| 记录 | `GET /api/records/statistics` | 收支统计 | 是 |
| 转账 | `POST /api/transfers` | 新增转账 | 是 |
| 转账 | `GET /api/transfers` | 分页转账记录 | 是 |
| 转账 | `DELETE /api/transfers/{id}` | 删除转账 | 是 |
| 图标 | `GET /api/icons/{name}.svg` | 静态图标文件 | 否 |

详细 API 文档请参考 [docs/API.md](docs/API.md)。

## 数据库

- PostgreSQL，默认连接 `localhost:5432/coinsec_db`
- **开发环境**：JPA `ddl-auto: update`，实体自动建表
- **生产环境**：Flyway 管理迁移，`ddl-auto: none`

### 核心表

所有表均使用逻辑删除（`is_deleted` 字段）。

| 表 | 说明 |
|----|------|
| `users` | 用户信息 |
| `accounts` | 账户（微信/支付宝/银行卡等，含余额） |
| `categories` | 收支分类（收入/支出，含图标） |
| `records` | 记账流水（核心流水表） |
| `transfers` | 转账记录（账户间转账） |

详细设计请参考 [docs/数据库设计.md](docs/数据库设计.md) 和 [docs/后端模块.md](docs/后端模块.md)。

## 启动

```bash
./gradlew bootRun
```

默认端口 `8080`，激活 dev profile（`spring.profiles.active: dev`）。

## 测试

```bash
./gradlew test
```

JUnit 5 + Spring Boot Test，覆盖 Controller 层和 Service 层。

## 构建与发布

```bash
./gradlew bootJar
```

CI/CD: 推送 `v*` 标签触发 GitHub Actions，自动构建 JAR 并发布到 GitHub Releases (JDK 25)。

## 项目版本

当前版本：`2.1.2`