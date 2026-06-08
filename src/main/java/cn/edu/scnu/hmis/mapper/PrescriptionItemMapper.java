package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.PrescriptionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PrescriptionItemMapper {

    List<PrescriptionItem> findAll();

    PrescriptionItem findById(@Param("id") Long id);

    int insert(PrescriptionItem prescriptionItem);

    List<PrescriptionItem> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

    List<Map<String, Object>> findDetailByPrescriptionId(@Param("prescriptionId") Long prescriptionId);
}
