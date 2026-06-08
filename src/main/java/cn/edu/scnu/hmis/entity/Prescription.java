package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Prescription {
    private Long id;
    private Long visitId;
    private BigDecimal totalDrugPrice;
}
