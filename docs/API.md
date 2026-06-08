# CoinSec 后端 API 设计

**基础路径**: `/api`

**统一响应格式**:
| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 200 成功，其他失败 |
| msg | String | 提示信息 |
| data | Object | 响应数据 |

---

## 身份验证机制

本系统使用 **Sa-Token** 进行身份认证，采用 token 验证方式。

### 认证流程

```
用户登录 → 后端验证账号密码 → StpUtil.login(userId) 生成 token
  → 返回 token 给前端 → 前端存储 token
  → 后续请求在 Header 中携带 satoken: {token}
  → Sa-Token 拦截器自动校验 → 通过/拒绝
```

### 前端集成

登录后将 token 存入本地存储，并在每次请求时携带：

```javascript
// 登录成功后保存 token
uni.setStorageSync('satoken', token);

// 后续请求在 header 中携带
uni.request({
    url: 'https://api.example.com/api/records',
    header: {
        "satoken": uni.getStorageSync('satoken')
    },
    success: (res) => { ... }
});
```

### 接口保护规则

| 路径 | 保护策略 |
|------|---------|
| `POST /api/auth/login` | 不校验 token |
| `POST /api/auth/setup` | 不校验 token |
| `POST /api/auth/logout` | 需要 token |
| `/api/**`（其他） | 需要 token |

未登录时返回：`{ "code": 401, "msg": "未登录或 token 已过期", "data": null }`

---

## 1. Auth 模块 (`/api/auth`)

### 首次初始化
```
POST /api/auth/setup
```
**说明**: 用户表为空时可调用，设置初始管理员账户
**请求体**:
```json
{
  "username": "admin",
  "password": "your_password"
}
```
**响应 data**:
```json
{
  "userId": 1,
  "token": "xxx"
}
```

### 登录
```
POST /api/auth/login
```
**请求体**:
```json
{
  "username": "admin",
  "password": "your_password"
}
```
**响应 data**:
```json
{
  "userId": 1,
  "token": "xxx",
  "userInfo": {
    "username": "admin",
    "nickname": "Kody"
  }
}
```

### 登出
```
POST /api/auth/logout
```
**Header**: `satoken: {token}`
**响应 data**: null

---

## 2. User 模块 (`/api/user`)

### 获取用户信息
```
GET /api/user/info
```
**Header**: `satoken: {token}`
**响应 data**:
```json
{
  "userId": 1,
  "username": "admin",
  "nickname": "Kody",
  "avatar": "http://...",
  "createTime": "2026-01-01 12:00:00"
}
```

### 修改昵称
```
PUT /api/user/nickname
```
**请求体**:
```json
{
  "nickname": "新昵称"
}
```

### 修改密码
```
PUT /api/user/password
```
**请求体**:
```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

### 上传头像
```
POST /api/user/avatar
```
**Header**: `Content-Type: multipart/form-data`
**参数**: `file` (MultipartFile)
**响应 data**:
```json
{
  "avatarUrl": "/uploads/avatar_xxx.jpg"
}
```

---

## 3. Category 模块 (`/api/categories`)

### 分类列表
```
GET /api/categories
```
**参数**: `type` (可选，income/expense，不传返回全部)
**响应 data**:
```json
[
  {
    "categoryId": 1,
    "name": "餐饮",
    "type": "expense",
    "icon": "/icons/food.png",
    "sort": 1
  }
]
```

### 新增分类
```
POST /api/categories
```
**请求体**:
```json
{
  "name": "餐饮",
  "type": "expense",
  "icon": "/icons/food.png",
  "sort": 1
}
```
**响应 data**:
```json
{
  "categoryId": 1
}
```

### 修改分类
```
PUT /api/categories/{id}
```
**请求体**:
```json
{
  "name": "美食",
  "icon": "/icons/food2.png",
  "sort": 2
}
```

### 删除分类
```
DELETE /api/categories/{id}
```
**说明**: 逻辑删除（is_deleted = true）

---

## 4. Account 模块 (`/api/accounts`)

### 账户列表
```
GET /api/accounts
```
**响应 data**:
```json
[
  {
    "accountId": 1,
    "name": "微信",
    "icon": "/icons/wechat.png",
    "balance": 1000.00,
    "status": 1
  }
]
```

### 新增账户
```
POST /api/accounts
```
**请求体**:
```json
{
  "name": "支付宝",
  "icon": "/icons/alipay.png",
  "balance": 500.00
}
```
**响应 data**:
```json
{
  "accountId": 1
}
```

### 修改账户
```
PUT /api/accounts/{id}
```
**请求体**:
```json
{
  "name": "微信零钱",
  "icon": "/icons/wechat2.png",
  "status": 1
}
```
**注意**: balance 变更不通过此接口，通过记账/转账自动更新

### 删除账户
```
DELETE /api/accounts/{id}
```
**说明**: 逻辑删除

---

## 5. Record 模块 (`/api/records`)

### 新增记录
```
POST /api/records
```
**请求体**:
```json
{
  "categoryId": 1,
  "accountId": 1,
  "type": "expense",
  "amount": 25.00,
  "remark": "午餐",
  "recordTime": "2026-06-04 12:30:00"
}
```
**响应 data**:
```json
{
  "recordId": 1
}
```

### 记录列表
```
GET /api/records
```
**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认 1 |
| size | Integer | 否 | 每页条数，默认 20 |
| startDate | String | 否 | 开始日期 (yyyy-MM-dd) |
| endDate | String | 否 | 结束日期 (yyyy-MM-dd) |
| categoryId | Integer | 否 | 按分类筛选 |
| type | String | 否 | 按类型筛选 (income/expense) |

**响应 data**:
```json
{
  "records": [
    {
      "recordId": 1,
      "categoryId": 1,
      "categoryName": "餐饮",
      "accountId": 1,
      "accountName": "微信",
      "type": "expense",
      "amount": 25.00,
      "remark": "午餐",
      "recordTime": "2026-06-04 12:30:00"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

### 收支统计
```
GET /api/records/statistics
```
**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | String | 是 | 开始日期 (yyyy-MM-dd) |
| endDate | String | 是 | 结束日期 (yyyy-MM-dd) |

**响应 data**:
```json
{
  "totalIncome": 5000.00,
  "totalExpense": 3200.00,
  "categoryStats": [
    {
      "categoryId": 1,
      "categoryName": "餐饮",
      "type": "expense",
      "total": 1200.00,
      "count": 30
    }
  ]
}
```

---

## 6. Transfer 模块 (`/api/transfers`)

### 转账
```
POST /api/transfers
```
**请求体**:
```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.00,
  "remark": "转到支付宝",
  "transferTime": "2026-06-04 14:00:00"
}
```
**响应 data**:
```json
{
  "transferId": 1
}
```

### 转账记录
```
GET /api/transfers
```
**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认 1 |
| size | Integer | 否 | 每页条数，默认 20 |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

**响应 data**:
```json
{
  "transfers": [
    {
      "transferId": 1,
      "fromAccountId": 1,
      "fromAccountName": "微信",
      "toAccountId": 2,
      "toAccountName": "支付宝",
      "amount": 500.00,
      "remark": "转到支付宝",
      "transferTime": "2026-06-04 14:00:00"
    }
  ],
  "total": 10,
  "page": 1,
  "size": 20
}
```

---

## 通用错误码

| code | msg | 说明 |
|------|-----|------|
| 401 | 未登录或 token 已过期 | 需要重新登录 |
| 403 | 无权限访问 | - |
| 404 | 资源不存在 | - |
| 500 | 服务器内部错误 | - |
