package cn.edu.scnu.hmis.controller;

import cn.edu.scnu.hmis.common.ApiResponse;
import cn.edu.scnu.hmis.entity.Bed;
import cn.edu.scnu.hmis.entity.Department;
import cn.edu.scnu.hmis.entity.Doctor;
import cn.edu.scnu.hmis.entity.DoctorFee;
import cn.edu.scnu.hmis.entity.Drug;
import cn.edu.scnu.hmis.entity.Patient;
import cn.edu.scnu.hmis.entity.Schedule;
import cn.edu.scnu.hmis.entity.Ward;
import cn.edu.scnu.hmis.service.AdminService;
import cn.edu.scnu.hmis.service.ScheduleService;
import cn.edu.scnu.hmis.service.StatisticsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final ScheduleService scheduleService;
    private final StatisticsService statisticsService;
    private final cn.edu.scnu.hmis.service.InpatientService inpatientService;

    public AdminController(AdminService adminService, ScheduleService scheduleService,
                           StatisticsService statisticsService, cn.edu.scnu.hmis.service.InpatientService inpatientService) {
        this.adminService = adminService;
        this.scheduleService = scheduleService;
        this.statisticsService = statisticsService;
        this.inpatientService = inpatientService;
    }

    // ========== Department ==========

    @GetMapping("/departments")
    public ApiResponse<List<Department>> listDepartments() {
        return ApiResponse.ok(adminService.listDepartments());
    }

    @PostMapping("/departments")
    public ApiResponse<Integer> addDepartment(@RequestBody Department d) {
        return ApiResponse.ok(adminService.addDepartment(d));
    }

    @PutMapping("/departments")
    public ApiResponse<Integer> updateDepartment(@RequestBody Department d) {
        return ApiResponse.ok(adminService.updateDepartment(d));
    }

    @DeleteMapping("/departments/{id}")
    public ApiResponse<Integer> deleteDepartment(@PathVariable Long id) {
        return ApiResponse.ok(adminService.deleteDepartment(id));
    }

    // ========== Doctor ==========

    @GetMapping("/doctors")
    public ApiResponse<List<Doctor>> listDoctors() {
        return ApiResponse.ok(adminService.listDoctors());
    }

    @PostMapping("/doctors")
    public ApiResponse<Integer> addDoctor(@RequestBody Doctor d) {
        return ApiResponse.ok(adminService.addDoctor(d));
    }

    @PutMapping("/doctors")
    public ApiResponse<Integer> updateDoctor(@RequestBody Doctor d) {
        return ApiResponse.ok(adminService.updateDoctor(d));
    }

    @DeleteMapping("/doctors/{id}")
    public ApiResponse<Integer> deleteDoctor(@PathVariable Long id) {
        return ApiResponse.ok(adminService.deleteDoctor(id));
    }

    // ========== Drug ==========

    @GetMapping("/drugs")
    public ApiResponse<List<Drug>> listDrugs() {
        return ApiResponse.ok(adminService.listDrugs());
    }

    @PostMapping("/drugs")
    public ApiResponse<Integer> addDrug(@RequestBody Drug d) {
        return ApiResponse.ok(adminService.addDrug(d));
    }

    @PutMapping("/drugs")
    public ApiResponse<Integer> updateDrug(@RequestBody Drug d) {
        return ApiResponse.ok(adminService.updateDrug(d));
    }

    @DeleteMapping("/drugs/{id}")
    public ApiResponse<Integer> deleteDrug(@PathVariable Long id) {
        return ApiResponse.ok(adminService.deleteDrug(id));
    }

    // ========== DoctorFee ==========

    @GetMapping("/doctor-fees")
    public ApiResponse<List<DoctorFee>> listDoctorFees() {
        return ApiResponse.ok(adminService.listDoctorFees());
    }

    @PutMapping("/doctor-fees")
    public ApiResponse<Integer> updateDoctorFee(@RequestBody DoctorFee f) {
        return ApiResponse.ok(adminService.updateDoctorFee(f));
    }

    // ========== Ward ==========

    @GetMapping("/wards")
    public ApiResponse<List<Ward>> listWards() {
        return ApiResponse.ok(adminService.listWards());
    }

    @PostMapping("/wards")
    public ApiResponse<Integer> addWard(@RequestBody Ward w) {
        return ApiResponse.ok(adminService.addWard(w));
    }

    // ========== Bed ==========

    @GetMapping("/beds")
    public ApiResponse<List<Bed>> listBeds() {
        return ApiResponse.ok(adminService.listBeds());
    }

    @PostMapping("/beds")
    public ApiResponse<Integer> addBed(@RequestBody Bed b) {
        return ApiResponse.ok(adminService.addBed(b));
    }

    // ========== Patient ==========

    @GetMapping("/patients")
    public ApiResponse<List<Patient>> listPatients() {
        return ApiResponse.ok(adminService.listPatients());
    }

    @PostMapping("/patients")
    public ApiResponse<Integer> addPatient(@RequestBody Patient p) {
        return ApiResponse.ok(adminService.addPatient(p));
    }

    @PutMapping("/patients")
    public ApiResponse<Integer> updatePatient(@RequestBody Patient p) {
        return ApiResponse.ok(adminService.updatePatient(p));
    }

    @DeleteMapping("/patients/{id}")
    public ApiResponse<Integer> deletePatient(@PathVariable Long id) {
        return ApiResponse.ok(adminService.deletePatient(id));
    }

    // ========== Schedule ==========

    @GetMapping("/schedules")
    public ApiResponse<List<Schedule>> listSchedules() {
        return ApiResponse.ok(scheduleService.listAll());
    }

    @PostMapping("/schedules")
    public ApiResponse<Integer> addSchedule(@RequestBody Schedule s) {
        return ApiResponse.ok(scheduleService.addSchedule(s));
    }

    @DeleteMapping("/schedules/{id}")
    public ApiResponse<Integer> deleteSchedule(@PathVariable Long id) {
        return ApiResponse.ok(scheduleService.deleteSchedule(id));
    }

    // ========== Statistics ==========

    @GetMapping("/statistics/schedule")
    public ApiResponse<List<Map<String, Object>>> departmentSchedule(@RequestParam String date) {
        return ApiResponse.ok(statisticsService.departmentSchedule(date));
    }

    @GetMapping("/statistics/workload")
    public ApiResponse<List<Map<String, Object>>> doctorWorkload() {
        return ApiResponse.ok(statisticsService.doctorWorkload());
    }

    @GetMapping("/statistics/hospitalization-cost")
    public ApiResponse<List<Map<String, Object>>> hospitalizationCost() {
        return ApiResponse.ok(statisticsService.hospitalizationCost());
    }

    @GetMapping("/statistics/patient-treatment")
    public ApiResponse<List<Map<String, Object>>> patientTreatmentSummary() {
        return ApiResponse.ok(statisticsService.patientTreatmentSummary());
    }

    @GetMapping("/statistics/patient-treatment/detail")
    public ApiResponse<List<Map<String, Object>>> patientTreatmentDetail(@RequestParam Long patientId) {
        return ApiResponse.ok(statisticsService.patientTreatmentDetail(patientId));
    }

    @GetMapping("/patient-hospitalizations")
    public ApiResponse<List<cn.edu.scnu.hmis.entity.Hospitalization>> patientHospitalizations(@RequestParam Long patientId) {
        return ApiResponse.ok(inpatientService.getHospitalizationsByPatient(patientId));
    }
}
