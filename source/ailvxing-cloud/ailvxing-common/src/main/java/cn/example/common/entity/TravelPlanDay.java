package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TravelPlanDay {

    private Long id;
    private Long planId;
    private Integer dayIndex;
    private LocalDate date;
    private String summary;
    private LocalDateTime createTime;
}