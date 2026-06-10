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
public class BudgetResponse {

    @Schema(description = "预算 ID")
    private Long budgetId;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "周期类型: MONTHLY / YEARLY")
    private String periodType;

    @Schema(description = "年份")
    private Integer periodYear;

    @Schema(description = "月份，按月预算时有值")
    private Integer periodMonth;
}
