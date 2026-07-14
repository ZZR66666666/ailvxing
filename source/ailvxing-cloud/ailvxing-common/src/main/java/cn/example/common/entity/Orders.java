package cn.example.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Orders {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long planId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private LocalDateTime payTime;
    private String payMethod;
    private Integer status;
    private String contactName;
    private String contactPhone;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private transient List<OrderItem> items;
}