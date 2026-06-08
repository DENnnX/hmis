USE hospital;

-- View 1: Department schedule overview
CREATE OR REPLACE VIEW v_department_schedule AS
SELECT d.name AS department_name, s.schedule_date, s.time_slot, s.schedule_type,
       doc.name AS doctor_name, doc.title AS doctor_title
FROM schedule s
JOIN doctor doc ON s.doctor_id = doc.id
JOIN department d ON doc.department_id = d.id;

-- View 2: Doctor workload statistics
CREATE OR REPLACE VIEW v_doctor_workload AS
SELECT doc.id AS doctor_id, doc.name AS doctor_name, doc.title, d.name AS department_name,
       COUNT(DISTINCT r.id) AS total_registrations,
       COUNT(DISTINCT ov.id) AS total_visits,
       COUNT(DISTINCT h.id) AS total_hospitalizations
FROM doctor doc
JOIN department d ON doc.department_id = d.id
LEFT JOIN registration r ON r.doctor_id = doc.id
LEFT JOIN outpatient_visit ov ON ov.doctor_id = doc.id
LEFT JOIN hospitalization h ON h.attending_doctor_id = doc.id
GROUP BY doc.id, doc.name, doc.title, d.name;

-- View 3: Hospitalization cost summary
CREATE OR REPLACE VIEW v_hospitalization_cost AS
SELECT h.id AS hospitalization_id, h.hospital_no, p.name AS patient_name,
       d.name AS department_name, h.admission_date, h.discharge_date, h.status,
       COALESCE(dep.total_deposit, 0) AS total_deposit,
       COALESCE(chg.total_charge, 0) AS total_charge,
       COALESCE(dep.total_deposit, 0) - COALESCE(chg.total_charge, 0) AS balance
FROM hospitalization h
JOIN patient p ON h.patient_id = p.id
JOIN ward w ON h.ward_id = w.id
JOIN department d ON w.department_id = d.id
LEFT JOIN (SELECT hospitalization_id, SUM(amount) AS total_deposit FROM deposit GROUP BY hospitalization_id) dep ON dep.hospitalization_id = h.id
LEFT JOIN (SELECT hospitalization_id, SUM(total_fee) AS total_charge FROM daily_charge GROUP BY hospitalization_id) chg ON chg.hospitalization_id = h.id;

-- View 4: Patient outpatient visit history
CREATE OR REPLACE VIEW v_patient_outpatient_history AS
SELECT p.patient_no, p.name AS patient_name, r.registration_date, r.time_slot, r.visit_type,
       doc.name AS doctor_name, doc.title AS doctor_title,
       ov.visit_time, ov.diagnosis, ov.symptoms, ov.consultation_fee,
       pr.total_drug_price,
       (COALESCE(ov.consultation_fee, 0) + COALESCE(pr.total_drug_price, 0)) AS total_cost
FROM patient p
JOIN registration r ON r.patient_id = p.id
JOIN doctor doc ON r.doctor_id = doc.id
LEFT JOIN outpatient_visit ov ON ov.registration_id = r.id
LEFT JOIN prescription pr ON pr.visit_id = ov.id;
