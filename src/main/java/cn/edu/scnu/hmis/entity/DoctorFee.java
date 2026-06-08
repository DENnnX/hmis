package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoctorFee {
    private Long id;
    private String title;
    private BigDecimal consultationFee;
}
