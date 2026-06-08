package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Patient {
    private Long id;
    private String patientNo;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String address;
    private String phone;
}
