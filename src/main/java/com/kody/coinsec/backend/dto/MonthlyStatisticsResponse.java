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
public class MonthlyStatisticsResponse {

    @Schema(description = "月份 (1-12)")
    private Integer month;

    @Schema(description = "总收入")
    private BigDecimal income;

    @Schema(description = "总支出")
    private BigDecimal expense;
}
