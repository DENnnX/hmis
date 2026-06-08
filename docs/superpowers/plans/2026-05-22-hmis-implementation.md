# HMIS 医院管理信息系统 — 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 基于 Spring Boot + MyBatis + MySQL 实现医院管理信息系统，涵盖门诊、住院、药品管理三大业务模块，充分展示数据库高级特性（约束、触发器、视图、索引）。

**Architecture:** 三层架构 — Controller（REST API）→ Service（业务逻辑）→ Mapper（MyBatis 数据访问）。数据库层通过 DDL 脚本初始化，包含触发器、视图、索引等高级特性。统一 API 响应格式 `ApiResponse<T>`。

**Tech Stack:** Java 17, Spring Boot 4.0.6, MyBatis 4.0.1, MySQL 8.x, Lombok

---

## 文件结构

```
src/main/java/cn/edu/scnu/hmis/
├── HmisApplication.java                    # 启动类（已有）
├── common/
│   └── ApiResponse.java                    # 统一响应格式
├── entity/                                 # 实体类（对应数据库表）
│   ├── User.java
│   ├── Department.java
│   ├── Doctor.java
│   ├── Patient.java
│   ├── Drug.java
│   ├── DoctorFee.java
│   ├── Ward.java
│   ├── Bed.java
│   ├── Schedule.java
│   ├── Registration.java
│   ├── OutpatientVisit.java
│   ├── Prescription.java
│   ├── PrescriptionItem.java
│   ├── Payment.java
│   ├── Hospitalization.java
│   ├── HospitalizationRecord.java
│   ├── InpatientPrescription.java
│   ├── InpatientPrescriptionItem.java
│   ├── DailyCharge.java
│   └── Deposit.java
├── mapper/                                 # MyBatis Mapper 接口
│   ├── UserMapper.java
│   ├── DepartmentMapper.java
│   ├── DoctorMapper.java
│   ├── PatientMapper.java
│   ├── DrugMapper.java
│   ├── DoctorFeeMapper.java
│   ├── WardMapper.java
│   ├── BedMapper.java
│   ├── ScheduleMapper.java
│   ├── RegistrationMapper.java
│   ├── OutpatientVisitMapper.java
│   ├── PrescriptionMapper.java
│   ├── PrescriptionItemMapper.java
│   ├── PaymentMapper.java
│   ├── HospitalizationMapper.java
│   ├── HospitalizationRecordMapper.java
│   ├── InpatientPrescriptionMapper.java
│   ├── InpatientPrescriptionItemMapper.java
│   ├── DailyChargeMapper.java
│   └── DepositMapper.java
├── service/                                # 业务逻辑层
│   ├── AdminService.java                   # 管理员：基础数据 CRUD
│   ├── ScheduleService.java                # 排班管理
│   ├── OutpatientService.java              # 门诊业务
│   ├── InpatientService.java               # 住院业务
│   └── StatisticsService.java              # 统计查询
└── controller/                             # REST API 控制器
    ├── AdminController.java                # 管理员接口
    ├── DoctorController.java               # 医生接口
    └── PatientController.java              # 病人接口

src/main/resources/
├── application.properties                  # 数据库配置（已有）
├── schema.sql                              # DDL 建表脚本
├── triggers.sql                            # 触发器
├── views.sql                               # 视图
├── indexes.sql                             # 索引
├── data.sql                                # 初始数据
└── mapper/                                 # MyBatis XML 映射文件
    ├── UserMapper.xml
    ├── DepartmentMapper.xml
    ├── DoctorMapper.xml
    ├── PatientMapper.xml
    ├── DrugMapper.xml
    ├── DoctorFeeMapper.xml
    ├── WardMapper.xml
    ├── BedMapper.xml
    ├── ScheduleMapper.xml
    ├── RegistrationMapper.xml
    ├── OutpatientVisitMapper.xml
    ├── PrescriptionMapper.xml
    ├── PrescriptionItemMapper.xml
    ├── PaymentMapper.xml
    ├── HospitalizationMapper.xml
    ├── HospitalizationRecordMapper.xml
    ├── InpatientPrescriptionMapper.xml
    ├── InpatientPrescriptionItemMapper.xml
    ├── DailyChargeMapper.xml
    └── DepositMapper.xml

src/test/java/cn/edu/scnu/hmis/
├── mapper/                                 # Mapper 单元测试
│   ├── DepartmentMapperTest.java
│   ├── DoctorMapperTest.java
│   ├── PatientMapperTest.java
│   └── ...
└── service/                                # Service 单元测试
    ├── AdminServiceTest.java
    ├── OutpatientServiceTest.java
    └── ...
```

---

## Phase 1: 数据库初始化

### Task 1: DDL 建表脚本

**Files:**
- Create: `src/main/resources/schema.sql`

- [ ] **Step 1: 创建 schema.sql，包含全部 20 张表的建表语句**

从设计文档 `docs/superpowers/specs/2026-05-22-hmis-database-design.md` 第 4 节复制完整 DDL，加上 `CREATE DATABASE IF NOT EXISTS hospital`。

- [ ] **Step 2: 在 MySQL 中执行 schema.sql 验证**

```bash
mysql -u root -p123456 < src/main/resources/schema.sql
```

Expected: 无报错，`SHOW TABLES;` 显示 20 张表。

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/schema.sql
git commit -m "feat: add DDL schema with 20 tables"
```

---

### Task 2: 触发器

**Files:**
- Create: `src/main/resources/triggers.sql`

- [ ] **Step 1: 创建 triggers.sql，包含全部 8 个触发器**

从设计文档第 5.1 节复制全部触发器代码：
1. `trg_schedule_conflict` — 排班冲突检测
2. `trg_prescription_item_subtotal` — 门诊处方小计自动计算
3. `trg_prescription_item_update_total` — 门诊处方总价自动更新
4. `trg_inpatient_prescription_item_subtotal` — 住院处方小计自动计算
5. `trg_inpatient_prescription_item_update_total` — 住院处方总价自动更新
6. `trg_hospitalization_bed_occupy` — 入院自动占用病床
7. `trg_hospitalization_bed_release` — 出院自动释放病床
8. `trg_check_balance` — 住院余额检查
9. `trg_daily_charge_total` — 每日费用自动计算总费用
10. `trg_registration_schedule_check` — 挂号排班校验

- [ ] **Step 2: 执行 triggers.sql 验证**

```bash
mysql -u root -p123456 hospital < src/main/resources/triggers.sql
```

Expected: 无报错。`SHOW TRIGGERS;` 显示全部触发器。

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/triggers.sql
git commit -m "feat: add database triggers for business constraints"
```

---

### Task 3: 视图和索引

**Files:**
- Create: `src/main/resources/views.sql`
- Create: `src/main/resources/indexes.sql`

- [ ] **Step 1: 创建 views.sql，包含 4 个视图**

从设计文档第 5.2 节复制：
1. `v_department_schedule` — 科室排班统计
2. `v_doctor_workload` — 医生工作量统计
3. `v_hospitalization_cost` — 住院费用汇总
4. `v_patient_outpatient_history` — 病人门诊就诊记录

- [ ] **Step 2: 创建 indexes.sql，包含 14 个索引**

从设计文档第 5.3 节复制全部索引。

- [ ] **Step 3: 执行验证**

```bash
mysql -u root -p123456 hospital < src/main/resources/views.sql
mysql -u root -p123456 hospital < src/main/resources/indexes.sql
```

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/views.sql src/main/resources/indexes.sql
git commit -m "feat: add database views and indexes"
```

---

### Task 4: 初始数据

**Files:**
- Create: `src/main/resources/data.sql`

- [ ] **Step 1: 创建 data.sql，插入测试数据**

包含：
- 1 个管理员用户
- 2 个科室（内科、外科）
- 4 个医生（不同职称各 1 个）
- 4 个病人
- 5 种药品
- 4 条费用配置（DoctorFee，每种职称一条）
- 2 间病房，各 2 张病床
- 若干排班数据

```sql
USE `hospital`;

-- 管理员
INSERT INTO `user` (username, password, role, reference_id) VALUES ('admin', 'admin123', 'ADMIN', NULL);

-- 科室
INSERT INTO `department` (name, location) VALUES ('内科', '1楼A区');
INSERT INTO `department` (name, location) VALUES ('外科', '2楼B区');

-- 医生
INSERT INTO `doctor` (doctor_no, name, gender, title, phone, department_id) VALUES ('D001', '张伟', 'M', 'RESIDENT', '13800000001', 1);
INSERT INTO `doctor` (doctor_no, name, gender, title, phone, department_id) VALUES ('D002', '李芳', 'F', 'ATTENDING', '13800000002', 1);
INSERT INTO `doctor` (doctor_no, name, gender, title, phone, department_id) VALUES ('D003', '王强', 'M', 'VICE_CHIEF', '13800000003', 2);
INSERT INTO `doctor` (doctor_no, name, gender, title, phone, department_id) VALUES ('D004', '赵敏', 'F', 'CHIEF', '13800000004', 2);

-- 医生用户账号
INSERT INTO `user` (username, password, role, reference_id) VALUES ('zhangwei', '123456', 'DOCTOR', 1);
INSERT INTO `user` (username, password, role, reference_id) VALUES ('lifang', '123456', 'DOCTOR', 2);
INSERT INTO `user` (username, password, role, reference_id) VALUES ('wangqiang', '123456', 'DOCTOR', 3);
INSERT INTO `user` (username, password, role, reference_id) VALUES ('zhaomin', '123456', 'DOCTOR', 4);

-- 费用配置
INSERT INTO `doctor_fee` (title, registration_fee, consultation_fee) VALUES ('RESIDENT', 10.00, 10.00);
INSERT INTO `doctor_fee` (title, registration_fee, consultation_fee) VALUES ('ATTENDING', 15.00, 30.00);
INSERT INTO `doctor_fee` (title, registration_fee, consultation_fee) VALUES ('VICE_CHIEF', 25.00, 50.00);
INSERT INTO `doctor_fee` (title, registration_fee, consultation_fee) VALUES ('CHIEF', 35.00, 80.00);

-- 药品
INSERT INTO `drug` (drug_no, name, specification, unit, unit_price) VALUES ('DRUG001', '阿莫西林', '0.5g*24粒', '盒', 15.00);
INSERT INTO `drug` (drug_no, name, specification, unit, unit_price) VALUES ('DRUG002', '布洛芬', '0.2g*20片', '盒', 12.50);
INSERT INTO `drug` (drug_no, name, specification, unit, unit_price) VALUES ('DRUG003', '头孢克洛', '0.25g*12粒', '盒', 28.00);
INSERT INTO `drug` (drug_no, name, specification, unit, unit_price) VALUES ('DRUG004', '复方感冒灵', '12袋', '盒', 18.00);
INSERT INTO `drug` (drug_no, name, specification, unit, unit_price) VALUES ('DRUG005', '止咳糖浆', '100ml', '瓶', 22.00);

-- 病人
INSERT INTO `patient` (patient_no, name, gender, birth_date, address, phone) VALUES ('P20240001', '张三', 'M', '1990-05-10', '广州市天河区xxx', '13900000001');
INSERT INTO `patient` (patient_no, name, gender, birth_date, address, phone) VALUES ('P20240002', '李四', 'F', '1985-08-22', '广州市越秀区xxx', '13900000002');
INSERT INTO `patient` (patient_no, name, gender, birth_date, address, phone) VALUES ('P20240003', '王五', 'M', '1978-12-01', '广州市海珠区xxx', '13900000003');
INSERT INTO `patient` (patient_no, name, gender, birth_date, address, phone) VALUES ('P20240004', '赵六', 'F', '1995-03-15', '广州市番禺区xxx', '13900000004');

-- 病人用户账号
INSERT INTO `user` (username, password, role, reference_id) VALUES ('zhangsan', '123456', 'PATIENT', 1);
INSERT INTO `user` (username, password, role, reference_id) VALUES ('lisi', '123456', 'PATIENT', 2);
INSERT INTO `user` (username, password, role, reference_id) VALUES ('wangwu', '123456', 'PATIENT', 3);
INSERT INTO `user` (username, password, role, reference_id) VALUES ('zhaoliu', '123456', 'PATIENT', 4);

-- 病房
INSERT INTO `ward` (ward_no, location, daily_fee, department_id) VALUES ('W101', '1楼A区', 80.00, 1);
INSERT INTO `ward` (ward_no, location, daily_fee, department_id) VALUES ('W201', '2楼B区', 120.00, 2);

-- 病床
INSERT INTO `bed` (ward_id, bed_no) VALUES (1, '1');
INSERT INTO `bed` (ward_id, bed_no) VALUES (1, '2');
INSERT INTO `bed` (ward_id, bed_no) VALUES (2, '1');
INSERT INTO `bed` (ward_id, bed_no) VALUES (2, '2');
```

- [ ] **Step 2: 执行验证**

```bash
mysql -u root -p123456 hospital < src/main/resources/data.sql
```

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/data.sql
git commit -m "feat: add initial test data"
```

---

## Phase 2: 实体类

### Task 5: 基础数据层实体类

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/entity/User.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Department.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Doctor.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Patient.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Drug.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/DoctorFee.java`

- [ ] **Step 1: 创建 User 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;       // ADMIN, DOCTOR, PATIENT
    private Long referenceId;  // 关联医生ID或病人ID
}
```

- [ ] **Step 2: 创建 Department 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;

@Data
public class Department {
    private Long id;
    private String name;
    private String location;
}
```

- [ ] **Step 3: 创建 Doctor 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;

@Data
public class Doctor {
    private Long id;
    private String doctorNo;
    private String name;
    private String gender;       // M, F
    private String title;        // RESIDENT, ATTENDING, VICE_CHIEF, CHIEF
    private String phone;
    private Long departmentId;
}
```

- [ ] **Step 4: 创建 Patient 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Patient {
    private Long id;
    private String patientNo;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String address;
    private String phone;
}
```

- [ ] **Step 5: 创建 Drug 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Drug {
    private Long id;
    private String drugNo;
    private String name;
    private String specification;
    private String unit;
    private BigDecimal unitPrice;
}
```

- [ ] **Step 6: 创建 DoctorFee 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DoctorFee {
    private Long id;
    private String title;
    private BigDecimal registrationFee;
    private BigDecimal consultationFee;
}
```

- [ ] **Step 7: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/entity/User.java \
        src/main/java/cn/edu/scnu/hmis/entity/Department.java \
        src/main/java/cn/edu/scnu/hmis/entity/Doctor.java \
        src/main/java/cn/edu/scnu/hmis/entity/Patient.java \
        src/main/java/cn/edu/scnu/hmis/entity/Drug.java \
        src/main/java/cn/edu/scnu/hmis/entity/DoctorFee.java
git commit -m "feat: add basic entity classes"
```

---

### Task 6: 门诊业务层实体类

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Schedule.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Registration.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/OutpatientVisit.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Prescription.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/PrescriptionItem.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Payment.java`

- [ ] **Step 1: 创建 Schedule 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Schedule {
    private Long id;
    private Long doctorId;
    private LocalDate scheduleDate;
    private String timeSlot;       // MORNING, AFTERNOON, EVENING
    private String scheduleType;   // OUTPATIENT, INPATIENT_ROUND
}
```

- [ ] **Step 2: 创建 Registration 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Registration {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDate registrationDate;
    private String timeSlot;
    private BigDecimal registrationFee;
    private String status;  // PENDING_PAYMENT, WAITING, VISITED, CANCELLED
}
```

- [ ] **Step 3: 创建 OutpatientVisit 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OutpatientVisit {
    private Long id;
    private Long registrationId;
    private Long doctorId;
    private Long patientId;
    private LocalDateTime visitTime;
    private String diagnosis;
    private String symptoms;
    private BigDecimal consultationFee;
}
```

- [ ] **Step 4: 创建 Prescription 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Prescription {
    private Long id;
    private Long visitId;
    private BigDecimal totalDrugPrice;
}
```

- [ ] **Step 5: 创建 PrescriptionItem 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PrescriptionItem {
    private Long id;
    private Long prescriptionId;
    private Long drugId;
    private Integer quantity;
    private String dosage;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
```

- [ ] **Step 6: 创建 Payment 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Long id;
    private Long patientId;
    private String type;          // REGISTRATION, CONSULTATION, DRUG
    private Long referenceId;
    private BigDecimal amount;
    private String status;        // UNPAID, PAID
    private LocalDateTime payTime;
    private String payMethod;     // ALIPAY, WECHAT, CARD
}
```

- [ ] **Step 7: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/entity/Schedule.java \
        src/main/java/cn/edu/scnu/hmis/entity/Registration.java \
        src/main/java/cn/edu/scnu/hmis/entity/OutpatientVisit.java \
        src/main/java/cn/edu/scnu/hmis/entity/Prescription.java \
        src/main/java/cn/edu/scnu/hmis/entity/PrescriptionItem.java \
        src/main/java/cn/edu/scnu/hmis/entity/Payment.java
git commit -m "feat: add outpatient entity classes"
```

---

### Task 7: 住院业务层实体类

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Ward.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Bed.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Hospitalization.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/HospitalizationRecord.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/InpatientPrescription.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/InpatientPrescriptionItem.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/DailyCharge.java`
- Create: `src/main/java/cn/edu/scnu/hmis/entity/Deposit.java`

- [ ] **Step 1: 创建 Ward 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Ward {
    private Long id;
    private String wardNo;
    private String location;
    private BigDecimal dailyFee;
    private Long departmentId;
}
```

- [ ] **Step 2: 创建 Bed 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;

@Data
public class Bed {
    private Long id;
    private Long wardId;
    private String bedNo;
    private String status;  // AVAILABLE, OCCUPIED
}
```

- [ ] **Step 3: 创建 Hospitalization 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Hospitalization {
    private Long id;
    private String hospitalNo;
    private Long patientId;
    private Long attendingDoctorId;
    private Long wardId;
    private Long bedId;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String status;  // IN_HOSPITAL, DISCHARGED
}
```

- [ ] **Step 4: 创建 HospitalizationRecord 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HospitalizationRecord {
    private Long id;
    private Long hospitalizationId;
    private LocalDate recordDate;
    private String conditionDesc;
    private String treatmentPlan;
}
```

- [ ] **Step 5: 创建 InpatientPrescription 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InpatientPrescription {
    private Long id;
    private Long recordId;
    private BigDecimal totalDrugPrice;
}
```

- [ ] **Step 6: 创建 InpatientPrescriptionItem 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InpatientPrescriptionItem {
    private Long id;
    private Long prescriptionId;
    private Long drugId;
    private Integer quantity;
    private String dosage;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
```

- [ ] **Step 7: 创建 DailyCharge 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyCharge {
    private Long id;
    private Long hospitalizationId;
    private LocalDate chargeDate;
    private BigDecimal bedFee;
    private BigDecimal drugFee;
    private BigDecimal treatmentFee;
    private BigDecimal totalFee;
}
```

- [ ] **Step 8: 创建 Deposit 实体**

```java
package cn.edu.scnu.hmis.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Deposit {
    private Long id;
    private Long hospitalizationId;
    private BigDecimal amount;
    private LocalDateTime depositTime;
}
```

- [ ] **Step 9: 创建统一响应类 ApiResponse**

```java
package cn.edu.scnu.hmis.common;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String error;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(true);
        resp.setData(data);
        return resp;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(false);
        resp.setError(message);
        return resp;
    }
}
```

- [ ] **Step 10: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/entity/Ward.java \
        src/main/java/cn/edu/scnu/hmis/entity/Bed.java \
        src/main/java/cn/edu/scnu/hmis/entity/Hospitalization.java \
        src/main/java/cn/edu/scnu/hmis/entity/HospitalizationRecord.java \
        src/main/java/cn/edu/scnu/hmis/entity/InpatientPrescription.java \
        src/main/java/cn/edu/scnu/hmis/entity/InpatientPrescriptionItem.java \
        src/main/java/cn/edu/scnu/hmis/entity/DailyCharge.java \
        src/main/java/cn/edu/scnu/hmis/entity/Deposit.java \
        src/main/java/cn/edu/scnu/hmis/common/ApiResponse.java
git commit -m "feat: add inpatient entity classes and ApiResponse"
```

---

## Phase 3: Mapper 层

### Task 8: 基础数据层 Mapper（User、Department、Doctor、Patient、Drug、DoctorFee）

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/UserMapper.java`
- Create: `src/main/resources/mapper/UserMapper.xml`
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/DepartmentMapper.java`
- Create: `src/main/resources/mapper/DepartmentMapper.xml`
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/DoctorMapper.java`
- Create: `src/main/resources/mapper/DoctorMapper.xml`
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/PatientMapper.java`
- Create: `src/main/resources/mapper/PatientMapper.xml`
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/DrugMapper.java`
- Create: `src/main/resources/mapper/DrugMapper.xml`
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/DoctorFeeMapper.java`
- Create: `src/main/resources/mapper/DoctorFeeMapper.xml`

以 DepartmentMapper 为示例，其余同理。

- [ ] **Step 1: 创建 DepartmentMapper 接口**

```java
package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<Department> findAll();
    Department findById(@Param("id") Long id);
    int insert(Department department);
    int update(Department department);
    int deleteById(@Param("id") Long id);
}
```

- [ ] **Step 2: 创建 DepartmentMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.edu.scnu.hmis.mapper.DepartmentMapper">

    <select id="findAll" resultType="cn.edu.scnu.hmis.entity.Department">
        SELECT id, name, location FROM department
    </select>

    <select id="findById" resultType="cn.edu.scnu.hmis.entity.Department">
        SELECT id, name, location FROM department WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO department (name, location) VALUES (#{name}, #{location})
    </insert>

    <update id="update">
        UPDATE department SET name = #{name}, location = #{location} WHERE id = #{id}
    </update>

    <delete id="deleteById">
        DELETE FROM department WHERE id = #{id}
    </delete>

</mapper>
```

- [ ] **Step 3: 按相同模式创建其余 5 个 Mapper（User、Doctor、Patient、Drug、DoctorFee）**

每个 Mapper 接口包含 findAll、findById、insert、update、deleteById 方法。
XML 文件包含对应 SQL。DoctorMapper 额外提供 findByDepartmentId。UserMapper 额外提供 findByUsername。

- [ ] **Step 4: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/mapper/ \
        src/main/resources/mapper/
git commit -m "feat: add basic data layer mappers"
```

---

### Task 9: 门诊业务层 Mapper

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/ScheduleMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/RegistrationMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/OutpatientVisitMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/PrescriptionMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/PrescriptionItemMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/PaymentMapper.java` + XML

- [ ] **Step 1: 创建 ScheduleMapper**

接口额外提供 `findByDoctorAndDate`、`findByDate` 方法。

- [ ] **Step 2: 创建 RegistrationMapper**

接口额外提供 `findByPatientId`、`findByDoctorAndDate`、`updateStatus` 方法。

- [ ] **Step 3: 创建 OutpatientVisitMapper**

接口额外提供 `findByRegistrationId`、`findByPatientId` 方法。

- [ ] **Step 4: 创建 PrescriptionMapper + PrescriptionItemMapper**

PrescriptionMapper: findByVisitId
PrescriptionItemMapper: findByPrescriptionId、batchInsert

- [ ] **Step 5: 创建 PaymentMapper**

接口额外提供 `findByPatientId`、`findUnpaidByPatientId`、`updateStatus` 方法。

- [ ] **Step 6: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/mapper/ScheduleMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/RegistrationMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/OutpatientVisitMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/PrescriptionMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/PrescriptionItemMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/PaymentMapper.java \
        src/main/resources/mapper/ScheduleMapper.xml \
        src/main/resources/mapper/RegistrationMapper.xml \
        src/main/resources/mapper/OutpatientVisitMapper.xml \
        src/main/resources/mapper/PrescriptionMapper.xml \
        src/main/resources/mapper/PrescriptionItemMapper.xml \
        src/main/resources/mapper/PaymentMapper.xml
git commit -m "feat: add outpatient layer mappers"
```

---

### Task 10: 住院业务层 Mapper

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/WardMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/BedMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/HospitalizationMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/HospitalizationRecordMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/InpatientPrescriptionMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/InpatientPrescriptionItemMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/DailyChargeMapper.java` + XML
- Create: `src/main/java/cn/edu/scnu/hmis/mapper/DepositMapper.java` + XML

- [ ] **Step 1: 创建 WardMapper + BedMapper**

BedMapper 额外提供 `findAvailableByWardId`。

- [ ] **Step 2: 创建 HospitalizationMapper + HospitalizationRecordMapper**

HospitalizationMapper 额外提供 `findByPatientId`、`findActive`、`updateStatus`。
HospitalizationRecordMapper 额外提供 `findByHospitalizationId`。

- [ ] **Step 3: 创建 InpatientPrescriptionMapper + InpatientPrescriptionItemMapper**

- [ ] **Step 4: 创建 DailyChargeMapper + DepositMapper**

DailyChargeMapper 额外提供 `findByHospitalizationId`、`sumByHospitalizationId`。
DepositMapper 额外提供 `findByHospitalizationId`、`sumByHospitalizationId`。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/mapper/WardMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/BedMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/HospitalizationMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/HospitalizationRecordMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/InpatientPrescriptionMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/InpatientPrescriptionItemMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/DailyChargeMapper.java \
        src/main/java/cn/edu/scnu/hmis/mapper/DepositMapper.java \
        src/main/resources/mapper/WardMapper.xml \
        src/main/resources/mapper/BedMapper.xml \
        src/main/resources/mapper/HospitalizationMapper.xml \
        src/main/resources/mapper/HospitalizationRecordMapper.xml \
        src/main/resources/mapper/InpatientPrescriptionMapper.xml \
        src/main/resources/mapper/InpatientPrescriptionItemMapper.xml \
        src/main/resources/mapper/DailyChargeMapper.xml \
        src/main/resources/mapper/DepositMapper.xml
git commit -m "feat: add inpatient layer mappers"
```

---

## Phase 4: Service 层

### Task 11: AdminService — 基础数据管理

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/service/AdminService.java`

- [ ] **Step 1: 创建 AdminService**

```java
package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.*;
import cn.edu.scnu.hmis.mapper.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    private final DepartmentMapper departmentMapper;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final DrugMapper drugMapper;
    private final DoctorFeeMapper doctorFeeMapper;
    private final WardMapper wardMapper;
    private final BedMapper bedMapper;

    public AdminService(DepartmentMapper departmentMapper, DoctorMapper doctorMapper,
                        PatientMapper patientMapper, DrugMapper drugMapper,
                        DoctorFeeMapper doctorFeeMapper, WardMapper wardMapper,
                        BedMapper bedMapper) {
        this.departmentMapper = departmentMapper;
        this.doctorMapper = doctorMapper;
        this.patientMapper = patientMapper;
        this.drugMapper = drugMapper;
        this.doctorFeeMapper = doctorFeeMapper;
        this.wardMapper = wardMapper;
        this.bedMapper = bedMapper;
    }

    // 科室 CRUD
    public List<Department> listDepartments() { return departmentMapper.findAll(); }
    public Department getDepartment(Long id) { return departmentMapper.findById(id); }
    public int addDepartment(Department d) { return departmentMapper.insert(d); }
    public int updateDepartment(Department d) { return departmentMapper.update(d); }
    public int deleteDepartment(Long id) { return departmentMapper.deleteById(id); }

    // 医生 CRUD
    public List<Doctor> listDoctors() { return doctorMapper.findAll(); }
    public Doctor getDoctor(Long id) { return doctorMapper.findById(id); }
    public int addDoctor(Doctor d) { return doctorMapper.insert(d); }
    public int updateDoctor(Doctor d) { return doctorMapper.update(d); }
    public int deleteDoctor(Long id) { return doctorMapper.deleteById(id); }

    // 药品 CRUD
    public List<Drug> listDrugs() { return drugMapper.findAll(); }
    public Drug getDrug(Long id) { return drugMapper.findById(id); }
    public int addDrug(Drug d) { return drugMapper.insert(d); }
    public int updateDrug(Drug d) { return drugMapper.update(d); }
    public int deleteDrug(Long id) { return drugMapper.deleteById(id); }

    // 费用配置 CRUD
    public List<DoctorFee> listDoctorFees() { return doctorFeeMapper.findAll(); }
    public int updateDoctorFee(DoctorFee f) { return doctorFeeMapper.update(f); }

    // 病房/病床 CRUD
    public List<Ward> listWards() { return wardMapper.findAll(); }
    public int addWard(Ward w) { return wardMapper.insert(w); }
    public List<Bed> listBeds() { return bedMapper.findAll(); }
    public int addBed(Bed b) { return bedMapper.insert(b); }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/service/AdminService.java
git commit -m "feat: add AdminService for basic data CRUD"
```

---

### Task 12: ScheduleService — 排班管理

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/service/ScheduleService.java`

- [ ] **Step 1: 创建 ScheduleService**

```java
package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.Schedule;
import cn.edu.scnu.hmis.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;

    public ScheduleService(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    public List<Schedule> listAll() { return scheduleMapper.findAll(); }

    public List<Schedule> findByDate(LocalDate date) {
        return scheduleMapper.findByDate(date);
    }

    public List<Schedule> findByDoctorAndDate(Long doctorId, LocalDate date) {
        return scheduleMapper.findByDoctorAndDate(doctorId, date);
    }

    public int addSchedule(Schedule s) {
        // 触发器会在数据库层检测冲突
        return scheduleMapper.insert(s);
    }

    public int deleteSchedule(Long id) { return scheduleMapper.deleteById(id); }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/service/ScheduleService.java
git commit -m "feat: add ScheduleService"
```

---

### Task 13: OutpatientService — 门诊业务

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/service/OutpatientService.java`

- [ ] **Step 1: 创建 OutpatientService**

```java
package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.*;
import cn.edu.scnu.hmis.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutpatientService {

    private final RegistrationMapper registrationMapper;
    private final OutpatientVisitMapper outpatientVisitMapper;
    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionItemMapper prescriptionItemMapper;
    private final PaymentMapper paymentMapper;
    private final DoctorFeeMapper doctorFeeMapper;
    private final DoctorMapper doctorMapper;

    public OutpatientService(RegistrationMapper registrationMapper,
                             OutpatientVisitMapper outpatientVisitMapper,
                             PrescriptionMapper prescriptionMapper,
                             PrescriptionItemMapper prescriptionItemMapper,
                             PaymentMapper paymentMapper,
                             DoctorFeeMapper doctorFeeMapper,
                             DoctorMapper doctorMapper) {
        this.registrationMapper = registrationMapper;
        this.outpatientVisitMapper = outpatientVisitMapper;
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionItemMapper = prescriptionItemMapper;
        this.paymentMapper = paymentMapper;
        this.doctorFeeMapper = doctorFeeMapper;
        this.doctorMapper = doctorMapper;
    }

    @Transactional
    public Registration register(Long patientId, Long doctorId,
                                 String date, String timeSlot) {
        Doctor doctor = doctorMapper.findById(doctorId);
        DoctorFee fee = doctorFeeMapper.findByTitle(doctor.getTitle());

        Registration r = new Registration();
        r.setPatientId(patientId);
        r.setDoctorId(doctorId);
        r.setRegistrationDate(java.time.LocalDate.parse(date));
        r.setTimeSlot(timeSlot);
        r.setRegistrationFee(fee.getRegistrationFee());
        r.setStatus("PENDING_PAYMENT");
        registrationMapper.insert(r);

        Payment p = new Payment();
        p.setPatientId(patientId);
        p.setType("REGISTRATION");
        p.setReferenceId(r.getId());
        p.setAmount(fee.getRegistrationFee());
        p.setStatus("UNPAID");
        paymentMapper.insert(p);

        return r;
    }

    @Transactional
    public int pay(Long paymentId, String payMethod) {
        return paymentMapper.updateStatus(paymentId, "PAID",
                LocalDateTime.now(), payMethod);
    }

    @Transactional
    public OutpatientVisit visit(Long registrationId, String diagnosis, String symptoms) {
        Registration r = registrationMapper.findById(registrationId);
        Doctor doctor = doctorMapper.findById(r.getDoctorId());
        DoctorFee fee = doctorFeeMapper.findByTitle(doctor.getTitle());

        OutpatientVisit ov = new OutpatientVisit();
        ov.setRegistrationId(registrationId);
        ov.setDoctorId(r.getDoctorId());
        ov.setPatientId(r.getPatientId());
        ov.setVisitTime(LocalDateTime.now());
        ov.setDiagnosis(diagnosis);
        ov.setSymptoms(symptoms);
        ov.setConsultationFee(fee.getConsultationFee());
        outpatientVisitMapper.insert(ov);

        registrationMapper.updateStatus(registrationId, "VISITED");

        Payment p = new Payment();
        p.setPatientId(r.getPatientId());
        p.setType("CONSULTATION");
        p.setReferenceId(ov.getId());
        p.setAmount(fee.getConsultationFee());
        p.setStatus("UNPAID");
        paymentMapper.insert(p);

        return ov;
    }

    @Transactional
    public Prescription prescribe(Long visitId, List<PrescriptionItem> items) {
        Prescription pr = new Prescription();
        pr.setVisitId(visitId);
        pr.setTotalDrugPrice(BigDecimal.ZERO);
        prescriptionMapper.insert(pr);

        for (PrescriptionItem item : items) {
            item.setPrescriptionId(pr.getId());
            // subtotal 由触发器自动计算
            prescriptionItemMapper.insert(item);
        }

        // 创建药费账单
        OutpatientVisit ov = outpatientVisitMapper.findById(visitId);
        Payment p = new Payment();
        p.setPatientId(ov.getPatientId());
        p.setType("DRUG");
        p.setReferenceId(pr.getId());
        p.setAmount(pr.getTotalDrugPrice()); // 触发器已更新总价
        p.setStatus("UNPAID");
        paymentMapper.insert(p);

        return pr;
    }

    public List<Payment> getPayments(Long patientId) {
        return paymentMapper.findByPatientId(patientId);
    }

    public List<Payment> getUnpaidPayments(Long patientId) {
        return paymentMapper.findUnpaidByPatientId(patientId);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/service/OutpatientService.java
git commit -m "feat: add OutpatientService for clinic workflow"
```

---

### Task 14: InpatientService — 住院业务

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/service/InpatientService.java`

- [ ] **Step 1: 创建 InpatientService**

```java
package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.*;
import cn.edu.scnu.hmis.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InpatientService {

    private final HospitalizationMapper hospitalizationMapper;
    private final HospitalizationRecordMapper recordMapper;
    private final InpatientPrescriptionMapper prescriptionMapper;
    private final InpatientPrescriptionItemMapper itemMapper;
    private final DailyChargeMapper dailyChargeMapper;
    private final DepositMapper depositMapper;
    private final DoctorFeeMapper doctorFeeMapper;
    private final DoctorMapper doctorMapper;
    private final WardMapper wardMapper;

    public InpatientService(HospitalizationMapper hospitalizationMapper,
                            HospitalizationRecordMapper recordMapper,
                            InpatientPrescriptionMapper prescriptionMapper,
                            InpatientPrescriptionItemMapper itemMapper,
                            DailyChargeMapper dailyChargeMapper,
                            DepositMapper depositMapper,
                            DoctorFeeMapper doctorFeeMapper,
                            DoctorMapper doctorMapper,
                            WardMapper wardMapper) {
        this.hospitalizationMapper = hospitalizationMapper;
        this.recordMapper = recordMapper;
        this.prescriptionMapper = prescriptionMapper;
        this.itemMapper = itemMapper;
        this.dailyChargeMapper = dailyChargeMapper;
        this.depositMapper = depositMapper;
        this.doctorFeeMapper = doctorFeeMapper;
        this.doctorMapper = doctorMapper;
        this.wardMapper = wardMapper;
    }

    @Transactional
    public Hospitalization admit(Long patientId, Long doctorId,
                                 Long wardId, Long bedId) {
        Hospitalization h = new Hospitalization();
        h.setHospitalNo("H" + System.currentTimeMillis());
        h.setPatientId(patientId);
        h.setAttendingDoctorId(doctorId);
        h.setWardId(wardId);
        h.setBedId(bedId);
        h.setAdmissionDate(LocalDate.now());
        h.setStatus("IN_HOSPITAL");
        hospitalizationMapper.insert(h);
        // 触发器自动占用病床
        return h;
    }

    @Transactional
    public Deposit deposit(Long hospitalizationId, BigDecimal amount) {
        Deposit d = new Deposit();
        d.setHospitalizationId(hospitalizationId);
        d.setAmount(amount);
        d.setDepositTime(LocalDateTime.now());
        depositMapper.insert(d);
        return d;
    }

    @Transactional
    public HospitalizationRecord dailyRound(Long hospitalizationId,
                                            String conditionDesc,
                                            String treatmentPlan,
                                            List<InpatientPrescriptionItem> items) {
        HospitalizationRecord record = new HospitalizationRecord();
        record.setHospitalizationId(hospitalizationId);
        record.setRecordDate(LocalDate.now());
        record.setConditionDesc(conditionDesc);
        record.setTreatmentPlan(treatmentPlan);
        recordMapper.insert(record);

        if (items != null && !items.isEmpty()) {
            InpatientPrescription pr = new InpatientPrescription();
            pr.setRecordId(record.getId());
            pr.setTotalDrugPrice(BigDecimal.ZERO);
            prescriptionMapper.insert(pr);

            for (InpatientPrescriptionItem item : items) {
                item.setPrescriptionId(pr.getId());
                itemMapper.insert(item);
            }

            Hospitalization h = hospitalizationMapper.findById(hospitalizationId);
            Doctor doctor = doctorMapper.findById(h.getAttendingDoctorId());
            DoctorFee fee = doctorFeeMapper.findByTitle(doctor.getTitle());
            Ward ward = wardMapper.findById(h.getWardId());

            DailyCharge dc = new DailyCharge();
            dc.setHospitalizationId(hospitalizationId);
            dc.setChargeDate(LocalDate.now());
            dc.setBedFee(ward.getDailyFee());
            dc.setDrugFee(pr.getTotalDrugPrice());
            dc.setTreatmentFee(fee.getConsultationFee());
            // totalFee 由触发器自动计算
            dailyChargeMapper.insert(dc);
        }

        return record;
    }

    @Transactional
    public int discharge(Long hospitalizationId) {
        Hospitalization h = hospitalizationMapper.findById(hospitalizationId);
        h.setDischargeDate(LocalDate.now());
        h.setStatus("DISCHARGED");
        // 触发器自动释放病床
        return hospitalizationMapper.update(h);
    }

    public BigDecimal getBalance(Long hospitalizationId) {
        BigDecimal deposit = depositMapper.sumByHospitalizationId(hospitalizationId);
        BigDecimal charge = dailyChargeMapper.sumByHospitalizationId(hospitalizationId);
        if (deposit == null) deposit = BigDecimal.ZERO;
        if (charge == null) charge = BigDecimal.ZERO;
        return deposit.subtract(charge);
    }

    public List<HospitalizationRecord> getRecords(Long hospitalizationId) {
        return recordMapper.findByHospitalizationId(hospitalizationId);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/service/InpatientService.java
git commit -m "feat: add InpatientService for hospitalization workflow"
```

---

### Task 15: StatisticsService — 统计查询

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/service/StatisticsService.java`

- [ ] **Step 1: 创建 StatisticsService**

```java
package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.mapper.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final ScheduleMapper scheduleMapper;
    private final DoctorMapper doctorMapper;
    private final RegistrationMapper registrationMapper;
    private final HospitalizationMapper hospitalizationMapper;

    public StatisticsService(ScheduleMapper scheduleMapper,
                             DoctorMapper doctorMapper,
                             RegistrationMapper registrationMapper,
                             HospitalizationMapper hospitalizationMapper) {
        this.scheduleMapper = scheduleMapper;
        this.doctorMapper = doctorMapper;
        this.registrationMapper = registrationMapper;
        this.hospitalizationMapper = hospitalizationMapper;
    }

    public List<Map<String, Object>> departmentSchedule(String date) {
        // 查询 v_department_schedule 视图
        return scheduleMapper.findDepartmentSchedule(date);
    }

    public List<Map<String, Object>> doctorWorkload() {
        // 查询 v_doctor_workload 视图
        return doctorMapper.findWorkload();
    }

    public List<Map<String, Object>> hospitalizationCost() {
        // 查询 v_hospitalization_cost 视图
        return hospitalizationMapper.findCostSummary();
    }

    public List<Map<String, Object>> patientHistory(Long patientId) {
        // 查询 v_patient_outpatient_history 视图
        return registrationMapper.findPatientHistory(patientId);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/service/StatisticsService.java
git commit -m "feat: add StatisticsService for reporting views"
```

---

## Phase 5: Controller 层

### Task 16: AdminController — 管理员接口

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/controller/AdminController.java`

- [ ] **Step 1: 创建 AdminController**

REST API 端点：
- `GET/POST/PUT/DELETE /api/admin/departments`
- `GET/POST/PUT/DELETE /api/admin/doctors`
- `GET/POST/PUT/DELETE /api/admin/drugs`
- `GET/PUT /api/admin/doctor-fees`
- `GET/POST /api/admin/wards`
- `GET/POST /api/admin/beds`
- `GET /api/admin/statistics/schedule`
- `GET /api/admin/statistics/workload`
- `GET /api/admin/statistics/hospitalization-cost`

```java
package cn.edu.scnu.hmis.controller;

import cn.edu.scnu.hmis.common.ApiResponse;
import cn.edu.scnu.hmis.entity.*;
import cn.edu.scnu.hmis.service.AdminService;
import cn.edu.scnu.hmis.service.StatisticsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final StatisticsService statisticsService;

    public AdminController(AdminService adminService, StatisticsService statisticsService) {
        this.adminService = adminService;
        this.statisticsService = statisticsService;
    }

    // 科室
    @GetMapping("/departments")
    public ApiResponse<List<Department>> listDepartments() {
        return ApiResponse.ok(adminService.listDepartments());
    }

    @PostMapping("/departments")
    public ApiResponse<Integer> addDepartment(@RequestBody Department d) {
        return ApiResponse.ok(adminService.addDepartment(d));
    }

    @PutMapping("/departments")
    public ApiResponse<Integer> updateDepartment(@RequestBody Department d) {
        return ApiResponse.ok(adminService.updateDepartment(d));
    }

    @DeleteMapping("/departments/{id}")
    public ApiResponse<Integer> deleteDepartment(@PathVariable Long id) {
        return ApiResponse.ok(adminService.deleteDepartment(id));
    }

    // 医生
    @GetMapping("/doctors")
    public ApiResponse<List<Doctor>> listDoctors() {
        return ApiResponse.ok(adminService.listDoctors());
    }

    @PostMapping("/doctors")
    public ApiResponse<Integer> addDoctor(@RequestBody Doctor d) {
        return ApiResponse.ok(adminService.addDoctor(d));
    }

    @PutMapping("/doctors")
    public ApiResponse<Integer> updateDoctor(@RequestBody Doctor d) {
        return ApiResponse.ok(adminService.updateDoctor(d));
    }

    @DeleteMapping("/doctors/{id}")
    public ApiResponse<Integer> deleteDoctor(@PathVariable Long id) {
        return ApiResponse.ok(adminService.deleteDoctor(id));
    }

    // 药品
    @GetMapping("/drugs")
    public ApiResponse<List<Drug>> listDrugs() {
        return ApiResponse.ok(adminService.listDrugs());
    }

    @PostMapping("/drugs")
    public ApiResponse<Integer> addDrug(@RequestBody Drug d) {
        return ApiResponse.ok(adminService.addDrug(d));
    }

    @PutMapping("/drugs")
    public ApiResponse<Integer> updateDrug(@RequestBody Drug d) {
        return ApiResponse.ok(adminService.updateDrug(d));
    }

    @DeleteMapping("/drugs/{id}")
    public ApiResponse<Integer> deleteDrug(@PathVariable Long id) {
        return ApiResponse.ok(adminService.deleteDrug(id));
    }

    // 费用配置
    @GetMapping("/doctor-fees")
    public ApiResponse<List<DoctorFee>> listDoctorFees() {
        return ApiResponse.ok(adminService.listDoctorFees());
    }

    @PutMapping("/doctor-fees")
    public ApiResponse<Integer> updateDoctorFee(@RequestBody DoctorFee f) {
        return ApiResponse.ok(adminService.updateDoctorFee(f));
    }

    // 统计
    @GetMapping("/statistics/schedule")
    public ApiResponse<?> departmentSchedule(@RequestParam String date) {
        return ApiResponse.ok(statisticsService.departmentSchedule(date));
    }

    @GetMapping("/statistics/workload")
    public ApiResponse<?> doctorWorkload() {
        return ApiResponse.ok(statisticsService.doctorWorkload());
    }

    @GetMapping("/statistics/hospitalization-cost")
    public ApiResponse<?> hospitalizationCost() {
        return ApiResponse.ok(statisticsService.hospitalizationCost());
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/controller/AdminController.java
git commit -m "feat: add AdminController for admin REST API"
```

---

### Task 17: DoctorController — 医生接口

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/controller/DoctorController.java`

- [ ] **Step 1: 创建 DoctorController**

REST API 端点：
- `GET /api/doctor/schedule` — 查看自己的排班
- `POST /api/doctor/visit` — 接诊（创建就诊记录）
- `POST /api/doctor/prescribe` — 开处方
- `POST /api/doctor/hospitalization/admit` — 办理住院
- `POST /api/doctor/hospitalization/round` — 每日查房
- `POST /api/doctor/hospitalization/discharge` — 出院

```java
package cn.edu.scnu.hmis.controller;

import cn.edu.scnu.hmis.common.ApiResponse;
import cn.edu.scnu.hmis.entity.*;
import cn.edu.scnu.hmis.service.InpatientService;
import cn.edu.scnu.hmis.service.OutpatientService;
import cn.edu.scnu.hmis.service.ScheduleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final ScheduleService scheduleService;
    private final OutpatientService outpatientService;
    private final InpatientService inpatientService;

    public DoctorController(ScheduleService scheduleService,
                            OutpatientService outpatientService,
                            InpatientService inpatientService) {
        this.scheduleService = scheduleService;
        this.outpatientService = outpatientService;
        this.inpatientService = inpatientService;
    }

    @GetMapping("/schedule")
    public ApiResponse<List<Schedule>> mySchedule(
            @RequestParam Long doctorId,
            @RequestParam(required = false) String date) {
        if (date != null) {
            return ApiResponse.ok(scheduleService.findByDoctorAndDate(
                    doctorId, java.time.LocalDate.parse(date)));
        }
        return ApiResponse.ok(scheduleService.listAll());
    }

    @PostMapping("/visit")
    public ApiResponse<OutpatientVisit> visit(
            @RequestParam Long registrationId,
            @RequestParam String diagnosis,
            @RequestParam(required = false) String symptoms) {
        return ApiResponse.ok(outpatientService.visit(registrationId, diagnosis, symptoms));
    }

    @PostMapping("/prescribe")
    public ApiResponse<Prescription> prescribe(
            @RequestParam Long visitId,
            @RequestBody List<PrescriptionItem> items) {
        return ApiResponse.ok(outpatientService.prescribe(visitId, items));
    }

    @PostMapping("/hospitalization/admit")
    public ApiResponse<Hospitalization> admit(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam Long wardId,
            @RequestParam Long bedId) {
        return ApiResponse.ok(inpatientService.admit(patientId, doctorId, wardId, bedId));
    }

    @PostMapping("/hospitalization/round")
    public ApiResponse<HospitalizationRecord> round(
            @RequestParam Long hospitalizationId,
            @RequestParam String conditionDesc,
            @RequestParam String treatmentPlan,
            @RequestBody(required = false) List<InpatientPrescriptionItem> items) {
        return ApiResponse.ok(inpatientService.dailyRound(
                hospitalizationId, conditionDesc, treatmentPlan, items));
    }

    @PostMapping("/hospitalization/discharge")
    public ApiResponse<Integer> discharge(@RequestParam Long hospitalizationId) {
        return ApiResponse.ok(inpatientService.discharge(hospitalizationId));
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/controller/DoctorController.java
git commit -m "feat: add DoctorController for doctor REST API"
```

---

### Task 18: PatientController — 病人接口

**Files:**
- Create: `src/main/java/cn/edu/scnu/hmis/controller/PatientController.java`

- [ ] **Step 1: 创建 PatientController**

REST API 端点：
- `POST /api/patient/register` — 挂号
- `POST /api/patient/pay` — 缴费
- `GET /api/patient/payments` — 查看缴费记录
- `GET /api/patient/unpaid` — 查看待缴费
- `GET /api/patient/history` — 就诊记录
- `GET /api/patient/hospitalization/balance` — 住院余额

```java
package cn.edu.scnu.hmis.controller;

import cn.edu.scnu.hmis.common.ApiResponse;
import cn.edu.scnu.hmis.entity.Payment;
import cn.edu.scnu.hmis.entity.Registration;
import cn.edu.scnu.hmis.service.InpatientService;
import cn.edu.scnu.hmis.service.OutpatientService;
import cn.edu.scnu.hmis.service.StatisticsService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final OutpatientService outpatientService;
    private final InpatientService inpatientService;
    private final StatisticsService statisticsService;

    public PatientController(OutpatientService outpatientService,
                             InpatientService inpatientService,
                             StatisticsService statisticsService) {
        this.outpatientService = outpatientService;
        this.inpatientService = inpatientService;
        this.statisticsService = statisticsService;
    }

    @PostMapping("/register")
    public ApiResponse<Registration> register(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam String timeSlot) {
        return ApiResponse.ok(outpatientService.register(patientId, doctorId, date, timeSlot));
    }

    @PostMapping("/pay")
    public ApiResponse<Integer> pay(
            @RequestParam Long paymentId,
            @RequestParam String payMethod) {
        return ApiResponse.ok(outpatientService.pay(paymentId, payMethod));
    }

    @GetMapping("/payments")
    public ApiResponse<List<Payment>> payments(@RequestParam Long patientId) {
        return ApiResponse.ok(outpatientService.getPayments(patientId));
    }

    @GetMapping("/unpaid")
    public ApiResponse<List<Payment>> unpaid(@RequestParam Long patientId) {
        return ApiResponse.ok(outpatientService.getUnpaidPayments(patientId));
    }

    @GetMapping("/history")
    public ApiResponse<List<Map<String, Object>>> history(@RequestParam Long patientId) {
        return ApiResponse.ok(statisticsService.patientHistory(patientId));
    }

    @GetMapping("/hospitalization/balance")
    public ApiResponse<BigDecimal> balance(@RequestParam Long hospitalizationId) {
        return ApiResponse.ok(inpatientService.getBalance(hospitalizationId));
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/cn/edu/scnu/hmis/controller/PatientController.java
git commit -m "feat: add PatientController for patient REST API"
```

---

## Phase 6: 配置与启动验证

### Task 19: MyBatis 配置与启动验证

**Files:**
- Modify: `src/main/resources/application.properties`

- [ ] **Step 1: 确认 application.properties 配置正确**

```properties
spring.application.name=HMIS

spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql:///hospital?useUnicode=true&useSSL=false&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=123456

mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.configuration.map-underscore-to-camel-case=true
```

- [ ] **Step 2: 启动应用验证**

```bash
./mvnw spring-boot:run
```

Expected: 应用启动成功，无报错。访问 `http://localhost:8080/api/admin/departments` 返回科室列表。

- [ ] **Step 3: 测试核心 API**

```bash
# 获取科室列表
curl http://localhost:8080/api/admin/departments

# 获取医生列表
curl http://localhost:8080/api/admin/doctors

# 挂号
curl -X POST "http://localhost:8080/api/patient/register?patientId=1&doctorId=2&date=2024-01-15&timeSlot=MORNING"

# 查看待缴费
curl "http://localhost:8080/api/patient/unpaid?patientId=1"
```

- [ ] **Step 4: Commit**

```bash
git add .
git commit -m "feat: verify application startup and core API endpoints"
```

---

## Phase 7: 测试

### Task 20: Mapper 单元测试

**Files:**
- Create: `src/test/java/cn/edu/scnu/hmis/mapper/DepartmentMapperTest.java`
- Create: `src/test/java/cn/edu/scnu/hmis/mapper/DoctorMapperTest.java`
- Create: `src/test/java/cn/edu/scnu/hmis/mapper/PatientMapperTest.java`
- Create: `src/test/java/cn/edu/scnu/hmis/mapper/DrugMapperTest.java`

- [ ] **Step 1: 创建 DepartmentMapperTest**

```java
package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Department;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DepartmentMapperTest {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Test
    void findAll_returnsAllDepartments() {
        List<Department> list = departmentMapper.findAll();
        assertThat(list).isNotEmpty();
    }

    @Test
    void insert_and_findById() {
        Department d = new Department();
        d.setName("测试科室");
        d.setLocation("测试位置");
        departmentMapper.insert(d);

        Department found = departmentMapper.findById(d.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("测试科室");
    }
}
```

- [ ] **Step 2: 创建其余 Mapper 测试（Doctor、Patient、Drug）**

模式相同：findAll 非空断言 + insert 后 findById 验证。

- [ ] **Step 3: 运行测试**

```bash
./mvnw test
```

- [ ] **Step 4: Commit**

```bash
git add src/test/java/cn/edu/scnu/hmis/mapper/
git commit -m "test: add mapper unit tests"
```

---

### Task 21: Service 单元测试

**Files:**
- Create: `src/test/java/cn/edu/scnu/hmis/service/AdminServiceTest.java`
- Create: `src/test/java/cn/edu/scnu/hmis/service/OutpatientServiceTest.java`

- [ ] **Step 1: 创建 AdminServiceTest（Mockito）**

```java
package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.Department;
import cn.edu.scnu.hmis.mapper.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private DepartmentMapper departmentMapper;
    @Mock private DoctorMapper doctorMapper;
    @Mock private PatientMapper patientMapper;
    @Mock private DrugMapper drugMapper;
    @Mock private DoctorFeeMapper doctorFeeMapper;
    @Mock private WardMapper wardMapper;
    @Mock private BedMapper bedMapper;

    @InjectMocks
    private AdminService adminService;

    @Test
    void listDepartments_returnsAll() {
        Department d = new Department();
        d.setId(1L);
        d.setName("内科");
        when(departmentMapper.findAll()).thenReturn(List.of(d));

        List<Department> result = adminService.listDepartments();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("内科");
    }
}
```

- [ ] **Step 2: 创建 OutpatientServiceTest**

测试 register 方法创建挂号和账单、visit 方法创建就诊记录等。

- [ ] **Step 3: 运行测试**

```bash
./mvnw test
```

- [ ] **Step 4: Commit**

```bash
git add src/test/java/cn/edu/scnu/hmis/service/
git commit -m "test: add service unit tests"
```

---

## 自审清单

- [ ] 20 张表全部有对应实体类和 Mapper
- [ ] 全部 8+2 个触发器在 triggers.sql 中
- [ ] 全部 4 个视图在 views.sql 中
- [ ] 全部 14 个索引在 indexes.sql 中
- [ ] 门诊全流程 API：挂号→缴费→就诊→开处方→缴费
- [ ] 住院全流程 API：入院→预缴→查房→出院
- [ ] 统计查询通过视图实现
- [ ] 统一 ApiResponse 响应格式
- [ ] 构造器注入（非 @Autowired 字段注入）
- [ ] MyBatis XML 映射文件齐全
- [ ] 测试覆盖 Mapper 和 Service 层
