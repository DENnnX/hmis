package cn.edu.scnu.hmis.entity;

import lombok.Data;

@Data
public class Bed {
    private Long id;
    private Long wardId;
    private String bedNo;
    private String status;
}
