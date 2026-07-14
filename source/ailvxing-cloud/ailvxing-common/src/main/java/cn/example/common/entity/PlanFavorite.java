package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlanFavorite {

    private Long id;
    private Long userId;
    private Long planId;
    private String planTitle;
    private LocalDateTime createTime;
}