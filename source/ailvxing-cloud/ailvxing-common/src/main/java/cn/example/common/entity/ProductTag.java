package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductTag {

    private Long id;
    private String name;
    private String category;
    private LocalDateTime createTime;
}