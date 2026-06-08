package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Deposit {
    private Long id;
    private Long hospitalizationId;
    private BigDecimal amount;
    private LocalDateTime depositTime;
}
