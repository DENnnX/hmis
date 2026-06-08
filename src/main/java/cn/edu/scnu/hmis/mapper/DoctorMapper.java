package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DoctorMapper {

    List<Doctor> findAll();

    Doctor findById(@Param("id") Long id);

    int insert(Doctor doctor);

    int update(Doctor doctor);

    int deleteById(@Param("id") Long id);

    List<Doctor> findByDepartmentId(@Param("departmentId") Long departmentId);

    List<Map<String, Object>> findWorkload();
}
