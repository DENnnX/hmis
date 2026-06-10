-- HMIS Hospital Management Information System
-- Database: MySQL 8.x, charset utf8mb4

CREATE DATABASE IF NOT EXISTS hospital DEFAULT CHARACTER SET utf8mb4;

USE hospital;

-- ============================================================
-- 基础数据层 (Base Data Layer) - 6 tables
-- ============================================================

CREATE TABLE IF NOT EXISTS `user` (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL,
    reference_id BIGINT     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username),
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN','DOCTOR','PATIENT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `department` (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(50)  NOT NULL,
    location VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_department_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `doctor` (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    doctor_no     VARCHAR(20) NOT NULL,
    name          VARCHAR(50) NOT NULL,
    gender        VARCHAR(20) NOT NULL,
    title         VARCHAR(20) NOT NULL,
    phone         VARCHAR(20) NULL,
    department_id BIGINT      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_doctor_no (doctor_no),
    CONSTRAINT fk_doctor_department FOREIGN KEY (department_id) REFERENCES `department`(id),
    CONSTRAINT chk_doctor_gender CHECK (gender IN ('M','F')),
    CONSTRAINT chk_doctor_title CHECK (title IN ('RESIDENT','ATTENDING','VICE_CHIEF','CHIEF'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `patient` (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    patient_no VARCHAR(20)  NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    gender     VARCHAR(20)  NOT NULL,
    birth_date DATE         NULL,
    address    VARCHAR(200) NULL,
    phone      VARCHAR(20)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_no (patient_no),
    CONSTRAINT chk_patient_gender CHECK (gender IN ('M','F'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `drug` (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    drug_no       VARCHAR(20)   NOT NULL,
    name          VARCHAR(100)  NOT NULL,
    specification VARCHAR(100)  NULL,
    unit          VARCHAR(20)   NOT NULL,
    unit_price    DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_drug_no (drug_no),
    CONSTRAINT chk_drug_unit_price CHECK (unit_price > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `doctor_fee` (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    title            VARCHAR(20)  NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_doctor_fee_title (title),
    CONSTRAINT chk_consultation_fee CHECK (consultation_fee >= 0),
    CONSTRAINT chk_doctor_fee_title CHECK (title IN ('RESIDENT','ATTENDING','VICE_CHIEF','CHIEF'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 住院基础 (Hospitalization Base) - 2 tables
-- ============================================================

CREATE TABLE IF NOT EXISTS `ward` (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    ward_no       VARCHAR(20)   NOT NULL,
    location      VARCHAR(100)  NULL,
    daily_fee     DECIMAL(10,2) NOT NULL,
    department_id BIGINT        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ward_no (ward_no),
    CONSTRAINT fk_ward_department FOREIGN KEY (department_id) REFERENCES `department`(id),
    CONSTRAINT chk_ward_daily_fee CHECK (daily_fee >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `bed` (
    id      BIGINT                    NOT NULL AUTO_INCREMENT,
    ward_id BIGINT                    NOT NULL,
    bed_no  VARCHAR(10)               NOT NULL,
    status  VARCHAR(20) DEFAULT 'AVAILABLE',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ward_bed (ward_id, bed_no),
    CONSTRAINT fk_bed_ward FOREIGN KEY (ward_id) REFERENCES `ward`(id),
    CONSTRAINT chk_bed_status CHECK (status IN ('AVAILABLE','OCCUPIED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 门诊业务层 (Outpatient Business Layer) - 6 tables
-- ============================================================

CREATE TABLE IF NOT EXISTS `schedule` (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id     BIGINT NOT NULL,
    schedule_date DATE   NOT NULL,
    time_slot     VARCHAR(20) NOT NULL,
    schedule_type VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_schedule (doctor_id, schedule_date, time_slot),
    CONSTRAINT fk_schedule_doctor FOREIGN KEY (doctor_id) REFERENCES `doctor`(id),
    CONSTRAINT chk_schedule_time_slot CHECK (time_slot IN ('MORNING','AFTERNOON','EVENING')),
    CONSTRAINT chk_schedule_type CHECK (schedule_type IN ('OUTPATIENT','INPATIENT_ROUND'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `registration` (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    patient_id       BIGINT        NOT NULL,
    doctor_id        BIGINT        NOT NULL,
    registration_date DATE         NOT NULL,
    time_slot        VARCHAR(20) NOT NULL,
    visit_type       VARCHAR(20) NOT NULL DEFAULT 'FIRST_VISIT',
    status           VARCHAR(20) DEFAULT 'PENDING_PAYMENT',
    PRIMARY KEY (id),
    CONSTRAINT fk_registration_patient FOREIGN KEY (patient_id) REFERENCES `patient`(id),
    CONSTRAINT fk_registration_doctor  FOREIGN KEY (doctor_id)  REFERENCES `doctor`(id),
    CONSTRAINT chk_registration_time_slot CHECK (time_slot IN ('MORNING','AFTERNOON','EVENING')),
    CONSTRAINT chk_registration_visit_type CHECK (visit_type IN ('FIRST_VISIT','REVISIT')),
    CONSTRAINT chk_registration_status CHECK (status IN ('PENDING_PAYMENT','WAITING','VISITED','CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `outpatient_visit` (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    registration_id  BIGINT        NOT NULL,
    doctor_id        BIGINT        NOT NULL,
    patient_id       BIGINT        NOT NULL,
    visit_time       DATETIME      NOT NULL,
    diagnosis        VARCHAR(500)  NOT NULL,
    symptoms         TEXT          NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_outpatient_registration (registration_id),
    CONSTRAINT fk_outpatient_registration FOREIGN KEY (registration_id) REFERENCES `registration`(id),
    CONSTRAINT fk_outpatient_doctor       FOREIGN KEY (doctor_id)       REFERENCES `doctor`(id),
    CONSTRAINT fk_outpatient_patient      FOREIGN KEY (patient_id)      REFERENCES `patient`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `prescription` (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    visit_id         BIGINT        NOT NULL,
    total_drug_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_prescription_visit (visit_id),
    CONSTRAINT fk_prescription_visit FOREIGN KEY (visit_id) REFERENCES `outpatient_visit`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `prescription_item` (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    prescription_id BIGINT        NOT NULL,
    drug_id         BIGINT        NOT NULL,
    quantity        INT           NOT NULL,
    dosage          VARCHAR(100)  NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_prescription_item_prescription FOREIGN KEY (prescription_id) REFERENCES `prescription`(id),
    CONSTRAINT fk_prescription_item_drug         FOREIGN KEY (drug_id)         REFERENCES `drug`(id),
    CONSTRAINT chk_prescription_item_quantity    CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `payment` (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    patient_id   BIGINT        NOT NULL,
    type         VARCHAR(20)  NOT NULL,
    reference_id BIGINT        NOT NULL,
    amount       DECIMAL(10,2) NOT NULL,
    status       VARCHAR(20)  DEFAULT 'UNPAID',
    pay_time     DATETIME      NULL,
    pay_method   VARCHAR(20)  NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_payment_patient FOREIGN KEY (patient_id) REFERENCES `patient`(id),
    CONSTRAINT chk_payment_type CHECK (type IN ('REGISTRATION','CONSULTATION','DRUG')),
    CONSTRAINT chk_payment_status CHECK (status IN ('UNPAID','PAID')),
    CONSTRAINT chk_payment_pay_method CHECK (pay_method IN ('ALIPAY','WECHAT','CARD'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 住院业务层 (Inpatient Business Layer) - 6 tables
-- ============================================================

CREATE TABLE IF NOT EXISTS `hospitalization` (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    hospital_no        VARCHAR(20)  NOT NULL,
    patient_id         BIGINT       NOT NULL,
    attending_doctor_id BIGINT      NOT NULL,
    ward_id            BIGINT       NOT NULL,
    bed_id             BIGINT       NOT NULL,
    admission_date     DATE         NOT NULL,
    discharge_date     DATE         NULL,
    status             VARCHAR(20) DEFAULT 'IN_HOSPITAL',
    PRIMARY KEY (id),
    UNIQUE KEY uk_hospitalization_no (hospital_no),
    CONSTRAINT fk_hospitalization_patient FOREIGN KEY (patient_id)          REFERENCES `patient`(id),
    CONSTRAINT fk_hospitalization_doctor  FOREIGN KEY (attending_doctor_id) REFERENCES `doctor`(id),
    CONSTRAINT fk_hospitalization_ward    FOREIGN KEY (ward_id)             REFERENCES `ward`(id),
    CONSTRAINT fk_hospitalization_bed     FOREIGN KEY (bed_id)              REFERENCES `bed`(id),
    CONSTRAINT chk_hospitalization_status CHECK (status IN ('IN_HOSPITAL','DISCHARGED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `hospitalization_record` (
    id                BIGINT NOT NULL AUTO_INCREMENT,
    hospitalization_id BIGINT NOT NULL,
    record_date       DATE   NOT NULL,
    condition_desc    TEXT   NOT NULL,
    treatment_plan    TEXT   NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_hosp_record (hospitalization_id, record_date),
    CONSTRAINT fk_hosp_record_hospitalization FOREIGN KEY (hospitalization_id) REFERENCES `hospitalization`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `inpatient_prescription` (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    record_id        BIGINT        NOT NULL,
    total_drug_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inpatient_prescription_record (record_id),
    CONSTRAINT fk_inpatient_prescription_record FOREIGN KEY (record_id) REFERENCES `hospitalization_record`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `inpatient_prescription_item` (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    prescription_id BIGINT        NOT NULL,
    drug_id         BIGINT        NOT NULL,
    quantity        INT           NOT NULL,
    dosage          VARCHAR(100)  NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_inpatient_item_prescription FOREIGN KEY (prescription_id) REFERENCES `inpatient_prescription`(id),
    CONSTRAINT fk_inpatient_item_drug         FOREIGN KEY (drug_id)         REFERENCES `drug`(id),
    CONSTRAINT chk_inpatient_item_quantity    CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `daily_charge` (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    hospitalization_id BIGINT       NOT NULL,
    charge_date       DATE          NOT NULL,
    bed_fee           DECIMAL(10,2) NOT NULL,
    drug_fee          DECIMAL(10,2) NOT NULL DEFAULT 0,
    treatment_fee     DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_fee         DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_daily_charge (hospitalization_id, charge_date),
    CONSTRAINT fk_daily_charge_hospitalization FOREIGN KEY (hospitalization_id) REFERENCES `hospitalization`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `deposit` (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    hospitalization_id BIGINT       NOT NULL,
    amount            DECIMAL(10,2) NOT NULL,
    deposit_time      DATETIME      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_deposit_hospitalization FOREIGN KEY (hospitalization_id) REFERENCES `hospitalization`(id),
    CONSTRAINT chk_deposit_amount         CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
