package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.Schedule;
import cn.edu.scnu.hmis.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;

    public ScheduleService(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    public List<Schedule> listAll() {
        return scheduleMapper.findAll();
    }

    public List<Schedule> findByDate(LocalDate date) {
        return scheduleMapper.findByDate(date);
    }

    public List<Schedule> findByDoctorAndDate(Long doctorId, LocalDate date) {
        return scheduleMapper.findByDoctorAndDate(doctorId, date);
    }

    public int addSchedule(Schedule s) {
        return scheduleMapper.insert(s);
    }

    public int updateSchedule(Schedule s) {
        return scheduleMapper.update(s);
    }

    public int deleteSchedule(Long id) {
        return scheduleMapper.deleteById(id);
    }

    public List<Map<String, Object>> getFutureOutpatientSchedules() {
        return scheduleMapper.findFutureOutpatient();
    }
}
