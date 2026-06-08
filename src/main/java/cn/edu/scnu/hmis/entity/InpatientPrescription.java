package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InpatientPrescription {
    private Long id;
    private Long recordId;
    private BigDecimal totalDrugPrice;
}
