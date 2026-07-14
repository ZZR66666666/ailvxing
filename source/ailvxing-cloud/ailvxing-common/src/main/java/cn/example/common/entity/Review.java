package cn.example.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {

    private Long id;
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer rating;
    private String content;
    private String images;
    private Integer status;
    private LocalDateTime createTime;
}