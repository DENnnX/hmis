package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface RegistrationMapper {

    List<Registration> findAll();

    Registration findById(@Param("id") Long id);

    int insert(Registration registration);

    List<Registration> findByPatientId(@Param("patientId") Long patientId);

    List<Registration> findByDoctorAndDate(@Param("doctorId") Long doctorId,
                                           @Param("date") LocalDate date);

    List<Registration> findByDoctorId(@Param("doctorId") Long doctorId);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    List<Map<String, Object>> findPatientHistory(@Param("patientId") Long patientId);

    List<Map<String, Object>> findPatientTreatmentSummary();

    List<Map<String, Object>> findPatientTreatmentDetail(@Param("patientId") Long patientId);
}
