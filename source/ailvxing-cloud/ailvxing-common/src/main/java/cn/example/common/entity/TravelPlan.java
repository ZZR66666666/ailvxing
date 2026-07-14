package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TravelPlan {

    private Long id;
    private Long userId;
    private String title;
    private String destination;
    private Integer days;
    private java.math.BigDecimal budget;
    private String budgetLevel;
    private String interestTags;
    private String companionIds;
    private String planDetail;
    private String aiModel;
    private Integer status;
    private String shareCode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}