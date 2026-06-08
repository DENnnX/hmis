package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Long id;
    private Long patientId;
    private String type;
    private Long referenceId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime payTime;
    private String payMethod;
}
