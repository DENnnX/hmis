package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HospitalizationRecord {
    private Long id;
    private Long hospitalizationId;
    private LocalDate recordDate;
    private String conditionDesc;
    private String treatmentPlan;
}
