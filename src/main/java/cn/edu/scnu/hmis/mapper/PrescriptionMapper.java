package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Prescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PrescriptionMapper {

    List<Prescription> findAll();

    Prescription findById(@Param("id") Long id);

    int insert(Prescription prescription);

    List<Prescription> findByVisitId(@Param("visitId") Long visitId);
}
