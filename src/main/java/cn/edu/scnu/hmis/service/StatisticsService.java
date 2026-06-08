package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.mapper.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final ScheduleMapper scheduleMapper;
    private final DoctorMapper doctorMapper;
    private final RegistrationMapper registrationMapper;
    private final HospitalizationMapper hospitalizationMapper;

    public StatisticsService(ScheduleMapper scheduleMapper,
                             DoctorMapper doctorMapper,
                             RegistrationMapper registrationMapper,
                             HospitalizationMapper hospitalizationMapper) {
        this.scheduleMapper = scheduleMapper;
        this.doctorMapper = doctorMapper;
        this.registrationMapper = registrationMapper;
        this.hospitalizationMapper = hospitalizationMapper;
    }

    public List<Map<String, Object>> departmentSchedule(String date) {
        return scheduleMapper.findDepartmentSchedule(date);
    }

    public List<Map<String, Object>> doctorWorkload() {
        return doctorMapper.findWorkload();
    }

    public List<Map<String, Object>> hospitalizationCost() {
        return hospitalizationMapper.findCostSummary();
    }

    public List<Map<String, Object>> patientHistory(Long patientId) {
        return registrationMapper.findPatientHistory(patientId);
    }
}
