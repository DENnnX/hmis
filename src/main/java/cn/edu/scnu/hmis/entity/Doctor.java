package cn.edu.scnu.hmis.entity;

import lombok.Data;

@Data
public class Doctor {
    private Long id;
    private String doctorNo;
    private String name;
    private String gender;
    private String title;
    private String phone;
    private Long departmentId;
}
