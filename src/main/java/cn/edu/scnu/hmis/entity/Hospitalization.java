package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Hospitalization {
    private Long id;
    private String hospitalNo;
    private Long patientId;
    private Long attendingDoctorId;
    private Long wardId;
    private Long bedId;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String status;
}
