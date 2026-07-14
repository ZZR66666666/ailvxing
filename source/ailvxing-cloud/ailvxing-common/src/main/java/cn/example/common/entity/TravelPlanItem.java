package cn.example.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class TravelPlanItem {

    private Long id;
    private Long planDayId;
    private String itemType;
    private String name;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal cost;
    private String description;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer sortOrder;
    private LocalDateTime createTime;
}