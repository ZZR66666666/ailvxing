package cn.example.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {

    @NotBlank(message = "产品名称不能为空")
    private String name;

    @NotBlank(message = "产品类型不能为空")
    private String type;

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "天数不能为空")
    private Integer days;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private String priceType;
    private String includes;
    private String description;
    private String coverImage;
    private Integer stock;
    private String startDate;
    private String endDate;
}