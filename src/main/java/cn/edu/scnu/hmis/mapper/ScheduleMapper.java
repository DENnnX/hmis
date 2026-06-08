package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ScheduleMapper {

    List<Schedule> findAll();

    Schedule findById(@Param("id") Long id);

    int insert(Schedule schedule);

    int update(Schedule schedule);

    int deleteById(@Param("id") Long id);

    List<Schedule> findByDoctorAndDate(@Param("doctorId") Long doctorId,
                                       @Param("scheduleDate") LocalDate scheduleDate);

    List<Schedule> findByDate(@Param("scheduleDate") LocalDate scheduleDate);

    List<Map<String, Object>> findDepartmentSchedule(@Param("date") String date);

    List<Map<String, Object>> findFutureOutpatient();
}
