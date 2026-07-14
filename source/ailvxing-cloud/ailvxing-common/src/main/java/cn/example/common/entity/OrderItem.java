package cn.example.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productType;
    private BigDecimal price;
    private Integer quantity;
    private LocalDate travelDate;
    private LocalDateTime createTime;
}