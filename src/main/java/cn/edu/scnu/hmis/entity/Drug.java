package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Drug {
    private Long id;
    private String drugNo;
    private String name;
    private String specification;
    private String unit;
    private BigDecimal unitPrice;
}
