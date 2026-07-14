package cn.example.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Product {

    private Long id;
    private Long agencyId;
    private String name;
    private String type;
    private String destination;
    private Integer days;
    private BigDecimal price;
    private String priceType;
    private String includes;
    private String description;
    private String coverImage;
    private Integer stock;
    private Integer soldCount;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}