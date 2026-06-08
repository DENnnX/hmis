package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.DoctorFee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DoctorFeeMapper {

    List<DoctorFee> findAll();

    DoctorFee findById(@Param("id") Long id);

    int insert(DoctorFee doctorFee);

    int update(DoctorFee doctorFee);

    int deleteById(@Param("id") Long id);

    DoctorFee findByTitle(@Param("title") String title);
}
