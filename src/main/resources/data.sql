-- HMIS Hospital Management Information System
-- Initial test data
-- All INSERT statements include explicit column names.

USE hospital;

-- ============================================================
-- 1. Admin user (reference_id = NULL)
-- ============================================================
INSERT INTO `user` (username, password, role, reference_id)
VALUES ('admin', 'admin123', 'ADMIN', NULL);

-- ============================================================
-- 2. Departments
-- ============================================================
INSERT INTO department (name, location)
VALUES ('内科', '1楼A区');

INSERT INTO department (name, location)
VALUES ('外科', '1楼B区');

-- ============================================================
-- 3. Doctors (one per title)
-- ============================================================
INSERT INTO doctor (doctor_no, name, gender, title, phone, department_id)
VALUES ('D001', '张伟', 'M', 'RESIDENT', '13800000001', 1);

INSERT INTO doctor (doctor_no, name, gender, title, phone, department_id)
VALUES ('D002', '李芳', 'F', 'ATTENDING', '13800000002', 1);

INSERT INTO doctor (doctor_no, name, gender, title, phone, department_id)
VALUES ('D003', '王强', 'M', 'VICE_CHIEF', '13800000003', 2);

INSERT INTO doctor (doctor_no, name, gender, title, phone, department_id)
VALUES ('D004', '赵敏', 'F', 'CHIEF', '13800000004', 2);

-- ============================================================
-- 4. Doctor user accounts (reference_id = doctor.id)
-- ============================================================
INSERT INTO `user` (username, password, role, reference_id)
VALUES ('zhangwei', '123456', 'DOCTOR', 1);

INSERT INTO `user` (username, password, role, reference_id)
VALUES ('lifang', '123456', 'DOCTOR', 2);

INSERT INTO `user` (username, password, role, reference_id)
VALUES ('wangqiang', '123456', 'DOCTOR', 3);

INSERT INTO `user` (username, password, role, reference_id)
VALUES ('zhaomin', '123456', 'DOCTOR', 4);

-- ============================================================
-- 5. Doctor fee configurations
-- ============================================================
INSERT INTO doctor_fee (title, consultation_fee)
VALUES ('RESIDENT', 10.00);

INSERT INTO doctor_fee (title, consultation_fee)
VALUES ('ATTENDING', 30.00);

INSERT INTO doctor_fee (title, consultation_fee)
VALUES ('VICE_CHIEF', 50.00);

INSERT INTO doctor_fee (title, consultation_fee)
VALUES ('CHIEF', 80.00);

-- ============================================================
-- 6. Drugs
-- ============================================================
INSERT INTO drug (drug_no, name, specification, unit, unit_price)
VALUES ('DRUG001', '阿莫西林', '0.5g*24粒', '盒', 15.00);

INSERT INTO drug (drug_no, name, specification, unit, unit_price)
VALUES ('DRUG002', '布洛芬', '0.2g*20片', '盒', 12.50);

INSERT INTO drug (drug_no, name, specification, unit, unit_price)
VALUES ('DRUG003', '头孢克洛', '0.25g*12粒', '盒', 28.00);

INSERT INTO drug (drug_no, name, specification, unit, unit_price)
VALUES ('DRUG004', '复方感冒灵', '12袋', '盒', 18.00);

INSERT INTO drug (drug_no, name, specification, unit, unit_price)
VALUES ('DRUG005', '止咳糖浆', '100ml', '瓶', 22.00);

-- ============================================================
-- 7. Patients
-- ============================================================
INSERT INTO patient (patient_no, name, gender, birth_date, address, phone)
VALUES ('P20240001', '张三', 'M', '1990-05-10', '广州市天河区xxx', '13900000001');

INSERT INTO patient (patient_no, name, gender, birth_date, address, phone)
VALUES ('P20240002', '李四', 'F', '1985-08-22', '广州市越秀区xxx', '13900000002');

INSERT INTO patient (patient_no, name, gender, birth_date, address, phone)
VALUES ('P20240003', '王五', 'M', '1978-12-01', '广州市海珠区xxx', '13900000003');

INSERT INTO patient (patient_no, name, gender, birth_date, address, phone)
VALUES ('P20240004', '赵六', 'F', '1995-03-15', '广州市番禺区xxx', '13900000004');

-- ============================================================
-- 8. Patient user accounts (reference_id = patient.id)
-- ============================================================
INSERT INTO `user` (username, password, role, reference_id)
VALUES ('zhangsan', '123456', 'PATIENT', 1);

INSERT INTO `user` (username, password, role, reference_id)
VALUES ('lisi', '123456', 'PATIENT', 2);

INSERT INTO `user` (username, password, role, reference_id)
VALUES ('wangwu', '123456', 'PATIENT', 3);

INSERT INTO `user` (username, password, role, reference_id)
VALUES ('zhaoliu', '123456', 'PATIENT', 4);

-- ============================================================
-- 9. Wards
-- ============================================================
INSERT INTO ward (ward_no, location, daily_fee, department_id)
VALUES ('W201', '2楼A区', 80.00, 1);

INSERT INTO ward (ward_no, location, daily_fee, department_id)
VALUES ('W205', '2楼B区', 120.00, 2);

-- ============================================================
-- 10. Beds (2 per ward, status defaults to 'AVAILABLE')
-- ============================================================
INSERT INTO bed (ward_id, bed_no)
VALUES (1, '1');

INSERT INTO bed (ward_id, bed_no)
VALUES (1, '2');

INSERT INTO bed (ward_id, bed_no)
VALUES (2, '1');

INSERT INTO bed (ward_id, bed_no)
VALUES (2, '2');

-- ============================================================
-- 11. Schedules (future outpatient schedules for patient registration)
-- ============================================================
INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (1, CURDATE(), 'MORNING', 'OUTPATIENT');

INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (2, CURDATE(), 'AFTERNOON', 'OUTPATIENT');

INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', 'OUTPATIENT');

INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', 'OUTPATIENT');

INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', 'OUTPATIENT');

INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', 'OUTPATIENT');

INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AFTERNOON', 'OUTPATIENT');

INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'MORNING', 'OUTPATIENT');

INSERT INTO schedule (doctor_id, schedule_date, time_slot, schedule_type)
VALUES (2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'MORNING', 'OUTPATIENT');
