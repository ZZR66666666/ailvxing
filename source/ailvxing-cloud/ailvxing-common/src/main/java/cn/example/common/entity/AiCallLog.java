package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiCallLog {

    private Long id;
    private Long userId;
    private Long planId;
    private String model;
    private String prompt;
    private String response;
    private Integer tokenCount;
    private Integer durationMs;
    private Integer isSuccess;
    private String errorMsg;
    private LocalDateTime createTime;
}