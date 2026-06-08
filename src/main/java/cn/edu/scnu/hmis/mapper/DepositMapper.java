package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Deposit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface DepositMapper {

    List<Deposit> findAll();

    Deposit findById(@Param("id") Long id);

    int insert(Deposit deposit);

    List<Deposit> findByHospitalizationId(@Param("hospitalizationId") Long hospitalizationId);

    BigDecimal sumByHospitalizationId(@Param("hospitalizationId") Long hospitalizationId);
}
