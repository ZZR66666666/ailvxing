package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlanShare {

    private Long id;
    private Long planId;
    private Long shareUserId;
    private String shareCode;
    private Integer viewCount;
    private LocalDateTime expireTime;
    private Integer status;
    private LocalDateTime createTime;
}