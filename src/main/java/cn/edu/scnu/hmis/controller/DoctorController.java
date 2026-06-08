package cn.edu.scnu.hmis.controller;

import cn.edu.scnu.hmis.common.ApiResponse;
import cn.edu.scnu.hmis.entity.Hospitalization;
import cn.edu.scnu.hmis.entity.HospitalizationRecord;
import cn.edu.scnu.hmis.entity.InpatientPrescriptionItem;
import cn.edu.scnu.hmis.entity.OutpatientVisit;
import cn.edu.scnu.hmis.entity.Prescription;
import cn.edu.scnu.hmis.entity.PrescriptionItem;
import cn.edu.scnu.hmis.entity.Registration;
import cn.edu.scnu.hmis.entity.Schedule;
import cn.edu.scnu.hmis.service.InpatientService;
import cn.edu.scnu.hmis.service.OutpatientService;
import cn.edu.scnu.hmis.service.ScheduleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public ApiResponse<List<Schedule>> getSchedule(@RequestParam Long doctorId,
                                                   @RequestParam(required = false) String date) {
        LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        return ApiResponse.ok(scheduleService.findByDoctorAndDate(doctorId, localDate));
    }

    @GetMapping("/registrations")
    public ApiResponse<List<Registration>> getRegistrations(@RequestParam Long doctorId) {
        return ApiResponse.ok(outpatientService.getRegistrationsByDoctor(doctorId));
    }

    @GetMapping("/hospitalizations")
    public ApiResponse<List<Hospitalization>> getHospitalizations(@RequestParam Long doctorId) {
        return ApiResponse.ok(inpatientService.getHospitalizationsByDoctor(doctorId));
    }

    @PostMapping("/visit")
    public ApiResponse<OutpatientVisit> visit(@RequestParam Long registrationId,
                                              @RequestParam String diagnosis,
                                              @RequestParam(required = false) String symptoms) {
        return ApiResponse.ok(outpatientService.visit(registrationId, diagnosis, symptoms));
    }

    @PostMapping("/prescribe")
    public ApiResponse<Prescription> prescribe(@RequestParam Long visitId,
                                               @RequestBody List<PrescriptionItem> items) {
        return ApiResponse.ok(outpatientService.prescribe(visitId, items));
    }

    @PostMapping("/hospitalization/round")
    public ApiResponse<HospitalizationRecord> dailyRound(
            @RequestParam Long hospitalizationId,
            @RequestParam String conditionDesc,
            @RequestParam String treatmentPlan,
            @RequestBody(required = false) List<InpatientPrescriptionItem> items) {
        return ApiResponse.ok(inpatientService.dailyRound(hospitalizationId, conditionDesc, treatmentPlan, items));
    }

    @GetMapping("/hospitalization/prescription-items")
    public ApiResponse<List<Map<String, Object>>> getRecordPrescriptionItems(@RequestParam Long recordId) {
        return ApiResponse.ok(inpatientService.getPrescriptionItemsByRecordId(recordId));
    }

    @GetMapping("/visit-history")
    public ApiResponse<List<Map<String, Object>>> getVisitHistory(@RequestParam Long doctorId) {
        return ApiResponse.ok(outpatientService.getVisitHistoryByDoctorId(doctorId));
    }
}
