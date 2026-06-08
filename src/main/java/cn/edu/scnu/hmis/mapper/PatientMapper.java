package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PatientMapper {

    List<Patient> findAll();

    Patient findById(@Param("id") Long id);

    int insert(Patient patient);

    int update(Patient patient);

    int deleteById(@Param("id") Long id);
}
