package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.HospitalizationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HospitalizationRecordMapper {

    List<HospitalizationRecord> findAll();

    HospitalizationRecord findById(@Param("id") Long id);

    int insert(HospitalizationRecord hospitalizationRecord);

    List<HospitalizationRecord> findByHospitalizationId(@Param("hospitalizationId") Long hospitalizationId);

    HospitalizationRecord findByHospitalizationAndDate(@Param("hospitalizationId") Long hospitalizationId,
                                                       @Param("recordDate") java.time.LocalDate recordDate);
}
