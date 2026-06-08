package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.InpatientPrescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InpatientPrescriptionMapper {

    List<InpatientPrescription> findAll();

    InpatientPrescription findById(@Param("id") Long id);

    int insert(InpatientPrescription inpatientPrescription);

    List<InpatientPrescription> findByRecordId(@Param("recordId") Long recordId);
}
