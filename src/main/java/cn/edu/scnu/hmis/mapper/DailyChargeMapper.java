package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.DailyCharge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface DailyChargeMapper {

    List<DailyCharge> findAll();

    DailyCharge findById(@Param("id") Long id);

    int insert(DailyCharge dailyCharge);

    List<DailyCharge> findByHospitalizationId(@Param("hospitalizationId") Long hospitalizationId);

    BigDecimal sumByHospitalizationId(@Param("hospitalizationId") Long hospitalizationId);
}
