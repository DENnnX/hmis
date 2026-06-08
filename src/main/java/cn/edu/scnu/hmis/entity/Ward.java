package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Ward {
    private Long id;
    private String wardNo;
    private String location;
    private BigDecimal dailyFee;
    private Long departmentId;
}
