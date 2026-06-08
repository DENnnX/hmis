package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InpatientPrescriptionItem {
    private Long id;
    private Long prescriptionId;
    private Long drugId;
    private Integer quantity;
    private String dosage;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
