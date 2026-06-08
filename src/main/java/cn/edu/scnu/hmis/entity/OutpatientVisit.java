package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OutpatientVisit {
    private Long id;
    private Long registrationId;
    private Long doctorId;
    private Long patientId;
    private LocalDateTime visitTime;
    private String diagnosis;
    private String symptoms;
    private BigDecimal consultationFee;
}
