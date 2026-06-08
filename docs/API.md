# HMIS API 接口文档

**Base URL:** `http://localhost:8080`

所有接口返回统一格式：

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

失败时：

```json
{
  "success": false,
  "data": null,
  "error": "错误信息"
}
```

---

## 目录

- [0. 认证 /api/auth](#0-认证-apiauth)
- [1. 管理端 /api/admin](#1-管理端-apiadmin)
  - [1.1 科室管理](#11-科室管理)
  - [1.2 医生管理](#12-医生管理)
  - [1.3 病人管理](#13-病人管理)
  - [1.4 药品管理](#14-药品管理)
  - [1.5 诊疗费配置](#15-诊疗费配置)
  - [1.6 病房管理](#16-病房管理)
  - [1.7 床位管理](#17-床位管理)
  - [1.8 统计查询](#18-统计查询)
  - [1.9 患者住院记录查询](#19-患者住院记录查询)
- [2. 医生端 /api/doctor](#2-医生端-apidoctor)
  - [2.1 排班查询](#21-排班查询)
  - [2.2 查询挂号记录](#22-查询医生挂号记录)
  - [2.3 门诊就诊](#23-门诊就诊)
  - [2.4 门诊开处方](#24-门诊开处方)
  - [2.5 每日查房](#25-每日查房)
  - [2.6 查询住院病人](#26-查询住院病人)
  - [2.7 查询查房用药清单](#27-查询查房用药清单)
  - [2.8 接诊历史](#28-接诊历史)
- [3. 患者端 /api/patient](#3-患者端-apipatient)
  - [3.1 查询可预约排班](#31-查询可预约排班)
  - [3.2 挂号](#32-挂号)
  - [3.3 取消挂号](#33-取消挂号)
  - [3.4 缴费](#34-缴费)
  - [3.5 查询挂号记录](#35-查询挂号记录)
  - [3.6 缴费记录查询](#36-缴费记录查询)
  - [3.7 未缴费查询](#37-未缴费查询)
  - [3.8 就诊历史](#38-就诊历史)
  - [3.9 办理入院](#39-办理入院)
  - [3.10 住院预缴金](#310-住院预缴金)
  - [3.11 住院余额查询](#311-住院余额查询)
  - [3.12 办理出院](#312-办理出院)
  - [3.13 查询住院记录](#313-查询住院记录)
  - [3.14 查询查房记录](#314-查询查房记录)
  - [3.15 查询处方用药清单](#315-查询处方用药清单)
  - [3.16 查询查房用药清单](#316-查询查房用药清单)
- [4. 数据模型](#4-数据模型)

---

## 0. 认证 `/api/auth`

### 0.1 登录

#### POST /api/auth/login — 用户登录

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | String | 是 | 用户名 |
| `password` | String | 是 | 密码 |

**示例:**
```
POST /api/auth/login?username=admin&password=admin123
```

**成功响应 data:**
```json
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN",
  "referenceId": null
}
```

**失败响应:**
```json
{
  "success": false,
  "data": null,
  "error": "用户不存在"
}
```
```json
{
  "success": false,
  "data": null,
  "error": "密码错误"
}
```

**role 可选值:** `ADMIN`（管理员） | `DOCTOR`（医生） | `PATIENT`（患者）

**referenceId 说明:** `ADMIN` 角色为 `null`；`DOCTOR` 对应 `doctor.id`；`PATIENT` 对应 `patient.id`

**测试账号:**

| 用户名 | 密码 | 角色 | referenceId |
|--------|------|------|-------------|
| admin | admin123 | ADMIN | null |
| zhangwei | 123456 | DOCTOR | 1 |
| lifang | 123456 | DOCTOR | 2 |
| wangqiang | 123456 | DOCTOR | 3 |
| zhaomin | 123456 | DOCTOR | 4 |
| zhangsan | 123456 | PATIENT | 1 |
| lisi | 123456 | PATIENT | 2 |
| wangwu | 123456 | PATIENT | 3 |
| zhaoliu | 123456 | PATIENT | 4 |

---

## 1. 管理端 `/api/admin`

### 1.1 科室管理

#### GET /api/admin/departments — 查询所有科室

**响应 data:**
```json
[
  {
    "id": 1,
    "name": "内科",
    "location": "门诊楼2层"
  }
]
```

#### POST /api/admin/departments — 新增科室

**请求体 (JSON):**
```json
{
  "name": "外科",
  "location": "门诊楼3层"
}
```

**响应 data:** `1` (影响行数)

#### PUT /api/admin/departments — 修改科室

**请求体 (JSON):**
```json
{
  "id": 1,
  "name": "内科",
  "location": "门诊楼2层A区"
}
```

**响应 data:** `1`

#### DELETE /api/admin/departments/{id} — 删除科室

**路径参数:** `id` — 科室ID

**响应 data:** `1`

---

### 1.2 医生管理

#### GET /api/admin/doctors — 查询所有医生

**响应 data:**
```json
[
  {
    "id": 1,
    "doctorNo": "D20240001",
    "name": "张三",
    "gender": "M",
    "title": "CHIEF",
    "phone": "13800000001",
    "departmentId": 1
  }
]
```

#### POST /api/admin/doctors — 新增医生

**请求体 (JSON):**
```json
{
  "doctorNo": "D20240005",
  "name": "王五",
  "gender": "M",
  "title": "VICE_CHIEF",
  "phone": "13800000005",
  "departmentId": 1
}
```

**可选 title 值:** `RESIDENT`（住院医师） | `ATTENDING`（主治医师） | `VICE_CHIEF`（副主任医师） | `CHIEF`（主任医师）

**响应 data:** `1`

#### PUT /api/admin/doctors — 修改医生

**请求体 (JSON):** 同新增，需包含 `id`

#### DELETE /api/admin/doctors/{id} — 删除医生

---

### 1.3 病人管理

#### GET /api/admin/patients — 查询所有病人

**响应 data:**
```json
[
  {
    "id": 1,
    "patientNo": "P20240001",
    "name": "李四",
    "gender": "M",
    "birthDate": "1990-01-15",
    "address": "广州市天河区",
    "phone": "13900000001"
  }
]
```

#### POST /api/admin/patients — 新增病人

**请求体 (JSON):**
```json
{
  "patientNo": "P20240005",
  "name": "赵六",
  "gender": "F",
  "birthDate": "1985-05-20",
  "address": "广州市番禺区",
  "phone": "13900000005"
}
```

**响应 data:** `1`

#### PUT /api/admin/patients — 修改病人

**请求体 (JSON):** 同新增，需包含 `id`

#### DELETE /api/admin/patients/{id} — 删除病人

---

### 1.4 药品管理

#### GET /api/admin/drugs — 查询所有药品

**响应 data:**
```json
[
  {
    "id": 1,
    "drugNo": "DRG001",
    "name": "阿莫西林胶囊",
    "specification": "250mg*24粒",
    "unit": "盒",
    "unitPrice": 12.50
  }
]
```

#### POST /api/admin/drugs — 新增药品

**请求体 (JSON):**
```json
{
  "drugNo": "DRG006",
  "name": "布洛芬缓释胶囊",
  "specification": "300mg*20粒",
  "unit": "盒",
  "unitPrice": 18.00
}
```

**响应 data:** `1`

#### PUT /api/admin/drugs — 修改药品

**请求体 (JSON):** 同新增，需包含 `id`

#### DELETE /api/admin/drugs/{id} — 删除药品

---

### 1.5 诊疗费配置

#### GET /api/admin/doctor-fees — 查询所有费用配置

**响应 data:**
```json
[
  {
    "id": 1,
    "title": "RESIDENT",
    "consultationFee": 10.00
  },
  {
    "id": 2,
    "title": "ATTENDING",
    "consultationFee": 30.00
  }
]
```

#### PUT /api/admin/doctor-fees — 修改费用配置

**请求体 (JSON):**
```json
{
  "id": 1,
  "title": "RESIDENT",
  "consultationFee": 18.00
}
```

---

### 1.6 病房管理

#### GET /api/admin/wards — 查询所有病房

**响应 data:**
```json
[
  {
    "id": 1,
    "wardNo": "W001",
    "location": "住院楼3层",
    "dailyFee": 80.00,
    "departmentId": 1
  }
]
```

#### POST /api/admin/wards — 新增病房

**请求体 (JSON):**
```json
{
  "wardNo": "W003",
  "location": "住院楼5层",
  "dailyFee": 120.00,
  "departmentId": 2
}
```

---

### 1.7 床位管理

#### GET /api/admin/beds — 查询所有床位

**响应 data:**
```json
[
  {
    "id": 1,
    "wardId": 1,
    "bedNo": "B001",
    "status": "空闲"
  },
  {
    "id": 2,
    "wardId": 1,
    "bedNo": "B002",
    "status": "占用"
  }
]
```

**status 可选值:** `AVAILABLE`（空闲） | `OCCUPIED`（占用）

#### POST /api/admin/beds — 新增床位

**请求体 (JSON):**
```json
{
  "wardId": 1,
  "bedNo": "B005",
  "status": "空闲"
}
```

---

### 1.8 统计查询

#### GET /api/admin/statistics/schedule — 科室排班统计

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `date` | String | 是 | 日期，格式 `yyyy-MM-dd` |

**示例:** `GET /api/admin/statistics/schedule?date=2026-05-25`

**响应 data:**
```json
[
  {
    "department_name": "内科",
    "total": 3,
    "outpatient_count": 2,
    "inpatient_count": 1
  }
]
```

#### GET /api/admin/statistics/workload — 医生工作量统计

**响应 data:**
```json
[
  {
    "doctor_id": 1,
    "doctor_name": "张伟",
    "title": "RESIDENT",
    "department_name": "内科",
    "total_registrations": 15,
    "total_visits": 12,
    "total_hospitalizations": 3
  }
]
```

#### GET /api/admin/statistics/patient-treatment — 病人治疗情况汇总

> 仅返回有就诊或住院记录的活跃患者。

**响应 data:**
```json
[
  {
    "patient_id": 1,
    "patient_no": "P20240001",
    "patient_name": "张三",
    "registration_count": 6,
    "visit_count": 6,
    "hospitalization_count": 2,
    "total_consultation_fee": 180.00,
    "total_drug_fee": 320.00,
    "total_cost": 500.00
  }
]
```

#### GET /api/admin/statistics/patient-treatment/detail — 患者门诊就诊明细

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 患者ID |

**响应 data:**
```json
[
  {
    "visit_time": "2026-05-25T10:30:00",
    "diagnosis": "上呼吸道感染",
    "symptoms": "发热咳嗽",
    "consultation_fee": 30.00,
    "doctor_name": "张伟",
    "doctor_title": "RESIDENT",
    "total_drug_price": 42.50,
    "total_cost": 72.50
  }
]
```

---

### 1.9 患者住院记录查询

#### GET /api/admin/patient-hospitalizations — 查询患者住院记录

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 患者ID |

**响应 data:**
```json
[
  {
    "id": 1,
    "hospitalNo": "H20240001",
    "patientId": 1,
    "attendingDoctorId": 2,
    "wardId": 1,
    "bedId": 1,
    "admissionDate": "2026-05-25",
    "dischargeDate": null,
    "status": "IN_HOSPITAL"
  }
]
```

---

## 2. 医生端 `/api/doctor`

### 2.1 排班查询

#### GET /api/doctor/schedule — 查询医生排班

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `doctorId` | Long | 是 | 医生ID |
| `date` | String | 否 | 日期，格式 `yyyy-MM-dd`；不传则查全部 |

**示例:**
```
GET /api/doctor/schedule?doctorId=1
GET /api/doctor/schedule?doctorId=1&date=2026-05-25
```

**响应 data:**
```json
[
  {
    "id": 1,
    "doctorId": 1,
    "scheduleDate": "2026-05-25",
    "timeSlot": "MORNING",
    "scheduleType": "OUTPATIENT"
  }
]
```

**timeSlot 可选值:** `MORNING`（上午） | `AFTERNOON`（下午）

**scheduleType 可选值:** `OUTPATIENT`（门诊） | `INPATIENT_ROUND`（住院查房）

---

### 2.2 查询医生挂号记录

#### GET /api/doctor/registrations — 查询医生的挂号记录

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `doctorId` | Long | 是 | 医生ID |

**响应 data:**
```json
[
  {
    "id": 2,
    "patientId": 2,
    "doctorId": 2,
    "registrationDate": "2026-05-25",
    "timeSlot": "MORNING",
    "visitType": "FIRST_VISIT",
    "status": "WAITING"
  }
]
```

---

### 2.3 门诊就诊

#### POST /api/doctor/visit — 创建就诊记录

**前置条件:** 挂号状态必须为 `WAITING`（已缴费等待就诊）

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `registrationId` | Long | 是 | 挂号ID |
| `diagnosis` | String | 是 | 诊断结果 |
| `symptoms` | String | 否 | 症状描述 |

**示例:**
```
POST /api/doctor/visit?registrationId=1&diagnosis=上呼吸道感染&symptoms=发热咳嗽
```

**响应 data:**
```json
{
  "id": 1,
  "registrationId": 1,
  "doctorId": 1,
  "patientId": 1,
  "visitTime": "2026-05-25T10:30:00",
  "diagnosis": "上呼吸道感染",
  "symptoms": "发热咳嗽",
  "consultationFee": 25.00
}
```

> 系统自动根据医生职称查找 `doctor_fee` 表设置诊疗费，并创建一笔类型为 `CONSULTATION` 的未缴费记录。

---

### 2.4 门诊开处方

#### POST /api/doctor/prescribe — 门诊开处方

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `visitId` | Long | 是 | 就诊记录ID |

**请求体 (JSON) — 药品列表:**
```json
[
  {
    "drugId": 1,
    "quantity": 2,
    "dosage": "每次1粒，每日3次"
  },
  {
    "drugId": 3,
    "quantity": 1,
    "dosage": "每次10ml，每日3次"
  }
]
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `drugId` | Long | 是 | 药品ID |
| `quantity` | Integer | 是 | 数量（正整数） |
| `dosage` | String | 是 | 用法用量 |

> - 系统自动从 `drug` 表读取 `unitPrice`，并由数据库触发器自动计算 `subtotal = unitPrice * quantity`
> - 触发器自动汇总所有明细，更新 `prescription.total_drug_price`
> - 同时创建一笔类型为 `DRUG` 的未缴费 Payment 记录

**响应 data:**
```json
{
  "id": 1,
  "visitId": 1,
  "totalDrugPrice": 37.50
}
```

---

### 2.5 每日查房

#### POST /api/doctor/hospitalization/round — 每日查房记录

**前置条件:** 住院状态必须为 `IN_HOSPITAL`

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `hospitalizationId` | Long | 是 | 住院ID |
| `conditionDesc` | String | 是 | 病情描述 |
| `treatmentPlan` | String | 是 | 治疗方案 |

**请求体 (JSON) — 可选，住院处方药品列表:**
```json
[
  {
    "drugId": 1,
    "quantity": 2,
    "dosage": "每次1粒，每日3次"
  }
]
```

可传空数组 `[]` 或不传（`null`）表示当日无药品处方。

> - 每次调用自动创建 `DailyCharge` 记录（含床位费 + 治疗费）
> - 如有药品，触发器自动计算药品费并累加到 `DailyCharge.total_fee`
> - 如有药品，同步创建住院处方及明细
> - **允许多次提交**：同一天可多次提交查房记录，每次都会新增一条记录（不覆盖）。首次提交收取床位费+诊疗费+药费，当天第二次及以后提交仅收取药费
> - **余额不足时拒绝提交**：提交前系统会核算当天费用，若账户余额不足则返回错误，记录不会保存，提示患者补缴押金

**错误响应（余额不足）:**
```json
{
  "success": false,
  "data": null,
  "error": "患者账户余额不足，请及时通知患者补缴住院押金"
}
```

**响应 data:**
```json
{
  "id": 1,
  "hospitalizationId": 1,
  "recordDate": "2026-05-25",
  "conditionDesc": "患者体温37.2°C，咳嗽减轻",
  "treatmentPlan": "继续抗生素治疗"
}
```

---

### 2.6 查询住院病人

#### GET /api/doctor/hospitalizations — 查询医生负责的住院病人

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `doctorId` | Long | 是 | 医生ID |

**响应 data:**
```json
[
  {
    "id": 1,
    "hospitalNo": "H20240001",
    "patientId": 1,
    "attendingDoctorId": 2,
    "wardId": 1,
    "bedId": 1,
    "admissionDate": "2026-05-25",
    "dischargeDate": null,
    "status": "IN_HOSPITAL"
  }
]
```

---

### 2.7 查询查房用药清单

#### GET /api/doctor/hospitalization/prescription-items — 查询某次查房的用药明细

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `recordId` | Long | 是 | 查房记录ID |

**示例:** `GET /api/doctor/hospitalization/prescription-items?recordId=1`

**响应 data:**
```json
[
  {
    "drug_name": "阿莫西林",
    "specification": "0.5g*24粒",
    "unit": "盒",
    "quantity": 2,
    "dosage": "每次1粒，每日3次",
    "unit_price": 15.00,
    "subtotal": 30.00
  }
]
```

> 该次查房无药品处方时返回空数组 `[]`

---

### 2.8 接诊历史

#### GET /api/doctor/visit-history — 查询医生接诊历史

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `doctorId` | Long | 是 | 医生ID |

**示例:** `GET /api/doctor/visit-history?doctorId=1`

**响应 data:**
```json
[
  {
    "id": 1,
    "visit_time": "2026-05-25T10:30:00",
    "diagnosis": "上呼吸道感染",
    "symptoms": "发热咳嗽",
    "consultation_fee": 30.00,
    "patient_name": "张三",
    "patient_no": "P20240001",
    "prescription_id": 1,
    "total_drug_price": 42.50,
    "total_cost": 72.50
  }
]
```

> `prescription_id` 不为 null 时可通过 `GET /api/patient/prescription-detail?prescriptionId=x` 查询用药明细。

---

## 3. 患者端 `/api/patient`

### 3.1 查询可预约排班

#### GET /api/patient/available-schedules — 查询未来可预约的门诊排班

> 返回今天及未来的门诊排班，包含医生姓名、职称、科室等信息，供患者挂号选择。

**响应 data:**
```json
[
  {
    "schedule_id": 1,
    "doctor_id": 1,
    "schedule_date": "2026-05-27",
    "time_slot": "MORNING",
    "schedule_type": "OUTPATIENT",
    "doctor_name": "张三",
    "doctor_title": "CHIEF",
    "doctor_phone": "13800000001",
    "department_name": "内科",
    "department_id": 1
  }
]
```

---

### 3.2 挂号

#### POST /api/patient/register — 患者挂号

**前置条件:** 医生在该日期该时段有排班（`schedule` 表中有 `scheduleType=OUTPATIENT` 的记录）

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 病人ID |
| `doctorId` | Long | 是 | 医生ID |
| `date` | String | 是 | 挂号日期，格式 `yyyy-MM-dd` |
| `timeSlot` | String | 是 | 时段：`MORNING`（上午）或 `AFTERNOON`（下午） |
| `visitType` | String | 是 | 就诊类型：`FIRST_VISIT`（初诊）或 `REVISIT`（复诊） |

**示例:**
```
POST /api/patient/register?patientId=1&doctorId=1&date=2026-05-25&timeSlot=MORNING&visitType=FIRST_VISIT
```

> - 系统自动创建挂号记录（状态 `WAITING`）

**响应 data:**
```json
{
  "id": 1,
  "patientId": 1,
  "doctorId": 1,
  "registrationDate": "2026-05-25",
  "timeSlot": "MORNING",
  "visitType": "FIRST_VISIT",
  "status": "WAITING"
}
```

**挂号状态流转:** `WAITING` → `VISITED`（就诊后）或 `CANCELLED`（取消）

---

### 3.3 取消挂号

#### POST /api/patient/registrations/cancel — 取消挂号

**前置条件:** 挂号状态必须为 `WAITING`（候诊中）

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `registrationId` | Long | 是 | 挂号记录ID |

**示例:**
```
POST /api/patient/registrations/cancel?registrationId=1
```

**响应 data:** `1` (影响行数)

**状态变化:** `WAITING` → `CANCELLED`

---

### 3.4 缴费

#### POST /api/patient/pay — 缴费

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `paymentId` | Long | 是 | 缴费记录ID |
| `payMethod` | String | 是 | 支付方式 |

**payMethod 可选值:** `CARD`（银行卡） | `WECHAT`（微信） | `ALIPAY`（支付宝）

**示例:**
```
POST /api/patient/pay?paymentId=1&payMethod=ALIPAY
```

> 缴费后：
> - Payment 状态更新为 `PAID`，记录支付时间和方式
> - Payment 状态更新为 `PAID`，记录支付时间和方式

**响应 data:** `1`

---

### 3.5 查询挂号记录

#### GET /api/patient/registrations — 查询患者挂号记录

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 病人ID |

**响应 data:**
```json
[
  {
    "id": 1,
    "patientId": 1,
    "doctorId": 2,
    "registrationDate": "2026-05-25",
    "timeSlot": "MORNING",
    "status": "WAITING"
  }
]
```

---

### 3.6 缴费记录查询

#### GET /api/patient/payments — 查询患者所有缴费记录

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 病人ID |

**响应 data:**
```json
[
  {
    "id": 1,
    "patientId": 1,
    "type": "REGISTRATION",
    "referenceId": 1,
    "amount": 15.00,
    "status": "PAID",
    "payTime": "2026-05-25T09:15:00",
    "payMethod": "ALIPAY"
  },
  {
    "id": 2,
    "patientId": 1,
    "type": "CONSULTATION",
    "referenceId": 1,
    "amount": 25.00,
    "status": "UNPAID",
    "payTime": null,
    "payMethod": null
  }
]
```

**type 可选值:** `CONSULTATION`（诊疗费） | `DRUG`（药费）

**status 可选值:** `UNPAID` | `PAID`

---

### 3.7 未缴费查询

#### GET /api/patient/unpaid — 查询患者未缴费项目

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 病人ID |

**响应 data:** 同缴费记录，仅返回 `status=UNPAID` 的记录

---

### 3.8 就诊历史

#### GET /api/patient/history — 查询患者门诊就诊历史

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 病人ID |

**响应 data:**
```json
[
  {
    "patient_no": "P20240001",
    "patient_name": "张三",
    "registration_date": "2026-05-25",
    "time_slot": "MORNING",
    "doctor_name": "李芳",
    "doctor_title": "ATTENDING",
    "visit_time": "2026-05-25T10:30:00",
    "diagnosis": "上呼吸道感染",
    "symptoms": "发热咳嗽",
    "consultation_fee": 30.00,
    "total_drug_price": 42.50,
    "total_cost": 87.50
  }
]
```

---

### 3.9 办理入院

#### POST /api/patient/hospitalization/admit — 办理入院

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 病人ID |
| `doctorId` | Long | 是 | 主治医生ID |
| `wardId` | Long | 是 | 病房ID |
| `bedId` | Long | 是 | 床位ID（必须属于指定病房且状态为 AVAILABLE） |

> 数据库触发器自动将床位状态更新为 `OCCUPIED`

**响应 data:**
```json
{
  "id": 1,
  "hospitalNo": "H20240001",
  "patientId": 1,
  "attendingDoctorId": 1,
  "wardId": 1,
  "bedId": 1,
  "admissionDate": "2026-05-25",
  "dischargeDate": null,
  "status": "IN_HOSPITAL"
}
```

---

### 3.10 住院预缴金

#### POST /api/patient/hospitalization/deposit — 住院预缴金

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `hospitalizationId` | Long | 是 | 住院ID |
| `amount` | BigDecimal | 是 | 缴费金额 |

**响应 data:**
```json
{
  "id": 1,
  "hospitalizationId": 1,
  "amount": 3000.00,
  "depositTime": "2026-05-25T14:00:00"
}
```

---

### 3.11 住院余额查询

#### GET /api/patient/hospitalization/balance — 查询住院费用余额

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `hospitalizationId` | Long | 是 | 住院ID |

**计算公式:** `余额 = 预缴金总额 - 每日费用总额`

**响应 data:** `1500.00`（BigDecimal，正数表示余额充足，负数表示欠费）

---

### 3.12 办理出院

#### POST /api/patient/hospitalization/discharge — 办理出院

**前置条件:** 住院状态必须为 `IN_HOSPITAL`

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `hospitalizationId` | Long | 是 | 住院ID |

> 数据库触发器自动将床位状态恢复为 `AVAILABLE`

**响应 data:**
```json
{
  "rows": 1,
  "refund": 1500.00
}
```

> `refund` 为核算后的余额退还金额。正数表示退还给患者，负数表示患者欠费，0 表示刚好结清。

---

### 3.13 查询住院记录

#### GET /api/patient/hospitalizations — 查询患者住院记录

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `patientId` | Long | 是 | 病人ID |

**响应 data:**
```json
[
  {
    "id": 1,
    "hospitalNo": "H20240001",
    "patientId": 1,
    "attendingDoctorId": 2,
    "wardId": 1,
    "bedId": 1,
    "admissionDate": "2026-05-25",
    "dischargeDate": null,
    "status": "IN_HOSPITAL"
  }
]
```

---

### 3.14 查询查房记录

#### GET /api/patient/hospitalization/records — 查询住院查房记录

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `hospitalizationId` | Long | 是 | 住院ID |

**响应 data:**
```json
[
  {
    "id": 1,
    "hospitalizationId": 1,
    "recordDate": "2026-05-25",
    "conditionDesc": "患者体温37.2°C，咳嗽减轻",
    "treatmentPlan": "继续抗生素治疗"
  }
]
```

---

### 3.15 查询处方用药清单

#### GET /api/patient/prescription-detail — 查询门诊处方用药明细

> 用于缴费记录中"查看清单"按钮，查看某次处方的药品详情。

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `prescriptionId` | Long | 是 | 处方ID（即 DRUG 类型缴费记录的 `referenceId`） |

**示例:** `GET /api/patient/prescription-detail?prescriptionId=1`

**响应 data:**
```json
[
  {
    "drug_name": "阿莫西林",
    "specification": "0.5g*24粒",
    "unit": "盒",
    "quantity": 2,
    "dosage": "每次1粒，每日3次",
    "unit_price": 15.00,
    "subtotal": 30.00
  },
  {
    "drug_name": "布洛芬",
    "specification": "0.2g*20片",
    "unit": "盒",
    "quantity": 1,
    "dosage": "每次1片，每日2次",
    "unit_price": 12.50,
    "subtotal": 12.50
  }
]
```

---

### 3.16 查询查房用药清单

#### GET /api/patient/hospitalization/prescription-items — 查询某次查房的用药明细

> 用于住院服务中查看查房记录的药品详情。

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `recordId` | Long | 是 | 查房记录ID |

**示例:** `GET /api/patient/hospitalization/prescription-items?recordId=1`

**响应 data:** 同 2.7，返回药品明细列表。该次查房无药品时返回空数组 `[]`

---

## 4. 数据模型

### 4.1 业务状态汇总

| 实体 | 字段 | 可选值 |
|------|------|--------|
| Registration | status | `WAITING`（候诊中）, `VISITED`（已就诊）, `CANCELLED`（已取消） |
| Registration | visitType | `FIRST_VISIT`（初诊）, `REVISIT`（复诊） |
| Payment | type | `CONSULTATION`（诊疗费）, `DRUG`（药费） |
| Payment | status | `UNPAID`（未缴费）, `PAID`（已缴费） |
| Payment | payMethod | `CARD`（银行卡）, `WECHAT`（微信）, `ALIPAY`（支付宝） |
| Hospitalization | status | `IN_HOSPITAL`（住院中）, `DISCHARGED`（已出院） |
| Bed | status | `AVAILABLE`（空闲）, `OCCUPIED`（占用） |
| Schedule | timeSlot | `MORNING`（上午）, `AFTERNOON`（下午） |
| Schedule | scheduleType | `OUTPATIENT`（门诊）, `INPATIENT_ROUND`（住院查房） |
| Doctor | title | `RESIDENT`（住院医师）, `ATTENDING`（主治医师）, `VICE_CHIEF`（副主任医师）, `CHIEF`（主任医师） |
| Doctor / Patient | gender | `M`（男）, `F`（女） |
| User | role | `ADMIN`, `DOCTOR`, `PATIENT` |

### 4.2 门诊业务流程

```
挂号(register) → 挂号记录(status=WAITING)
  → 就诊(visit) → 就诊记录 + Payment(type=CONSULTATION, status=UNPAID)
  → 开处方(prescribe) → 处方 + 处方明细 + Payment(type=DRUG, status=UNPAID)
  → 缴费(pay) → Payment(status=PAID)
```

### 4.3 住院业务流程

```
入院(admit) → 住院记录(status=IN_HOSPITAL), 床位自动占用(AVAILABLE→OCCUPIED)
  → 预缴(deposit) → Deposit 记录
  → 每日查房(round) → 查房记录 + DailyCharge(床位费+治疗费+药费)
  → 出院(discharge) → 住院(status=DISCHARGED), 床位自动释放(OCCUPIED→AVAILABLE)
```
