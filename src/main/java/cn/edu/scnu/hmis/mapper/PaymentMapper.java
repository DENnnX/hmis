package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaymentMapper {

    List<Payment> findAll();

    Payment findById(@Param("id") Long id);

    int insert(Payment payment);

    List<Payment> findByPatientId(@Param("patientId") Long patientId);

    List<Payment> findUnpaidByPatientId(@Param("patientId") Long patientId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("payTime") LocalDateTime payTime,
                     @Param("payMethod") String payMethod);
}
