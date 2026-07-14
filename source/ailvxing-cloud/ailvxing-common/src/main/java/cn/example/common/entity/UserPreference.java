package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPreference {

    private Long id;
    private Long userId;
    private String budgetLevel;
    private String interestTags;
    private String dietaryPreference;
    private String transportPreference;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}