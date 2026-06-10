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

## 7. Budget 模块 (`/api/budgets`) 【v2.0 新增】

### 预算列表
```
GET /api/budgets
```
**Header**: `satoken: {token}`
**响应 data**:
```json
[
  {
    "budgetId": 1,
    "categoryId": 1,
    "categoryName": "餐饮",
    "budgetAmount": 3000.00,
    "periodType": "MONTHLY",
    "periodYear": 2026,
    "periodMonth": 6
  }
]
```

### 新增预算
```
POST /api/budgets
```
**请求体**:
```json
{
  "categoryId": 1,
  "budgetAmount": 3000.00,
  "periodType": "MONTHLY",
  "periodYear": 2026,
  "periodMonth": 6
}
```
**响应 data**:
```json
{
  "budgetId": 1
}
```

### 修改预算
```
PUT /api/budgets/{id}
```
**请求体**:
```json
{
  "budgetAmount": 3500.00,
  "periodType": "MONTHLY",
  "periodYear": 2026,
  "periodMonth": 7
}
```

### 删除预算
```
DELETE /api/budgets/{id}
```
**说明**: 逻辑删除

### 预算进度概览
```
GET /api/budgets/overview
```
**参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| periodType | String | 是 | MONTHLY 或 YEARLY |
| periodYear | Integer | 是 | 年份 |
| periodMonth | Integer | 否 | 月份，periodType=MONTHLY 时必填 |

**响应 data**:
```json
[
  {
    "budgetId": 1,
    "categoryId": 1,
    "categoryName": "餐饮",
    "budgetAmount": 3000.00,
    "spentAmount": 2450.00,
    "remaining": 550.00,
    "percentage": 0.82
  }
]
```

---

## 8. Tag 模块 (`/api/tags`) 【v2.0 新增】

### 标签列表
```
GET /api/tags
```
**Header**: `satoken: {token}`
**响应 data**:
```json
[
  {
    "tagId": 1,
    "name": "工作餐",
    "color": "#FF6B6B"
  }
]
```

### 新增标签
```
POST /api/tags
```
**请求体**:
```json
{
  "name": "工作餐",
  "color": "#FF6B6B"
}
```
**响应 data**:
```json
{
  "tagId": 1
}
```

### 修改标签
```
PUT /api/tags/{id}
```
**请求体**:
```json
{
  "name": "加班餐",
  "color": "#FF0000"
}
```

### 删除标签
```
DELETE /api/tags/{id}
```
**说明**: 逻辑删除

---

## Record 模块补充 API 【v2.0 新增】

### 标签-记录关联
```
PUT /api/records/{id}/tags
```
**请求体**:
```json
{
  "tagIds": [1, 2, 3]
}
```
**说明**: 全量替换标签，传空数组清空所有标签

### 月度收支汇总
```
GET /api/records/statistics/monthly?year=2026
```
**响应 data**:
```json
[
  {
    "month": 1,
    "income": 5000.00,
    "expense": 3200.00
  },
  {
    "month": 2,
    "income": 5200.00,
    "expense": 2800.00
  }
]
```

### 年度收支对比
```
GET /api/records/statistics/annual?startYear=2025&endYear=2026
```
**响应 data**:
```json
[
  {
    "year": 2025,
    "income": 60000.00,
    "expense": 40000.00
  },
  {
    "year": 2026,
    "income": 35000.00,
    "expense": 22000.00
  }
]
```

### 记账搜索
```
GET /api/records?keyword=午餐
```
**参数**（在原有列表参数基础上增加）:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 搜索备注关键字 |

### 数据导出
```
GET /api/records/export?startDate=2026-01-01&endDate=2026-06-30&type=expense
```
**响应**: CSV 文件下载
**说明**: type 可选，不传则导出全部类型

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
  "recordId": 1,
  "categoryId": 1,
  "categoryName": "餐饮",
  "accountId": 1,
  "accountName": "微信",
  "type": "expense",
  "amount": 25.00,
  "remark": "午餐",
  "recordTime": "2026-06-04 12:30:00",
  "tagIds": []
}
```

---

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
| categoryIds | String | 否 | 分类 ID 列表，多个用逗号分隔 |
| type | String | 否 | 按类型筛选 (income/expense) |
| tagIds | String | 否 | 标签 ID 列表，多个用逗号分隔【v2.0 新增】 |
| keyword | String | 否 | 备注关键字搜索【v2.0 新增】 |

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
      "recordTime": "2026-06-04 12:30:00",
      "tagIds": [1, 2]
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

---

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
