package cn.edu.scnu.hmis.controller;

import cn.edu.scnu.hmis.common.ApiResponse;
import cn.edu.scnu.hmis.entity.Deposit;
import cn.edu.scnu.hmis.entity.Hospitalization;
import cn.edu.scnu.hmis.entity.HospitalizationRecord;
import cn.edu.scnu.hmis.entity.Payment;
import cn.edu.scnu.hmis.entity.Registration;
import cn.edu.scnu.hmis.service.InpatientService;
import cn.edu.scnu.hmis.service.OutpatientService;
import cn.edu.scnu.hmis.service.ScheduleService;
import cn.edu.scnu.hmis.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final OutpatientService outpatientService;
    private final InpatientService inpatientService;
    private final StatisticsService statisticsService;
    private final ScheduleService scheduleService;

    public PatientController(OutpatientService outpatientService,
                             InpatientService inpatientService,
                             StatisticsService statisticsService,
                             ScheduleService scheduleService) {
        this.outpatientService = outpatientService;
        this.inpatientService = inpatientService;
        this.statisticsService = statisticsService;
        this.scheduleService = scheduleService;
    }

    @PostMapping("/register")
    public ApiResponse<Registration> register(@RequestParam Long patientId,
                                              @RequestParam Long doctorId,
                                              @RequestParam String date,
                                              @RequestParam String timeSlot,
                                              @RequestParam String visitType) {
        return ApiResponse.ok(outpatientService.register(patientId, doctorId, date, timeSlot, visitType));
    }

    @GetMapping("/available-schedules")
    public ApiResponse<List<Map<String, Object>>> getAvailableSchedules() {
        return ApiResponse.ok(scheduleService.getFutureOutpatientSchedules());
    }

    @PostMapping("/pay")
    public ApiResponse<Integer> pay(@RequestParam Long paymentId,
                                    @RequestParam String payMethod) {
        return ApiResponse.ok(outpatientService.pay(paymentId, payMethod));
    }

    @GetMapping("/payments")
    public ApiResponse<List<Payment>> getPayments(@RequestParam Long patientId) {
        return ApiResponse.ok(outpatientService.getPayments(patientId));
    }

    @GetMapping("/unpaid")
    public ApiResponse<List<Payment>> getUnpaidPayments(@RequestParam Long patientId) {
        return ApiResponse.ok(outpatientService.getUnpaidPayments(patientId));
    }

    @GetMapping("/registrations")
    public ApiResponse<List<Registration>> getRegistrations(@RequestParam Long patientId) {
        return ApiResponse.ok(outpatientService.getRegistrationsByPatient(patientId));
    }

    @PostMapping("/registrations/cancel")
    public ApiResponse<Integer> cancelRegistration(@RequestParam Long registrationId) {
        return ApiResponse.ok(outpatientService.cancelRegistration(registrationId));
    }

    @GetMapping("/history")
    public ApiResponse<List<Map<String, Object>>> patientHistory(@RequestParam Long patientId) {
        return ApiResponse.ok(statisticsService.patientHistory(patientId));
    }

    @GetMapping("/hospitalization/balance")
    public ApiResponse<BigDecimal> getBalance(@RequestParam Long hospitalizationId) {
        return ApiResponse.ok(inpatientService.getBalance(hospitalizationId));
    }

    @GetMapping("/prescription-detail")
    public ApiResponse<List<Map<String, Object>>> prescriptionDetail(@RequestParam Long prescriptionId) {
        return ApiResponse.ok(outpatientService.getPrescriptionDetailByPrescriptionId(prescriptionId));
    }

    @GetMapping("/hospitalization/prescription-items")
    public ApiResponse<List<Map<String, Object>>> getRecordPrescriptionItems(@RequestParam Long recordId) {
        return ApiResponse.ok(inpatientService.getPrescriptionItemsByRecordId(recordId));
    }

    @GetMapping("/hospitalizations")
    public ApiResponse<List<Hospitalization>> getHospitalizations(@RequestParam Long patientId) {
        return ApiResponse.ok(inpatientService.getHospitalizationsByPatient(patientId));
    }

    @GetMapping("/hospitalization/records")
    public ApiResponse<List<HospitalizationRecord>> getRecords(@RequestParam Long hospitalizationId) {
        return ApiResponse.ok(inpatientService.getRecords(hospitalizationId));
    }

    @PostMapping("/hospitalization/admit")
    public ApiResponse<Hospitalization> admit(@RequestParam Long patientId,
                                              @RequestParam Long doctorId,
                                              @RequestParam Long wardId,
                                              @RequestParam Long bedId) {
        return ApiResponse.ok(inpatientService.admit(patientId, doctorId, wardId, bedId));
    }

    @PostMapping("/hospitalization/deposit")
    public ApiResponse<Deposit> deposit(@RequestParam Long hospitalizationId,
                                        @RequestParam BigDecimal amount) {
        return ApiResponse.ok(inpatientService.deposit(hospitalizationId, amount));
    }

    @PostMapping("/hospitalization/discharge")
    public ApiResponse<Map<String, Object>> discharge(@RequestParam Long hospitalizationId) {
        return ApiResponse.ok(inpatientService.discharge(hospitalizationId));
    }
}
