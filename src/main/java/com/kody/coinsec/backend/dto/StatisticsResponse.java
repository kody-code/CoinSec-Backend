package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {

    @Schema(description = "总收入")
    private BigDecimal totalIncome;

    @Schema(description = "总支出")
    private BigDecimal totalExpense;

    @Schema(description = "分类统计明细")
    private List<CategoryStat> categoryStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStat {
        @Schema(description = "分类 ID")
        private Long categoryId;

        @Schema(description = "分类名称")
        private String categoryName;

        @Schema(description = "类型: income(收入) / expense(支出)")
        private String type;

        @Schema(description = "该分类总金额")
        private BigDecimal total;

        @Schema(description = "该分类记录条数")
        private Long count;
    }
}
