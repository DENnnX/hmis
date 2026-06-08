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
public class OutpatientService {

    private final RegistrationMapper registrationMapper;
    private final OutpatientVisitMapper outpatientVisitMapper;
    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionItemMapper prescriptionItemMapper;
    private final PaymentMapper paymentMapper;
    private final DoctorFeeMapper doctorFeeMapper;
    private final DoctorMapper doctorMapper;
    private final DrugMapper drugMapper;

    public OutpatientService(RegistrationMapper registrationMapper,
                             OutpatientVisitMapper outpatientVisitMapper,
                             PrescriptionMapper prescriptionMapper,
                             PrescriptionItemMapper prescriptionItemMapper,
                             PaymentMapper paymentMapper,
                             DoctorFeeMapper doctorFeeMapper,
                             DoctorMapper doctorMapper,
                             DrugMapper drugMapper) {
        this.registrationMapper = registrationMapper;
        this.outpatientVisitMapper = outpatientVisitMapper;
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionItemMapper = prescriptionItemMapper;
        this.paymentMapper = paymentMapper;
        this.doctorFeeMapper = doctorFeeMapper;
        this.doctorMapper = doctorMapper;
        this.drugMapper = drugMapper;
    }

    @Transactional
    public Registration register(Long patientId, Long doctorId, String date, String timeSlot, String visitType) {
        // 1. Validate doctor exists
        Doctor doctor = doctorMapper.findById(doctorId);
        if (doctor == null) {
            throw new RuntimeException("Doctor not found: id=" + doctorId);
        }

        // 2. Create Registration (no fee, directly WAITING)
        Registration registration = new Registration();
        registration.setPatientId(patientId);
        registration.setDoctorId(doctorId);
        registration.setRegistrationDate(LocalDate.parse(date));
        registration.setTimeSlot(timeSlot);
        registration.setVisitType(visitType);
        registration.setStatus("WAITING");
        registrationMapper.insert(registration);

        // 3. Return Registration
        return registration;
    }

    @Transactional
    public int pay(Long paymentId, String payMethod) {
        int rows = paymentMapper.updateStatus(paymentId, "PAID", LocalDateTime.now(), payMethod);
        // C1: If this payment is for REGISTRATION, update Registration status to WAITING
        Payment payment = paymentMapper.findById(paymentId);
        if (payment != null && "REGISTRATION".equals(payment.getType())) {
            registrationMapper.updateStatus(payment.getReferenceId(), "WAITING");
        }
        return rows;
    }

    @Transactional
    public OutpatientVisit visit(Long registrationId, String diagnosis, String symptoms) {
        // 1. Get Registration -> get Doctor -> get DoctorFee
        Registration registration = registrationMapper.findById(registrationId);
        if (registration == null) {
            throw new RuntimeException("Registration not found: id=" + registrationId);
        }
        // C3: Validate registration status is WAITING
        if (!"WAITING".equals(registration.getStatus())) {
            throw new RuntimeException("挂号状态异常，无法就诊");
        }
        Doctor doctor = doctorMapper.findById(registration.getDoctorId());
        if (doctor == null) {
            throw new RuntimeException("Doctor not found: id=" + registration.getDoctorId());
        }
        DoctorFee doctorFee = doctorFeeMapper.findByTitle(doctor.getTitle());
        if (doctorFee == null) {
            throw new RuntimeException("DoctorFee not found for title=" + doctor.getTitle());
        }

        // 2. Create OutpatientVisit
        OutpatientVisit visit = new OutpatientVisit();
        visit.setRegistrationId(registrationId);
        visit.setDoctorId(registration.getDoctorId());
        visit.setPatientId(registration.getPatientId());
        visit.setVisitTime(LocalDateTime.now());
        visit.setDiagnosis(diagnosis);
        visit.setSymptoms(symptoms);
        visit.setConsultationFee(doctorFee.getConsultationFee());
        outpatientVisitMapper.insert(visit);

        // 3. Update Registration status to VISITED
        registrationMapper.updateStatus(registrationId, "VISITED");

        // 4. Create Payment (type=CONSULTATION, status=UNPAID)
        Payment payment = new Payment();
        payment.setPatientId(registration.getPatientId());
        payment.setType("CONSULTATION");
        payment.setReferenceId(visit.getId());
        payment.setAmount(doctorFee.getConsultationFee());
        payment.setStatus("UNPAID");
        paymentMapper.insert(payment);

        // 5. Return OutpatientVisit
        return visit;
    }

    @Transactional
    public Prescription prescribe(Long visitId, List<PrescriptionItem> items) {
        // C2: Validate visit exists
        OutpatientVisit existingVisit = outpatientVisitMapper.findById(visitId);
        if (existingVisit == null) {
            throw new RuntimeException("OutpatientVisit not found: id=" + visitId);
        }

        // Validate items and look up unitPrice from drug table
        if (items != null) {
            for (PrescriptionItem item : items) {
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

        // 1. Create Prescription (visitId, totalDrugPrice=0)
        Prescription prescription = new Prescription();
        prescription.setVisitId(visitId);
        prescription.setTotalDrugPrice(BigDecimal.ZERO);
        prescriptionMapper.insert(prescription);

        // 2. For each item: set prescriptionId, insert
        if (items != null) {
            for (PrescriptionItem item : items) {
                item.setPrescriptionId(prescription.getId());
                prescriptionItemMapper.insert(item);
            }
        }

        // 3. Get OutpatientVisit for patientId (already validated above)
        OutpatientVisit visit = existingVisit;

        // 4. Create Payment (type=DRUG, amount from prescription.totalDrugPrice)
        // Re-fetch prescription to get trigger-calculated totalDrugPrice
        Prescription updated = prescriptionMapper.findById(prescription.getId());
        Payment payment = new Payment();
        payment.setPatientId(visit.getPatientId());
        payment.setType("DRUG");
        payment.setReferenceId(prescription.getId());
        payment.setAmount(updated.getTotalDrugPrice());
        payment.setStatus("UNPAID");
        paymentMapper.insert(payment);

        // 5. Return Prescription
        return updated;
    }

    public List<Payment> getPayments(Long patientId) {
        return paymentMapper.findByPatientId(patientId);
    }

    public List<Payment> getUnpaidPayments(Long patientId) {
        return paymentMapper.findUnpaidByPatientId(patientId);
    }

    public List<Registration> getRegistrationsByPatient(Long patientId) {
        return registrationMapper.findByPatientId(patientId);
    }

    public List<Registration> getRegistrationsByDoctor(Long doctorId) {
        return registrationMapper.findByDoctorId(doctorId);
    }

    @Transactional
    public int cancelRegistration(Long registrationId) {
        Registration reg = registrationMapper.findById(registrationId);
        if (reg == null) {
            throw new RuntimeException("挂号记录不存在: id=" + registrationId);
        }
        if (!"WAITING".equals(reg.getStatus())) {
            throw new RuntimeException("只有候诊中的挂号才能取消");
        }
        return registrationMapper.updateStatus(registrationId, "CANCELLED");
    }
}
