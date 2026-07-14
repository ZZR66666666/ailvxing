package cn.example.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateDTO {

    @NotNull(message = "产品ID不能为空")
    private Long productId;

    private String contactName;

    private String contactPhone;

    private String remark;
}