package cn.example.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlanGenerateDTO {

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "天数不能为空")
    private Integer days;

    private BigDecimal budget;

    private String budgetLevel;

    private String interestTags;

    private String companionIds;
}