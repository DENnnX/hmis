-- HMIS Hospital Management Information System
-- Database Triggers for Business Constraints

USE hospital;

-- ============================================================
-- Trigger 1: trg_schedule_conflict
-- Purpose: Prevent duplicate schedule entries for the same doctor
-- on the same date and time slot (BEFORE INSERT ON schedule)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_schedule_conflict
BEFORE INSERT ON `schedule`
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM `schedule`
        WHERE doctor_id = NEW.doctor_id
          AND schedule_date = NEW.schedule_date
          AND time_slot = NEW.time_slot
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '排班冲突：该医生此时段已排班';
    END IF;
END //

DELIMITER ;

-- ============================================================
-- Trigger 2: trg_prescription_item_subtotal
-- Purpose: Auto-calculate subtotal before inserting a
-- prescription item (BEFORE INSERT ON prescription_item)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_prescription_item_subtotal
BEFORE INSERT ON `prescription_item`
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.quantity * NEW.unit_price;
END //

DELIMITER ;

-- ============================================================
-- Trigger 3: trg_prescription_item_update_total
-- Purpose: Recalculate the total drug price of the parent
-- prescription after inserting a prescription item
-- (AFTER INSERT ON prescription_item)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_prescription_item_update_total
AFTER INSERT ON `prescription_item`
FOR EACH ROW
BEGIN
    UPDATE `prescription`
    SET total_drug_price = (
        SELECT COALESCE(SUM(subtotal), 0)
        FROM `prescription_item`
        WHERE prescription_id = NEW.prescription_id
    )
    WHERE id = NEW.prescription_id;
END //

DELIMITER ;

-- ============================================================
-- Trigger 4: trg_inpatient_prescription_item_subtotal
-- Purpose: Auto-calculate subtotal before inserting an
-- inpatient prescription item
-- (BEFORE INSERT ON inpatient_prescription_item)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_inpatient_prescription_item_subtotal
BEFORE INSERT ON `inpatient_prescription_item`
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.quantity * NEW.unit_price;
END //

DELIMITER ;

-- ============================================================
-- Trigger 5: trg_inpatient_prescription_item_update_total
-- Purpose: Recalculate the total drug price of the parent
-- inpatient prescription after inserting an item
-- (AFTER INSERT ON inpatient_prescription_item)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_inpatient_prescription_item_update_total
AFTER INSERT ON `inpatient_prescription_item`
FOR EACH ROW
BEGIN
    UPDATE `inpatient_prescription`
    SET total_drug_price = (
        SELECT COALESCE(SUM(subtotal), 0)
        FROM `inpatient_prescription_item`
        WHERE prescription_id = NEW.prescription_id
    )
    WHERE id = NEW.prescription_id;
END //

DELIMITER ;

-- ============================================================
-- Trigger 6: trg_hospitalization_bed_occupy
-- Purpose: Mark a bed as OCCUPIED when a new hospitalization
-- record is created (AFTER INSERT ON hospitalization)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_hospitalization_bed_occupy
AFTER INSERT ON `hospitalization`
FOR EACH ROW
BEGIN
    UPDATE `bed`
    SET status = 'OCCUPIED'
    WHERE id = NEW.bed_id;
END //

DELIMITER ;

-- ============================================================
-- Trigger 7: trg_hospitalization_bed_release
-- Purpose: Release a bed (set AVAILABLE) when a patient is
-- discharged (AFTER UPDATE ON hospitalization)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_hospitalization_bed_release
AFTER UPDATE ON `hospitalization`
FOR EACH ROW
BEGIN
    IF NEW.status = 'DISCHARGED' AND OLD.status = 'IN_HOSPITAL' THEN
        UPDATE `bed`
        SET status = 'AVAILABLE'
        WHERE id = NEW.bed_id;
    END IF;
END //

DELIMITER ;

-- ============================================================
-- Trigger 8: trg_check_balance
-- Purpose: Check whether the patient's remaining balance
-- (deposits minus daily charges) is sufficient after each
-- daily charge is inserted. Signals an error if balance < 0.
-- (AFTER INSERT ON daily_charge)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_check_balance
AFTER INSERT ON `daily_charge`
FOR EACH ROW
BEGIN
    DECLARE v_deposit_total  DECIMAL(10,2);
    DECLARE v_charge_total   DECIMAL(10,2);

    SELECT COALESCE(SUM(amount), 0)
    INTO v_deposit_total
    FROM `deposit`
    WHERE hospitalization_id = NEW.hospitalization_id;

    SELECT COALESCE(SUM(total_fee), 0)
    INTO v_charge_total
    FROM `daily_charge`
    WHERE hospitalization_id = NEW.hospitalization_id;

    IF (v_deposit_total - v_charge_total) < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '余额不足，请续缴住院费';
    END IF;
END //

DELIMITER ;

-- ============================================================
-- Trigger 9: trg_daily_charge_total
-- Purpose: Auto-calculate total_fee before inserting a daily
-- charge record (BEFORE INSERT ON daily_charge)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_daily_charge_total
BEFORE INSERT ON `daily_charge`
FOR EACH ROW
BEGIN
    SET NEW.total_fee = NEW.bed_fee + NEW.drug_fee + NEW.treatment_fee;
END //

DELIMITER ;

-- ============================================================
-- Trigger 10: trg_registration_schedule_check
-- Purpose: Verify that the doctor has an OUTPATIENT schedule
-- for the same date and time slot before allowing registration
-- (BEFORE INSERT ON registration)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_registration_schedule_check
BEFORE INSERT ON `registration`
FOR EACH ROW
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM `schedule`
        WHERE doctor_id = NEW.doctor_id
          AND schedule_date = NEW.registration_date
          AND time_slot = NEW.time_slot
          AND schedule_type = 'OUTPATIENT'
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '挂号失败：该医生此时段无门诊排班';
    END IF;
END //

DELIMITER ;

-- ============================================================
-- Trigger 11: trg_hospitalization_bed_ward_check
-- Purpose: Verify that the bed belongs to the specified ward
-- before allowing a hospitalization record to be created
-- (BEFORE INSERT ON hospitalization)
-- ============================================================

DELIMITER //

CREATE TRIGGER trg_hospitalization_bed_ward_check
BEFORE INSERT ON `hospitalization`
FOR EACH ROW
BEGIN
    DECLARE v_ward_id BIGINT;
    SELECT ward_id INTO v_ward_id FROM `bed` WHERE id = NEW.bed_id;
    IF v_ward_id != NEW.ward_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '病床不属于指定病房';
    END IF;
END //

DELIMITER ;
