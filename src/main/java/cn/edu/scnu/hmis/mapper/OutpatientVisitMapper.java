package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.OutpatientVisit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OutpatientVisitMapper {

    List<OutpatientVisit> findAll();

    OutpatientVisit findById(@Param("id") Long id);

    int insert(OutpatientVisit outpatientVisit);

    List<OutpatientVisit> findByRegistrationId(@Param("registrationId") Long registrationId);

    List<OutpatientVisit> findByPatientId(@Param("patientId") Long patientId);
}
