package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetOverviewResponse {

    @Schema(description = "预算 ID")
    private Long budgetId;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "已支出金额")
    private BigDecimal spentAmount;

    @Schema(description = "剩余金额")
    private BigDecimal remaining;

    @Schema(description = "已用比例 (0~1)")
    private Double percentage;
}
