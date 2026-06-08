package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyCharge {
    private Long id;
    private Long hospitalizationId;
    private LocalDate chargeDate;
    private BigDecimal bedFee;
    private BigDecimal drugFee;
    private BigDecimal treatmentFee;
    private BigDecimal totalFee;
}
