package com.kody.coinsec.backend.entity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets")
public class BudgetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    @Schema(description = "预算 ID")
    private Long budgetId;

    @Column(name = "user_id", nullable = false)
    @Schema(hidden = true)
    private Long userId;

    @Column(name = "category_id", nullable = false)
    @Schema(description = "分类 ID")
    private Long categoryId;

    @Column(name = "budget_amount", nullable = false, precision = 12, scale = 2)
    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Column(name = "period_type", nullable = false, length = 20)
    @Schema(description = "周期类型: MONTHLY / YEARLY")
    private String periodType;

    @Column(name = "period_year", nullable = false)
    @Schema(description = "年份")
    private Integer periodYear;

    @Column(name = "period_month")
    @Schema(description = "月份，按月预算时填写")
    private Integer periodMonth;

    @Column(name = "is_deleted")
    @Builder.Default
    @Schema(hidden = true)
    private Boolean isDeleted = false;
}
