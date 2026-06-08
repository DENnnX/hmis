package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.InpatientPrescriptionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InpatientPrescriptionItemMapper {

    List<InpatientPrescriptionItem> findAll();

    InpatientPrescriptionItem findById(@Param("id") Long id);

    int insert(InpatientPrescriptionItem inpatientPrescriptionItem);

    List<InpatientPrescriptionItem> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);
}
