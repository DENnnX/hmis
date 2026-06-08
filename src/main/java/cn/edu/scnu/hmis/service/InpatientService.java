package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.*;
import cn.edu.scnu.hmis.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InpatientService {

    private final HospitalizationMapper hospitalizationMapper;
    private final HospitalizationRecordMapper hospitalizationRecordMapper;
    private final InpatientPrescriptionMapper inpatientPrescriptionMapper;
    private final InpatientPrescriptionItemMapper inpatientPrescriptionItemMapper;
    private final DailyChargeMapper dailyChargeMapper;
    private final DepositMapper depositMapper;
    private final DoctorFeeMapper doctorFeeMapper;
    private final DoctorMapper doctorMapper;
    private final WardMapper wardMapper;
    private final DrugMapper drugMapper;

    public InpatientService(HospitalizationMapper hospitalizationMapper,
                            HospitalizationRecordMapper hospitalizationRecordMapper,
                            InpatientPrescriptionMapper inpatientPrescriptionMapper,
                            InpatientPrescriptionItemMapper inpatientPrescriptionItemMapper,
                            DailyChargeMapper dailyChargeMapper,
                            DepositMapper depositMapper,
                            DoctorFeeMapper doctorFeeMapper,
                            DoctorMapper doctorMapper,
                            WardMapper wardMapper,
                            DrugMapper drugMapper) {
        this.hospitalizationMapper = hospitalizationMapper;
        this.hospitalizationRecordMapper = hospitalizationRecordMapper;
        this.inpatientPrescriptionMapper = inpatientPrescriptionMapper;
        this.inpatientPrescriptionItemMapper = inpatientPrescriptionItemMapper;
        this.dailyChargeMapper = dailyChargeMapper;
        this.depositMapper = depositMapper;
        this.doctorFeeMapper = doctorFeeMapper;
        this.doctorMapper = doctorMapper;
        this.wardMapper = wardMapper;
        this.drugMapper = drugMapper;
    }

    @Transactional
    public Hospitalization admit(Long patientId, Long doctorId, Long wardId, Long bedId) {
        // 1. Create Hospitalization
        Hospitalization hospitalization = new Hospitalization();
        hospitalization.setHospitalNo("H" + System.currentTimeMillis());
        hospitalization.setPatientId(patientId);
        hospitalization.setAttendingDoctorId(doctorId);
        hospitalization.setWardId(wardId);
        hospitalization.setBedId(bedId);
        hospitalization.setAdmissionDate(LocalDate.now());
        hospitalization.setStatus("IN_HOSPITAL");

        // 2. Insert -> trigger auto-occupies bed
        hospitalizationMapper.insert(hospitalization);

        // 3. Return Hospitalization
        return hospitalization;
    }

    @Transactional
    public Deposit deposit(Long hospitalizationId, BigDecimal amount) {
        // 1. Create Deposit
        Deposit deposit = new Deposit();
        deposit.setHospitalizationId(hospitalizationId);
        deposit.setAmount(amount);
        deposit.setDepositTime(java.time.LocalDateTime.now());

        // 2. Insert and return
        depositMapper.insert(deposit);
        return deposit;
    }

    @Transactional
    public HospitalizationRecord dailyRound(Long hospitalizationId,
                                            String conditionDesc,
                                            String treatmentPlan,
                                            List<InpatientPrescriptionItem> items) {
        // C2: Validate hospitalization exists
        Hospitalization hospitalization = hospitalizationMapper.findById(hospitalizationId);
        if (hospitalization == null) {
            throw new RuntimeException("Hospitalization not found: id=" + hospitalizationId);
        }
        // I4: Validate status is IN_HOSPITAL
        if (!"IN_HOSPITAL".equals(hospitalization.getStatus())) {
            throw new RuntimeException("该住院记录已出院，无法查房");
        }

        // I10: Validate items and look up unitPrice from drug table
        if (items != null && !items.isEmpty()) {
            for (InpatientPrescriptionItem item : items) {
                if (item.getDrugId() == null || item.getQuantity() == null || item.getDosage() == null) {
                    throw new RuntimeException("处方明细字段不完整");
                }
                Drug drug = drugMapper.findById(item.getDrugId());
                if (drug == null) {
                    throw new RuntimeException("药品不存在: id=" + item.getDrugId());
                }
                item.setUnitPrice(drug.getUnitPrice());
                item.setSubtotal(drug.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        // 1. Create HospitalizationRecord (allow multiple per day now)
        HospitalizationRecord record = new HospitalizationRecord();
        record.setHospitalizationId(hospitalizationId);
        record.setRecordDate(LocalDate.now());
        record.setConditionDesc(conditionDesc);
        record.setTreatmentPlan(treatmentPlan);
        hospitalizationRecordMapper.insert(record);

        // 2. If items not empty, create prescription
        BigDecimal drugFee = BigDecimal.ZERO;
        if (items != null && !items.isEmpty()) {
            InpatientPrescription prescription = new InpatientPrescription();
            prescription.setRecordId(record.getId());
            prescription.setTotalDrugPrice(BigDecimal.ZERO);
            inpatientPrescriptionMapper.insert(prescription);

            for (InpatientPrescriptionItem item : items) {
                item.setPrescriptionId(prescription.getId());
                inpatientPrescriptionItemMapper.insert(item);
            }

            InpatientPrescription updatedPrescription = inpatientPrescriptionMapper.findById(prescription.getId());
            drugFee = updatedPrescription.getTotalDrugPrice();
        }

        // 3. Check if this is the first submission today
        HospitalizationRecord existingToday = hospitalizationRecordMapper
                .findByHospitalizationAndDate(hospitalizationId, LocalDate.now());
        // existingToday is the record we just inserted (since we inserted before this check)
        // Count records for today — if > 1, this is a subsequent submission
        List<HospitalizationRecord> todayRecords = hospitalizationRecordMapper.findByHospitalizationId(hospitalizationId)
                .stream().filter(r -> LocalDate.now().equals(r.getRecordDate())).toList();
        boolean isFirstToday = todayRecords.size() <= 1;

        // 4. Create DailyCharge: first submission = bed+treatment+drug, subsequent = drug only
        BigDecimal bedFee = BigDecimal.ZERO;
        BigDecimal treatmentFee = BigDecimal.ZERO;
        if (isFirstToday) {
            Doctor doctor = doctorMapper.findById(hospitalization.getAttendingDoctorId());
            if (doctor == null) throw new RuntimeException("Doctor not found");
            DoctorFee doctorFee = doctorFeeMapper.findByTitle(doctor.getTitle());
            if (doctorFee == null) throw new RuntimeException("DoctorFee not found");
            Ward ward = wardMapper.findById(hospitalization.getWardId());
            if (ward == null) throw new RuntimeException("Ward not found");
            bedFee = ward.getDailyFee();
            treatmentFee = doctorFee.getConsultationFee();
        }

        BigDecimal todayCost = bedFee.add(treatmentFee).add(drugFee);
        if (todayCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentBalance = getBalance(hospitalizationId);
            if (currentBalance.compareTo(todayCost) < 0) {
                throw new RuntimeException("患者账户余额不足，请及时通知患者补缴住院押金");
            }

            DailyCharge dailyCharge = new DailyCharge();
            dailyCharge.setHospitalizationId(hospitalizationId);
            dailyCharge.setChargeDate(LocalDate.now());
            dailyCharge.setBedFee(bedFee);
            dailyCharge.setDrugFee(drugFee);
            dailyCharge.setTreatmentFee(treatmentFee);
            dailyChargeMapper.insert(dailyCharge);
        }

        return record;
    }

    @Transactional
    public java.util.Map<String, Object> discharge(Long hospitalizationId) {
        // 1. Get Hospitalization
        Hospitalization hospitalization = hospitalizationMapper.findById(hospitalizationId);
        if (hospitalization == null) {
            throw new RuntimeException("Hospitalization not found: id=" + hospitalizationId);
        }
        // I3: Validate status is IN_HOSPITAL
        if (!"IN_HOSPITAL".equals(hospitalization.getStatus())) {
            throw new RuntimeException("该住院记录已出院");
        }

        // 2.核算余额
        BigDecimal refund = getBalance(hospitalizationId);

        // 3. Set dischargeDate=today, status=DISCHARGED
        hospitalization.setDischargeDate(LocalDate.now());
        hospitalization.setStatus("DISCHARGED");

        // 4. Update -> trigger auto-releases bed
        int rows = hospitalizationMapper.update(hospitalization);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("rows", rows);
        result.put("refund", refund);
        return result;
    }

    public BigDecimal getBalance(Long hospitalizationId) {
        BigDecimal deposit = depositMapper.sumByHospitalizationId(hospitalizationId);
        BigDecimal charge = dailyChargeMapper.sumByHospitalizationId(hospitalizationId);
        return (deposit != null ? deposit : BigDecimal.ZERO)
                .subtract(charge != null ? charge : BigDecimal.ZERO);
    }

    public List<HospitalizationRecord> getRecords(Long hospitalizationId) {
        return hospitalizationRecordMapper.findByHospitalizationId(hospitalizationId);
    }

    public List<Hospitalization> getHospitalizationsByPatient(Long patientId) {
        return hospitalizationMapper.findByPatientId(patientId);
    }

    public List<Hospitalization> getHospitalizationsByDoctor(Long doctorId) {
        return hospitalizationMapper.findByDoctorId(doctorId);
    }

    public List<java.util.Map<String, Object>> getPrescriptionItemsByRecordId(Long recordId) {
        return inpatientPrescriptionItemMapper.findByRecordId(recordId);
    }
}
