USE hospital;

-- Doctor department lookup
CREATE INDEX idx_doctor_department ON doctor(department_id);

-- Schedule queries by date
CREATE INDEX idx_schedule_date ON schedule(schedule_date);

-- Registration lookups
CREATE INDEX idx_registration_patient ON registration(patient_id);
CREATE INDEX idx_registration_doctor ON registration(doctor_id);
CREATE INDEX idx_registration_date ON registration(registration_date);

-- Outpatient visit patient lookup
CREATE INDEX idx_outpatient_visit_patient ON outpatient_visit(patient_id);

-- Prescription item lookup
CREATE INDEX idx_prescription_item_prescription ON prescription_item(prescription_id);

-- Payment lookups
CREATE INDEX idx_payment_patient ON payment(patient_id);
CREATE INDEX idx_payment_status ON payment(status);

-- Hospitalization lookups
CREATE INDEX idx_hospitalization_patient ON hospitalization(patient_id);
CREATE INDEX idx_hospitalization_status ON hospitalization(status);

-- Daily charge and deposit lookups
CREATE INDEX idx_deposit_hospitalization ON deposit(hospitalization_id);

-- Inpatient prescription item lookups
CREATE INDEX idx_inpatient_prescription_item_prescription ON inpatient_prescription_item(prescription_id);
CREATE INDEX idx_inpatient_prescription_item_drug ON inpatient_prescription_item(drug_id);

-- Inpatient prescription record lookup
CREATE INDEX idx_inpatient_prescription_record ON inpatient_prescription(record_id);

-- Hospitalization doctor and bed lookups
CREATE INDEX idx_hospitalization_attending_doctor ON hospitalization(attending_doctor_id);
CREATE INDEX idx_hospitalization_bed ON hospitalization(bed_id);

-- Ward department lookup
CREATE INDEX idx_ward_department ON ward(department_id);
