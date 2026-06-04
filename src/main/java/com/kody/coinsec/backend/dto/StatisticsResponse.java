package com.kody.coinsec.backend.dto;

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

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private List<CategoryStat> categoryStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStat {
        private Long categoryId;
        private String categoryName;
        private String type;
        private BigDecimal total;
        private Long count;
    }
}
