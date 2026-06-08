package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Hospitalization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface HospitalizationMapper {

    List<Hospitalization> findAll();

    Hospitalization findById(@Param("id") Long id);

    int insert(Hospitalization hospitalization);

    int update(Hospitalization hospitalization);

    List<Hospitalization> findByPatientId(@Param("patientId") Long patientId);

    List<Hospitalization> findActive();

    List<Hospitalization> findByDoctorId(@Param("doctorId") Long doctorId);

    List<Map<String, Object>> findCostSummary();
}
