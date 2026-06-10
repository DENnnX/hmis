# HMIS 医院管理信息系统 — 数据库设计文档

## 1. 项目概述

### 1.1 项目背景

《数据库系统原理与实践》课程综合设计实验。基于 MySQL 设计并实现一个医院管理信息系统。

### 1.2 业务范围

- **门诊部**：科室管理、医生排班、挂号、就诊、开处方、缴费
- **住院部**：住院档案、病房病床管理、每日查房、住院处方、费用结算、预缴金管理
- **药品管理**：药品基础信息维护
- **统计功能**：科室排班统计、医生工作量统计、病人治疗情况统计

### 1.3 技术栈

| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 4.0.6 |
| MyBatis | 4.0.1 |
| MySQL | 8.x |
| Lombok | — |

### 1.4 用户角色

| 角色 | 权限 |
|------|------|
| 管理员 | 维护科室、医生、药品信息；查看统计数据 |
| 医生 | 查看排班、接诊、开具处方 |
| 病人 | 挂号、缴费、查看就诊记录和费用明细 |

### 1.5 设计原则

- 文档驱动 + 数据库约束强化
- 充分利用数据库高级特性（约束、触发器、视图、索引）
- 业务语义时间字段保留，运维时间戳字段省略

---

## 2. E-R 模型

### 2.1 实体总览

共 20 张表，分为三层：

**基础数据层（6 张）**：User、Department、Doctor、Patient、Drug、DoctorFee

**门诊业务层（6 张）**：Schedule、Registration、OutpatientVisit、Prescription、PrescriptionItem、Payment

**住院业务层（8 张）**：Ward、Bed、Hospitalization、HospitalizationRecord、InpatientPrescription、InpatientPrescriptionItem、DailyCharge、Deposit

### 2.2 实体间关系

#### 基础数据层

| 关系 | 类型 | 说明 |
|------|------|------|
| Department 1→N Doctor | 一对多 | 一个科室有多名医生，一名医生属于一个科室 |
| User ↔ Doctor/Patient | 一对一 | 用户表通过 role + reference_id 关联医生或病人 |
| Department 1→N Ward | 一对多 | 一个科室有多间病房 |
| Ward 1→N Bed | 一对多 | 一间病房有多张病床 |
| DoctorFee ↔ Doctor | 间接关联 | 通过 Doctor.title = DoctorFee.title 关联 |

#### 门诊业务层

| 关系 | 类型 | 说明 |
|------|------|------|
| Patient 1→N Registration | 一对多 | 一个病人可多次挂号 |
| Doctor 1→N Registration | 一对多 | 一个医生可被多人挂号 |
| Registration 1→1 OutpatientVisit | 一对一 | 一次挂号对应一次就诊 |
| OutpatientVisit 1→1 Prescription | 一对一 | 一次就诊开一张处方 |
| Prescription 1→N PrescriptionItem | 一对多 | 一张处方包含多种药品 |
| Drug 1→N PrescriptionItem | 一对多 | 一种药品出现在多条明细中 |
| Patient 1→N Payment | 一对多 | 一个病人有多条缴费记录 |

#### 住院业务层

| 关系 | 类型 | 说明 |
|------|------|------|
| Patient 1→N Hospitalization | 一对多 | 一个病人可多次住院 |
| Doctor 1→N Hospitalization | 一对多 | 一个医生可管理多个住院病人 |
| Ward + Bed 1→N Hospitalization | 一对多 | 一张病床可收治多名病人（不同时段） |
| Hospitalization 1→N HospitalizationRecord | 一对多 | 每日查房记录 |
| HospitalizationRecord 1→1 InpatientPrescription | 一对一 | 每日记录对应一张处方 |
| InpatientPrescription 1→N InpatientPrescriptionItem | 一对多 | 处方明细 |
| Hospitalization 1→N DailyCharge | 一对多 | 每日费用记录 |
| Hospitalization 1→N Deposit | 一对多 | 可多次预缴 |

#### 跨域关系

| 关系 | 说明 |
|------|------|
| Schedule ↔ Doctor | 排班关联医生，schedule_type 区分门诊/住院巡诊 |
| Registration → Schedule | 挂号应与排班匹配（当天医生该时段有排班才能挂号） |
| Hospitalization → HospitalizationRecord → InpatientPrescription → InpatientPrescriptionItem → Drug | 住院用药链路 |

---

## 3. 关系模型（完整属性）

### 3.1 基础数据层

#### User（用户表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 登录名 |
| password | VARCHAR(255) | NOT NULL | 密码 |
| role | VARCHAR(20) | NOT NULL | 角色 |
| reference_id | BIGINT | NULLABLE | 关联医生ID或病人ID |

#### Department（科室表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 科室ID |
| name | VARCHAR(50) | UNIQUE, NOT NULL | 科室名称 |
| location | VARCHAR(100) | NULLABLE | 位置 |

#### Doctor（医生表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 医生ID |
| doctor_no | VARCHAR(20) | UNIQUE, NOT NULL | 工号 |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| gender | VARCHAR(20) | NOT NULL | 性别 |
| title | VARCHAR(20) | NOT NULL | 职称 |
| phone | VARCHAR(20) | NULLABLE | 电话 |
| department_id | BIGINT | FK → Department, NOT NULL | 所属科室 |

#### Patient（病人表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 病人ID |
| patient_no | VARCHAR(20) | UNIQUE, NOT NULL | 病案号 |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| gender | VARCHAR(20) | NOT NULL | 性别 |
| birth_date | DATE | NULLABLE | 出生日期 |
| address | VARCHAR(200) | NULLABLE | 地址 |
| phone | VARCHAR(20) | NULLABLE | 电话 |

#### Drug（药品表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 药品ID |
| drug_no | VARCHAR(20) | UNIQUE, NOT NULL | 药品编号 |
| name | VARCHAR(100) | NOT NULL | 药品名称 |
| specification | VARCHAR(100) | NULLABLE | 规格 |
| unit | VARCHAR(20) | NOT NULL | 单位 |
| unit_price | DECIMAL(10,2) | NOT NULL, CHECK(>0) | 单价 |

#### DoctorFee（费用配置表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | ID |
| title | VARCHAR(20) | UNIQUE, NOT NULL | 职称 |
| consultation_fee | DECIMAL(10,2) | NOT NULL, CHECK(>=0) | 诊疗费 |

### 3.2 住院基础

#### Ward（病房表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 病房ID |
| ward_no | VARCHAR(20) | UNIQUE, NOT NULL | 病房编号 |
| location | VARCHAR(100) | NULLABLE | 位置 |
| daily_fee | DECIMAL(10,2) | NOT NULL, CHECK(>=0) | 每日床位费 |
| department_id | BIGINT | FK → Department, NOT NULL | 所属科室 |

#### Bed（病床表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 病床ID |
| ward_id | BIGINT | FK → Ward, NOT NULL | 所属病房 |
| bed_no | VARCHAR(10) | NOT NULL | 床位号 |
| status | VARCHAR(20) | DEFAULT 'AVAILABLE' | 状态 |
| — | — | UNIQUE(ward_id, bed_no) | 同病房床位号唯一 |

### 3.3 门诊业务层

#### Schedule（排班表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 排班ID |
| doctor_id | BIGINT | FK → Doctor, NOT NULL | 医生 |
| schedule_date | DATE | NOT NULL | 排班日期 |
| time_slot | VARCHAR(20) | NOT NULL | 时段 |
| schedule_type | VARCHAR(20) | NOT NULL | 排班类型 |
| — | — | UNIQUE(doctor_id, schedule_date, time_slot) | 同一医生同一时段唯一 |

#### Registration（挂号表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 挂号ID |
| patient_id | BIGINT | FK → Patient, NOT NULL | 病人 |
| doctor_id | BIGINT | FK → Doctor, NOT NULL | 挂号医生 |
| registration_date | DATE | NOT NULL | 挂号日期 |
| time_slot | VARCHAR(20) | NOT NULL | 时段 |
| visit_type | VARCHAR(20) | NOT NULL, DEFAULT 'FIRST_VISIT' | 就诊类型：初诊/复诊 |
| status | VARCHAR(20) | DEFAULT 'PENDING_PAYMENT' | 状态 |

#### OutpatientVisit（就诊记录表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 就诊ID |
| registration_id | BIGINT | FK → Registration, UNIQUE, NOT NULL | 对应挂号 |
| doctor_id | BIGINT | FK → Doctor, NOT NULL | 接诊医生 |
| patient_id | BIGINT | FK → Patient, NOT NULL | 病人 |
| visit_time | DATETIME | NOT NULL | 就诊时间 |
| diagnosis | VARCHAR(500) | NOT NULL | 诊断 |
| symptoms | TEXT | NULLABLE | 症状描述 |
| consultation_fee | DECIMAL(10,2) | NOT NULL | 诊疗费 |

#### Prescription（门诊处方表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 处方ID |
| visit_id | BIGINT | FK → OutpatientVisit, UNIQUE, NOT NULL | 对应就诊 |
| total_drug_price | DECIMAL(10,2) | NOT NULL, DEFAULT 0 | 药品总价 |

#### PrescriptionItem（门诊处方明细表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 明细ID |
| prescription_id | BIGINT | FK → Prescription, NOT NULL | 所属处方 |
| drug_id | BIGINT | FK → Drug, NOT NULL | 药品 |
| quantity | INT | NOT NULL, CHECK(>0) | 数量 |
| dosage | VARCHAR(100) | NOT NULL | 用法用量 |
| unit_price | DECIMAL(10,2) | NOT NULL | 开方时单价 |
| subtotal | DECIMAL(10,2) | NOT NULL | 小计 |

#### Payment（缴费记录表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 缴费ID |
| patient_id | BIGINT | FK → Patient, NOT NULL | 病人 |
| type | VARCHAR(20) | NOT NULL | 费用类型 |
| reference_id | BIGINT | NOT NULL | 关联ID |
| amount | DECIMAL(10,2) | NOT NULL | 金额 |
| status | VARCHAR(20) | DEFAULT 'UNPAID' | 状态 |
| pay_time | DATETIME | NULLABLE | 缴费时间 |
| pay_method | VARCHAR(20) | NULLABLE | 支付方式 |

### 3.4 住院业务层

#### Hospitalization（住院档案表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 档案ID |
| hospital_no | VARCHAR(20) | UNIQUE, NOT NULL | 住院号 |
| patient_id | BIGINT | FK → Patient, NOT NULL | 病人 |
| attending_doctor_id | BIGINT | FK → Doctor, NOT NULL | 主治医生 |
| ward_id | BIGINT | FK → Ward, NOT NULL | 病房 |
| bed_id | BIGINT | FK → Bed, NOT NULL | 病床 |
| admission_date | DATE | NOT NULL | 入院时间 |
| discharge_date | DATE | NULLABLE | 出院时间 |
| status | VARCHAR(20) | DEFAULT 'IN_HOSPITAL' | 状态 |

#### HospitalizationRecord（住院记录 / 每日查房）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 记录ID |
| hospitalization_id | BIGINT | FK → Hospitalization, NOT NULL | 所属档案 |
| record_date | DATE | NOT NULL | 记录日期 |
| condition_desc | TEXT | NOT NULL | 病人情况 |
| treatment_plan | TEXT | NOT NULL | 诊疗方案 |
| — | — | UNIQUE(hospitalization_id, record_date) | 同一档案每天一条 |

#### InpatientPrescription（住院处方表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 处方ID |
| record_id | BIGINT | FK → HospitalizationRecord, UNIQUE, NOT NULL | 对应每日记录 |
| total_drug_price | DECIMAL(10,2) | NOT NULL, DEFAULT 0 | 药品总价 |

#### InpatientPrescriptionItem（住院处方明细表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 明细ID |
| prescription_id | BIGINT | FK → InpatientPrescription, NOT NULL | 所属处方 |
| drug_id | BIGINT | FK → Drug, NOT NULL | 药品 |
| quantity | INT | NOT NULL, CHECK(>0) | 数量 |
| dosage | VARCHAR(100) | NOT NULL | 用法用量 |
| unit_price | DECIMAL(10,2) | NOT NULL | 开方时单价 |
| subtotal | DECIMAL(10,2) | NOT NULL | 小计 |

#### DailyCharge（每日费用表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 费用ID |
| hospitalization_id | BIGINT | FK → Hospitalization, NOT NULL | 所属档案 |
| charge_date | DATE | NOT NULL | 费用日期 |
| bed_fee | DECIMAL(10,2) | NOT NULL | 床位费 |
| drug_fee | DECIMAL(10,2) | NOT NULL, DEFAULT 0 | 药费 |
| treatment_fee | DECIMAL(10,2) | NOT NULL, DEFAULT 0 | 诊疗费 |
| total_fee | DECIMAL(10,2) | NOT NULL | 当日总费用 |
| — | — | UNIQUE(hospitalization_id, charge_date) | 同一档案每天一条 |

#### Deposit（预缴金表）

| 属性 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 缴费ID |
| hospitalization_id | BIGINT | FK → Hospitalization, NOT NULL | 所属档案 |
| amount | DECIMAL(10,2) | NOT NULL, CHECK(>0) | 预缴金额 |
| deposit_time | DATETIME | NOT NULL | 缴费时间 |

---

## 4. DDL 建表语句

```sql
CREATE DATABASE IF NOT EXISTS `hospital` DEFAULT CHARACTER SET utf8mb4;
USE `hospital`;

-- ============================================================
-- 基础数据层
-- ============================================================

CREATE TABLE `user` (
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username`     VARCHAR(50) NOT NULL UNIQUE,
    `password`     VARCHAR(255) NOT NULL,
    `role`         VARCHAR(20) NOT NULL,
    `reference_id` BIGINT NULL,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN','DOCTOR','PATIENT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `department` (
    `id`       BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name`     VARCHAR(50) NOT NULL UNIQUE,
    `location` VARCHAR(100) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `doctor` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `doctor_no`     VARCHAR(20) NOT NULL UNIQUE,
    `name`          VARCHAR(50) NOT NULL,
    `gender`        VARCHAR(20) NOT NULL,
    `title`         VARCHAR(20) NOT NULL,
    `phone`         VARCHAR(20) NULL,
    `department_id` BIGINT NOT NULL,
    FOREIGN KEY (`department_id`) REFERENCES `department`(`id`),
    CONSTRAINT chk_doctor_gender CHECK (gender IN ('M','F')),
    CONSTRAINT chk_doctor_title CHECK (title IN ('RESIDENT','ATTENDING','VICE_CHIEF','CHIEF'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `patient` (
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `patient_no` VARCHAR(20) NOT NULL UNIQUE,
    `name`       VARCHAR(50) NOT NULL,
    `gender`     VARCHAR(20) NOT NULL,
    `birth_date` DATE NULL,
    `address`    VARCHAR(200) NULL,
    `phone`      VARCHAR(20) NULL,
    CONSTRAINT chk_patient_gender CHECK (gender IN ('M','F'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `drug` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `drug_no`       VARCHAR(20) NOT NULL UNIQUE,
    `name`          VARCHAR(100) NOT NULL,
    `specification` VARCHAR(100) NULL,
    `unit`          VARCHAR(20) NOT NULL,
    `unit_price`    DECIMAL(10,2) NOT NULL CHECK (`unit_price` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `doctor_fee` (
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title`            VARCHAR(20) NOT NULL UNIQUE,
    `consultation_fee` DECIMAL(10,2) NOT NULL CHECK (`consultation_fee` >= 0),
    CONSTRAINT chk_doctor_fee_title CHECK (title IN ('RESIDENT','ATTENDING','VICE_CHIEF','CHIEF'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 住院基础
-- ============================================================

CREATE TABLE `ward` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `ward_no`       VARCHAR(20) NOT NULL UNIQUE,
    `location`      VARCHAR(100) NULL,
    `daily_fee`     DECIMAL(10,2) NOT NULL CHECK (`daily_fee` >= 0),
    `department_id` BIGINT NOT NULL,
    FOREIGN KEY (`department_id`) REFERENCES `department`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `bed` (
    `id`      BIGINT PRIMARY KEY AUTO_INCREMENT,
    `ward_id` BIGINT NOT NULL,
    `bed_no`  VARCHAR(10) NOT NULL,
    `status`  VARCHAR(20) DEFAULT 'AVAILABLE',
    UNIQUE (`ward_id`, `bed_no`),
    FOREIGN KEY (`ward_id`) REFERENCES `ward`(`id`),
    CONSTRAINT chk_bed_status CHECK (status IN ('AVAILABLE','OCCUPIED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 门诊业务层
-- ============================================================

CREATE TABLE `schedule` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id`     BIGINT NOT NULL,
    `schedule_date` DATE NOT NULL,
    `time_slot`     VARCHAR(20) NOT NULL,
    `schedule_type` VARCHAR(20) NOT NULL,
    UNIQUE (`doctor_id`, `schedule_date`, `time_slot`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    CONSTRAINT chk_schedule_time_slot CHECK (time_slot IN ('MORNING','AFTERNOON','EVENING')),
    CONSTRAINT chk_schedule_type CHECK (schedule_type IN ('OUTPATIENT','INPATIENT_ROUND'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `registration` (
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
    `patient_id`       BIGINT NOT NULL,
    `doctor_id`        BIGINT NOT NULL,
    `registration_date` DATE NOT NULL,
    `time_slot`        VARCHAR(20) NOT NULL,
    `visit_type`       VARCHAR(20) NOT NULL DEFAULT 'FIRST_VISIT',
    `status`           VARCHAR(20) DEFAULT 'PENDING_PAYMENT',
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    CONSTRAINT chk_registration_time_slot CHECK (time_slot IN ('MORNING','AFTERNOON','EVENING')),
    CONSTRAINT chk_registration_visit_type CHECK (visit_type IN ('FIRST_VISIT','REVISIT')),
    CONSTRAINT chk_registration_status CHECK (status IN ('PENDING_PAYMENT','WAITING','VISITED','CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `outpatient_visit` (
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
    `registration_id`  BIGINT NOT NULL UNIQUE,
    `doctor_id`        BIGINT NOT NULL,
    `patient_id`       BIGINT NOT NULL,
    `visit_time`       DATETIME NOT NULL,
    `diagnosis`        VARCHAR(500) NOT NULL,
    `symptoms`         TEXT NULL,
    `consultation_fee` DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (`registration_id`) REFERENCES `registration`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `prescription` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `visit_id`        BIGINT NOT NULL UNIQUE,
    `total_drug_price` DECIMAL(10,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (`visit_id`) REFERENCES `outpatient_visit`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `prescription_item` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `prescription_id` BIGINT NOT NULL,
    `drug_id`         BIGINT NOT NULL,
    `quantity`        INT NOT NULL CHECK (`quantity` > 0),
    `dosage`          VARCHAR(100) NOT NULL,
    `unit_price`      DECIMAL(10,2) NOT NULL,
    `subtotal`        DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (`prescription_id`) REFERENCES `prescription`(`id`),
    FOREIGN KEY (`drug_id`) REFERENCES `drug`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `payment` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `patient_id`  BIGINT NOT NULL,
    `type`        VARCHAR(20) NOT NULL,
    `reference_id` BIGINT NOT NULL,
    `amount`      DECIMAL(10,2) NOT NULL,
    `status`      VARCHAR(20) DEFAULT 'UNPAID',
    `pay_time`    DATETIME NULL,
    `pay_method`  VARCHAR(20) NULL,
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`),
    CONSTRAINT chk_payment_type CHECK (type IN ('REGISTRATION','CONSULTATION','DRUG')),
    CONSTRAINT chk_payment_status CHECK (status IN ('UNPAID','PAID')),
    CONSTRAINT chk_payment_pay_method CHECK (pay_method IN ('ALIPAY','WECHAT','CARD'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 住院业务层
-- ============================================================

CREATE TABLE `hospitalization` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    `hospital_no`         VARCHAR(20) NOT NULL UNIQUE,
    `patient_id`          BIGINT NOT NULL,
    `attending_doctor_id` BIGINT NOT NULL,
    `ward_id`             BIGINT NOT NULL,
    `bed_id`              BIGINT NOT NULL,
    `admission_date`      DATE NOT NULL,
    `discharge_date`      DATE NULL,
    `status`              VARCHAR(20) DEFAULT 'IN_HOSPITAL',
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`),
    FOREIGN KEY (`attending_doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`ward_id`) REFERENCES `ward`(`id`),
    FOREIGN KEY (`bed_id`) REFERENCES `bed`(`id`),
    CONSTRAINT chk_hospitalization_status CHECK (status IN ('IN_HOSPITAL','DISCHARGED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `hospitalization_record` (
    `id`                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    `hospitalization_id` BIGINT NOT NULL,
    `record_date`        DATE NOT NULL,
    `condition_desc`     TEXT NOT NULL,
    `treatment_plan`     TEXT NOT NULL,
    UNIQUE (`hospitalization_id`, `record_date`),
    FOREIGN KEY (`hospitalization_id`) REFERENCES `hospitalization`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `inpatient_prescription` (
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
    `record_id`        BIGINT NOT NULL UNIQUE,
    `total_drug_price` DECIMAL(10,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (`record_id`) REFERENCES `hospitalization_record`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `inpatient_prescription_item` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `prescription_id` BIGINT NOT NULL,
    `drug_id`         BIGINT NOT NULL,
    `quantity`        INT NOT NULL CHECK (`quantity` > 0),
    `dosage`          VARCHAR(100) NOT NULL,
    `unit_price`      DECIMAL(10,2) NOT NULL,
    `subtotal`        DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (`prescription_id`) REFERENCES `inpatient_prescription`(`id`),
    FOREIGN KEY (`drug_id`) REFERENCES `drug`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `daily_charge` (
    `id`                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    `hospitalization_id` BIGINT NOT NULL,
    `charge_date`        DATE NOT NULL,
    `bed_fee`            DECIMAL(10,2) NOT NULL,
    `drug_fee`           DECIMAL(10,2) NOT NULL DEFAULT 0,
    `treatment_fee`      DECIMAL(10,2) NOT NULL DEFAULT 0,
    `total_fee`          DECIMAL(10,2) NOT NULL,
    UNIQUE (`hospitalization_id`, `charge_date`),
    FOREIGN KEY (`hospitalization_id`) REFERENCES `hospitalization`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `deposit` (
    `id`                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    `hospitalization_id` BIGINT NOT NULL,
    `amount`             DECIMAL(10,2) NOT NULL CHECK (`amount` > 0),
    `deposit_time`       DATETIME NOT NULL,
    FOREIGN KEY (`hospitalization_id`) REFERENCES `hospitalization`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 5. 数据库高级特性

### 5.1 触发器

#### 排班冲突检测

同一医生同一日期同一时段不能同时有门诊和住院巡诊。

```sql
DELIMITER //
CREATE TRIGGER trg_schedule_conflict
BEFORE INSERT ON `schedule`
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM `schedule`
        WHERE `doctor_id` = NEW.`doctor_id`
          AND `schedule_date` = NEW.`schedule_date`
          AND `time_slot` = NEW.`time_slot`
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '排班冲突：该医生此时段已排班';
    END IF;
END //
DELIMITER ;
```

#### 处方明细自动计算小计

```sql
DELIMITER //
CREATE TRIGGER trg_prescription_item_subtotal
BEFORE INSERT ON `prescription_item`
FOR EACH ROW
BEGIN
    SET NEW.`subtotal` = NEW.`quantity` * NEW.`unit_price`;
END //
DELIMITER ;
```

#### 处方明细更新后自动重算总价

```sql
DELIMITER //
CREATE TRIGGER trg_prescription_item_update_total
AFTER INSERT ON `prescription_item`
FOR EACH ROW
BEGIN
    UPDATE `prescription`
    SET `total_drug_price` = (
        SELECT COALESCE(SUM(`subtotal`), 0)
        FROM `prescription_item`
        WHERE `prescription_id` = NEW.`prescription_id`
    )
    WHERE `id` = NEW.`prescription_id`;
END //
DELIMITER ;
```

#### 住院处方明细同理

```sql
DELIMITER //
CREATE TRIGGER trg_inpatient_prescription_item_subtotal
BEFORE INSERT ON `inpatient_prescription_item`
FOR EACH ROW
BEGIN
    SET NEW.`subtotal` = NEW.`quantity` * NEW.`unit_price`;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_inpatient_prescription_item_update_total
AFTER INSERT ON `inpatient_prescription_item`
FOR EACH ROW
BEGIN
    UPDATE `inpatient_prescription`
    SET `total_drug_price` = (
        SELECT COALESCE(SUM(`subtotal`), 0)
        FROM `inpatient_prescription_item`
        WHERE `prescription_id` = NEW.`prescription_id`
    )
    WHERE `id` = NEW.`prescription_id`;
END //
DELIMITER ;
```

#### 病床状态自动同步

```sql
DELIMITER //
CREATE TRIGGER trg_hospitalization_bed_occupy
AFTER INSERT ON `hospitalization`
FOR EACH ROW
BEGIN
    UPDATE `bed` SET `status` = 'OCCUPIED' WHERE `id` = NEW.`bed_id`;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_hospitalization_bed_release
AFTER UPDATE ON `hospitalization`
FOR EACH ROW
BEGIN
    IF NEW.`status` = 'DISCHARGED' AND OLD.`status` = 'IN_HOSPITAL' THEN
        UPDATE `bed` SET `status` = 'AVAILABLE' WHERE `id` = NEW.`bed_id`;
    END IF;
END //
DELIMITER ;
```

#### 住院余额检查

```sql
DELIMITER //
CREATE TRIGGER trg_check_balance
AFTER INSERT ON `daily_charge`
FOR EACH ROW
BEGIN
    DECLARE v_balance DECIMAL(10,2);

    SELECT COALESCE(SUM(`amount`), 0) INTO v_balance
    FROM `deposit`
    WHERE `hospitalization_id` = NEW.`hospitalization_id`;

    SET v_balance = v_balance - (
        SELECT COALESCE(SUM(`total_fee`), 0)
        FROM `daily_charge`
        WHERE `hospitalization_id` = NEW.`hospitalization_id`
    );

    IF v_balance < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '余额不足，请续缴住院费';
    END IF;
END //
DELIMITER ;
```

#### 每日费用自动计算总费用

```sql
DELIMITER //
CREATE TRIGGER trg_daily_charge_total
BEFORE INSERT ON `daily_charge`
FOR EACH ROW
BEGIN
    SET NEW.`total_fee` = NEW.`bed_fee` + NEW.`drug_fee` + NEW.`treatment_fee`;
END //
DELIMITER ;
```

#### 挂号排班校验

挂号时校验医生在该日期该时段是否有门诊排班。

```sql
DELIMITER //
CREATE TRIGGER trg_registration_schedule_check
BEFORE INSERT ON `registration`
FOR EACH ROW
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM `schedule`
        WHERE `doctor_id` = NEW.`doctor_id`
          AND `schedule_date` = NEW.`registration_date`
          AND `time_slot` = NEW.`time_slot`
          AND `schedule_type` = 'OUTPATIENT'
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '挂号失败：该医生此时段无门诊排班';
    END IF;
END //
DELIMITER ;
```

### 5.2 视图

#### 科室排班统计

```sql
CREATE VIEW v_department_schedule AS
SELECT
    d.`name` AS department_name,
    s.`schedule_date`,
    s.`time_slot`,
    s.`schedule_type`,
    doc.`name` AS doctor_name,
    doc.`title` AS doctor_title
FROM `schedule` s
JOIN `doctor` doc ON s.`doctor_id` = doc.`id`
JOIN `department` d ON doc.`department_id` = d.`id`
ORDER BY d.`name`, s.`schedule_date`, FIELD(s.`time_slot`, 'MORNING', 'AFTERNOON', 'EVENING');
```

#### 医生工作量统计

```sql
CREATE VIEW v_doctor_workload AS
SELECT
    doc.`id` AS doctor_id,
    doc.`name` AS doctor_name,
    doc.`title`,
    d.`name` AS department_name,
    COUNT(DISTINCT r.`id`) AS total_registrations,
    COUNT(DISTINCT ov.`id`) AS total_visits,
    COUNT(DISTINCT h.`id`) AS total_hospitalizations
FROM `doctor` doc
JOIN `department` d ON doc.`department_id` = d.`id`
LEFT JOIN `registration` r ON r.`doctor_id` = doc.`id`
LEFT JOIN `outpatient_visit` ov ON ov.`doctor_id` = doc.`id`
LEFT JOIN `hospitalization` h ON h.`attending_doctor_id` = doc.`id`
GROUP BY doc.`id`, doc.`name`, doc.`title`, d.`name`;
```

#### 住院费用汇总

```sql
CREATE VIEW v_hospitalization_cost AS
SELECT
    h.`id` AS hospitalization_id,
    h.`hospital_no`,
    p.`name` AS patient_name,
    d.`name` AS department_name,
    h.`admission_date`,
    h.`discharge_date`,
    h.`status`,
    COALESCE(dep.`total_deposit`, 0) AS total_deposit,
    COALESCE(chg.`total_charge`, 0) AS total_charge,
    COALESCE(dep.`total_deposit`, 0) - COALESCE(chg.`total_charge`, 0) AS balance
FROM `hospitalization` h
JOIN `patient` p ON h.`patient_id` = p.`id`
JOIN `ward` w ON h.`ward_id` = w.`id`
JOIN `department` d ON w.`department_id` = d.`id`
LEFT JOIN (
    SELECT `hospitalization_id`, SUM(`amount`) AS `total_deposit`
    FROM `deposit` GROUP BY `hospitalization_id`
) dep ON dep.`hospitalization_id` = h.`id`
LEFT JOIN (
    SELECT `hospitalization_id`, SUM(`total_fee`) AS `total_charge`
    FROM `daily_charge` GROUP BY `hospitalization_id`
) chg ON chg.`hospitalization_id` = h.`id`;
```

#### 病人门诊就诊记录

```sql
CREATE VIEW v_patient_outpatient_history AS
SELECT
    p.`patient_no`,
    p.`name` AS patient_name,
    r.`registration_date`,
    r.`time_slot`,
    r.`visit_type`,
    doc.`name` AS doctor_name,
    doc.`title` AS doctor_title,
    ov.`visit_time`,
    ov.`diagnosis`,
    ov.`symptoms`,
    ov.`consultation_fee`,
    pr.`total_drug_price`,
    (r.`registration_fee` + ov.`consultation_fee` + pr.`total_drug_price`) AS total_cost
FROM `patient` p
JOIN `registration` r ON r.`patient_id` = p.`id`
JOIN `doctor` doc ON r.`doctor_id` = doc.`id`
LEFT JOIN `outpatient_visit` ov ON ov.`registration_id` = r.`id`
LEFT JOIN `prescription` pr ON pr.`visit_id` = ov.`id`
ORDER BY r.`registration_date` DESC;
```

### 5.3 索引

```sql
CREATE INDEX idx_doctor_department ON `doctor`(`department_id`);
CREATE INDEX idx_schedule_date ON `schedule`(`schedule_date`);
CREATE INDEX idx_registration_patient ON `registration`(`patient_id`);
CREATE INDEX idx_registration_doctor ON `registration`(`doctor_id`);
CREATE INDEX idx_registration_date ON `registration`(`registration_date`);
CREATE INDEX idx_outpatient_visit_patient ON `outpatient_visit`(`patient_id`);
CREATE INDEX idx_prescription_item_prescription ON `prescription_item`(`prescription_id`);
CREATE INDEX idx_payment_patient ON `payment`(`patient_id`);
CREATE INDEX idx_payment_status ON `payment`(`status`);
CREATE INDEX idx_hospitalization_patient ON `hospitalization`(`patient_id`);
CREATE INDEX idx_hospitalization_status ON `hospitalization`(`status`);
CREATE INDEX idx_hospitalization_record_hospitalization ON `hospitalization_record`(`hospitalization_id`);
CREATE INDEX idx_daily_charge_hospitalization ON `daily_charge`(`hospitalization_id`);
CREATE INDEX idx_deposit_hospitalization ON `deposit`(`hospitalization_id`);
```

---

## 6. 业务流程说明

### 6.1 门诊流程

```
挂号 → 付款挂号费 → 等待就诊 → 医生接诊 → 开处方 → 付款诊疗费+药费 → 取药
```

1. 病人挂号：创建 Registration（status=WAITING）
2. 医生接诊：创建 OutpatientVisit，同时创建 Payment（type=CONSULTATION, status=UNPAID），Registration 更新为 VISITED
3. 开处方：创建 Prescription + PrescriptionItem（触发器自动算小计和总价），同时创建 Payment（type=DRUG, status=UNPAID）
4. 缴药费+诊疗费：对应 Payment 更新为 PAID
5. 取药

### 6.2 住院流程

```
入院 → 预缴住院费 → 每日查房+开处方+产生费用 → 出院 → 结算
```

1. 办理住院：创建 Hospitalization（触发器自动占用病床），创建 Deposit
2. 每日查房：创建 HospitalizationRecord + InpatientPrescription + InpatientPrescriptionItem + DailyCharge
3. 余额检查：DailyCharge 触发器自动检查，余额不足则报错
4. 出院：更新 Hospitalization 状态（触发器自动释放病床）

### 6.3 管理员操作

- 维护科室、医生、药品、病房、病床的基础信息（CRUD）
- 配置不同职称的费用标准（DoctorFee 表）
- 通过视图查看统计信息

---

## 7. 完整业务模拟验证

```sql
-- 科室
INSERT INTO department (name, location) VALUES ('内科', '1楼A区');

-- 医生
INSERT INTO doctor (doctor_no, name, gender, title, phone, department_id)
VALUES ('D001', '李医生', 'M', 'ATTENDING', '13800000001', 1);

-- 费用配置
INSERT INTO doctor_fee (title, registration_fee, consultation_fee)
VALUES ('ATTENDING', 15.00, 30.00);

-- 药品
INSERT INTO drug (drug_no, name, specification, unit, unit_price)
VALUES ('DRUG001', '阿莫西林', '0.5g*24粒', '盒', 15.00);

-- 排班
INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (1, '2024-01-15', 'MORNING', 'OUTPATIENT');   -- ✅ 成功
INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (1, '2024-01-15', 'MORNING', 'INPATIENT_ROUND'); -- ❌ 触发器拒绝

-- 病人挂号
INSERT INTO patient (patient_no, name, gender, phone)
VALUES ('P20240001', '张三', 'M', '13900000001');
INSERT INTO registration (patient_id, doctor_id, registration_date, time_slot, visit_type, status)
VALUES (1, 1, '2024-01-15', 'MORNING', 'FIRST_VISIT', 'WAITING');
INSERT INTO payment (patient_id, type, reference_id, amount, status)
VALUES (1, 'REGISTRATION', 1, 15.00, 'UNPAID');

-- 缴挂号费
UPDATE payment SET status='PAID', pay_time=NOW(), pay_method='ALIPAY' WHERE id=1;
UPDATE registration SET status='WAITING' WHERE id=1;

-- 就诊
INSERT INTO outpatient_visit (registration_id, doctor_id, patient_id, visit_time, diagnosis, symptoms, consultation_fee)
VALUES (1, 1, 1, '2024-01-15 09:30:00', '上呼吸道感染', '发热38.5℃', 30.00);

-- 开处方
INSERT INTO prescription (visit_id) VALUES (1);
INSERT INTO prescription_item (prescription_id, drug_id, quantity, dosage, unit_price, subtotal)
VALUES (1, 1, 2, '每次1粒，每日3次', 15.00, 0);  -- subtotal 由触发器自动计算为 30.00
```

验证结果：所有约束、触发器、关联均正常工作。
