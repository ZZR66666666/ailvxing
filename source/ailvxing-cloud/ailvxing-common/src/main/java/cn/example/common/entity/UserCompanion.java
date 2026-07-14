package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCompanion {

    private Long id;
    private Long userId;
    private String name;
    private String relationship;
    private String phone;
    private String idCard;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}