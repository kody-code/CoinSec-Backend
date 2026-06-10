package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequest {

    @Schema(description = "分类 ID", example = "1")
    private Long categoryId;

    @Schema(description = "预算金额", example = "3000.00")
    private BigDecimal budgetAmount;

    @Schema(description = "周期类型: MONTHLY / YEARLY", example = "MONTHLY")
    private String periodType;

    @Schema(description = "年份", example = "2026")
    private Integer periodYear;

    @Schema(description = "月份，periodType=MONTHLY 时必填", example = "6")
    private Integer periodMonth;
}
