package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlanProductRecommend {

    private Long id;
    private Long planId;
    private Long productId;
    private Long planDayId;
    private String matchReason;
    private Integer status;
    private LocalDateTime createTime;
}