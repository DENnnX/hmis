package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Registration {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDate registrationDate;
    private String timeSlot;
    private String visitType;
    private String status;
}
