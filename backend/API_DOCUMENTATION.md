# 食品溯源平台后端 API 接口文档

## 基础信息
- **Base URL**: `http://localhost:8080`
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- `code`: 状态码（200-成功，500-失败）
- `message`: 响应消息
- `data`: 响应数据

---

## 1. 用户管理模块

### 1.1 用户登录
- **接口**: `POST /api/user/login`
- **描述**: 用户登录认证
- **请求参数**:
```json
{
  "loginCode": "admin",
  "password": "123456"
}
```
- **响应数据**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "token_1",
    "userInfo": {
      "userId": 1,
      "userType": 0,
      "loginCode": "admin",
      "enterpriseId": null,
      "status": 1
    }
  }
}
```

### 1.2 注册企业用户
- **接口**: `POST /api/user/register`
- **描述**: 注册新的企业用户
- **请求参数**:
```json
{
  "userType": 1,
  "loginCode": "breeding001",
  "password": "123456",
  "enterpriseId": 1
}
```

### 1.3 获取用户信息
- **接口**: `GET /api/user/{userId}`
- **描述**: 根据用户ID获取用户详细信息

### 1.4 更新用户信息
- **接口**: `PUT /api/user/{userId}`
- **描述**: 更新用户基本信息

### 1.5 修改密码
- **接口**: `PUT /api/user/password/{userId}`
- **描述**: 修改用户密码
- **请求参数**:
```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

---

## 2. 企业管理模块

### 2.1 分页查询企业列表
- **接口**: `GET /api/enterprise/page`
- **描述**: 分页查询企业信息
- **请求参数**:
  - `pageNum` (int, 可选): 页码，默认1
  - `pageSize` (int, 可选): 每页数量，默认10
  - `enterpriseType` (int, 可选): 企业类型（1-养殖，2-屠宰，3-批发，4-零售）
  - `province` (string, 可选): 省份
- **示例**: `GET /api/enterprise/page?pageNum=1&pageSize=10&enterpriseType=1`

### 2.2 创建企业
- **接口**: `POST /api/enterprise`
- **描述**: 创建新企业
- **请求参数**:
```json
{
  "enterpriseName": "某某养殖场",
  "enterpriseType": 1,
  "province": "山东省",
  "registerTime": "2025-01-01",
  "contactPhone": "13800138000",
  "address": "山东省济南市某某区某某街道123号"
}
```

### 2.3 获取企业详情
- **接口**: `GET /api/enterprise/{enterpriseId}`
- **描述**: 根据企业ID获取详细信息

### 2.4 更新企业信息
- **接口**: `PUT /api/enterprise/{enterpriseId}`
- **描述**: 更新企业基本信息

### 2.5 删除企业
- **接口**: `DELETE /api/enterprise/{enterpriseId}`
- **描述**: 删除企业（慎用）

### 2.6 启用/禁用企业
- **接口**: `PUT /api/enterprise/status/{enterpriseId}`
- **描述**: 修改企业状态
- **请求参数**:
```json
{
  "status": 0
}
```
- `status`: 0-禁用，1-启用

### 2.7 获取企业注册统计
- **接口**: `GET /api/enterprise/statistics`
- **描述**: 获取企业注册的统计数据（按省份、月份、类型）
- **响应数据**:
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "provinceStats": {
      "山东省": 30,
      "河北省": 20
    },
    "monthStats": {
      "2025-01": 10,
      "2025-02": 15
    },
    "typeStats": {
      "1": 40,
      "2": 30,
      "3": 20,
      "4": 10
    }
  }
}
```

### 2.8 获取企业列表（不分页）
- **接口**: `GET /api/enterprise/list`
- **描述**: 获取企业列表，用于下拉选择
- **请求参数**:
  - `enterpriseType` (int, 可选): 企业类型

---

## 3. 证件类型管理模块

### 3.1 获取所有证件类型
- **接口**: `GET /api/certificate-type/list`
- **描述**: 获取所有证件类型列表

### 3.2 根据企业类型获取适用证件
- **接口**: `GET /api/certificate-type/by-enterprise-type/{enterpriseType}`
- **描述**: 获取指定企业类型需要的证件类型
- **示例**: `GET /api/certificate-type/by-enterprise-type/1`

### 3.3 获取证件类型详情
- **接口**: `GET /api/certificate-type/{certTypeId}`

### 3.4 创建证件类型
- **接口**: `POST /api/certificate-type`
- **描述**: 创建新的证件类型（管理员）

### 3.5 更新证件类型
- **接口**: `PUT /api/certificate-type/{certTypeId}`

### 3.6 删除证件类型
- **接口**: `DELETE /api/certificate-type/{certTypeId}`

---

## 4. 企业证件管理模块

### 4.1 获取企业所有证件
- **接口**: `GET /api/enterprise-certificate/enterprise/{enterpriseId}`
- **描述**: 获取指定企业的所有证件

### 4.2 添加企业证件
- **接口**: `POST /api/enterprise-certificate`
- **描述**: 为企业添加资质证件
- **请求参数**:
```json
{
  "enterpriseId": 1,
  "certTypeId": 1,
  "certNo": "91370000123456789X",
  "validUntil": "2026-12-31"
}
```

### 4.3 获取证件详情
- **接口**: `GET /api/enterprise-certificate/{certId}`

### 4.4 更新证件信息
- **接口**: `PUT /api/enterprise-certificate/{certId}`

### 4.5 删除证件
- **接口**: `DELETE /api/enterprise-certificate/{certId}`

### 4.6 检查过期证件
- **接口**: `POST /api/enterprise-certificate/check-expired`
- **描述**: 系统检查并更新过期证件状态

---

## 5. 产品批号管理模块

### 5.1 创建养殖批号
- **接口**: `POST /api/batch/breeding`
- **描述**: 养殖企业创建新批号
- **请求参数**:
```json
{
  "enterpriseId": 1,
  "productVariety": "猪肉",
  "certNo": "动检证20250101001"
}
```
- **说明**: 系统自动生成批号，格式：养殖_YYYYMMDD_序号

### 5.2 创建下游批号
- **接口**: `POST /api/batch/downstream`
- **描述**: 屠宰/批发/零售企业创建批号
- **请求参数**:
```json
{
  "enterpriseId": 2,
  "upstreamBatchId": 1,
  "productVariety": "猪肉",
  "certNo": "肉检证20250101001"
}
```

### 5.3 发布养殖批号
- **接口**: `PUT /api/batch/publish/{batchId}`
- **描述**: 养殖企业发布批号（状态：待发布→已发布）

### 5.4 批号下架
- **接口**: `PUT /api/batch/offline/{batchId}`
- **描述**: 批号下架操作

### 5.5 分页查询批号列表
- **接口**: `GET /api/batch/page`
- **描述**: 分页查询批号
- **请求参数**:
  - `pageNum` (int): 页码
  - `pageSize` (int): 每页数量
  - `enterpriseId` (long, 可选): 企业ID
  - `batchStatus` (int, 可选): 批号状态

### 5.6 获取批号详情
- **接口**: `GET /api/batch/{batchId}`

### 5.7 获取可关联的上游批号
- **接口**: `GET /api/batch/available-upstream/{enterpriseType}`
- **描述**: 获取当前企业类型可以关联的上游批号列表
- **示例**: `GET /api/batch/available-upstream/2` （屠宰企业获取可用的养殖批号）

### 5.8 溯源查询
- **接口**: `GET /api/batch/trace/{batchId}`
- **描述**: 根据批号ID查询完整的溯源链路
- **响应**: 返回从当前批号到源头的完整链路数组

### 5.9 更新批号信息
- **接口**: `PUT /api/batch/{batchId}`

### 5.10 删除批号
- **接口**: `DELETE /api/batch/{batchId}`

---

## 6. 确认请求管理模块

### 6.1 发起确认请求
- **接口**: `POST /api/confirmation`
- **描述**: 下游企业向上游企业发起批号确认请求
- **请求参数**:
```json
{
  "initiateEnterpriseId": 2,
  "batchId": 1
}
```
- **说明**: 系统自动获取上游企业ID

### 6.2 确认请求
- **接口**: `PUT /api/confirmation/confirm/{confirmId}`
- **描述**: 上游企业确认请求（批号状态：待确认→已确认）

### 6.3 拒绝请求
- **接口**: `PUT /api/confirmation/reject/{confirmId}`
- **描述**: 上游企业拒绝请求
- **请求参数**:
```json
{
  "rejectReason": "产品信息不符"
}
```

### 6.4 分页查询确认请求
- **接口**: `GET /api/confirmation/page`
- **描述**: 查询确认请求列表
- **请求参数**:
  - `pageNum` (int): 页码
  - `pageSize` (int): 每页数量
  - `enterpriseId` (long, 可选): 企业ID
  - `confirmStatus` (int, 可选): 请求状态（0-待确认，1-已确认，2-已拒绝）
  - `type` (string, 可选): 请求类型（initiate-发起的，receive-接收的）

### 6.5 获取确认请求详情
- **接口**: `GET /api/confirmation/{confirmId}`

---

## 7. 溯源码管理模块

### 7.1 生成溯源码
- **接口**: `POST /api/trace-code/generate/{batchId}`
- **描述**: 为零售批号生成溯源码（UUID格式）
- **响应数据**:
```json
{
  "code": 200,
  "data": {
    "traceCode": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
  }
}
```

### 7.2 溯源码查询
- **接口**: `GET /api/trace-code/trace/{traceCode}`
- **描述**: 消费者通过溯源码查询完整溯源信息
- **示例**: `GET /api/trace-code/trace/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6`
- **响应**: 返回从零售到养殖的完整溯源链路

### 7.3 批量生成溯源码
- **接口**: `POST /api/trace-code/batch-generate`
- **描述**: 批量为多个批号生成溯源码
- **请求参数**:
```json
{
  "batchIds": [1, 2, 3]
}
```

---

## 8. 数据字典

### 8.1 用户类型 (userType)
- `0`: 系统管理员
- `1`: 养殖企业
- `2`: 屠宰企业
- `3`: 批发企业
- `4`: 零售企业

### 8.2 企业类型 (enterpriseType)
- `1`: 养殖
- `2`: 屠宰
- `3`: 批发
- `4`: 零售

### 8.3 批号状态 (batchStatus)
**养殖企业：**
- `0`: 待发布
- `1`: 已发布
- `2`: 已下架

**屠宰/批发/零售企业：**
- `0`: 新建
- `1`: 待确认
- `2`: 已确认
- `3`: 已下架

### 8.4 确认请求状态 (confirmStatus)
- `0`: 待确认
- `1`: 已确认
- `2`: 已拒绝

### 8.5 证件状态 (status)
- `0`: 过期/无效
- `1`: 有效

### 8.6 溯源码状态 (status)
- `0`: 无效
- `1`: 有效

---

## 9. 业务流程说明

### 9.1 养殖企业批号流程
1. 创建批号 → 状态：待发布
2. 发布批号 → 状态：已发布（此时可被下游关联）
3. 下架批号 → 状态：已下架

### 9.2 下游企业批号流程
1. 选择上游批号创建新批号 → 状态：新建
2. 发起确认请求 → 状态：待确认
3. 上游确认 → 状态：已确认
4. 下架批号 → 状态：已下架

### 9.3 溯源码生成
- 只有零售商的已确认批号才能生成溯源码
- 溯源码采用UUID格式，全局唯一
- 消费者扫描溯源码可查看完整溯源链路

---

## 10. 错误码说明

| 错误码 | 说明         |
| ------ | ------------ |
| 200    | 操作成功     |
| 500    | 系统错误     |
| 400    | 请求参数错误 |
| 401    | 未授权       |
| 403    | 无权限       |
| 404    | 资源不存在   |

---

## 11. 注意事项

1. **数据库配置**: 请确保 `application.properties` 中的数据库连接配置正确
2. **时间格式**: 所有时间字段统一使用 `yyyy-MM-dd HH:mm:ss` 格式
3. **日期格式**: 日期字段使用 `yyyy-MM-dd` 格式
4. **跨域支持**: 已配置跨域，前端可直接调用
5. **密码加密**: 使用 BCrypt 加密，默认强度10
6. **批号规则**: 
   - 养殖：`养殖_YYYYMMDD_序号`
   - 屠宰：`屠宰_YYYYMMDD_序号`
   - 批发：`批发_YYYYMMDD_序号`
   - 零售：`零售_YYYYMMDD_序号`

---

## 12. 快速开始

### 12.1 导入数据库
```bash
mysql -u root -p < food_traceability_platform.sql
```

### 12.2 修改配置
编辑 `application.properties`，修改数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/food_traceability_platform
spring.datasource.username=root
spring.datasource.password=your_password
```

### 12.3 运行项目
```bash
mvn spring-boot:run
```

### 12.4 测试接口
默认管理员账号：
- 登录编码: `admin`
- 密码: `123456`

访问: `http://localhost:8080/api/user/login`

---

**版本**: v1.0  
**更新时间**: 2025-10-13  
**联系方式**: 开发团队
