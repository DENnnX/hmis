package cn.edu.scnu.hmis.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Schedule {
    private Long id;
    private Long doctorId;
    private LocalDate scheduleDate;
    private String timeSlot;
    private String scheduleType;
}
