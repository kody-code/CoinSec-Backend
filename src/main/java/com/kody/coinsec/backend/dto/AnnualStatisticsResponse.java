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
public class AnnualStatisticsResponse {

    @Schema(description = "年份")
    private Integer year;

    @Schema(description = "年总收入")
    private BigDecimal income;

    @Schema(description = "年总支出")
    private BigDecimal expense;
}
