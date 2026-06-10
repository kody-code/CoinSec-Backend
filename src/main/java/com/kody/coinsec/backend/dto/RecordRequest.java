package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RecordRequest {

    @Schema(description = "分类 ID", example = "1")
    private Long categoryId;

    @Schema(description = "账户 ID", example = "1")
    private Long accountId;

    @Schema(description = "类型: income(收入) / expense(支出)", example = "expense")
    private String type;

    @Schema(description = "金额", example = "99.00")
    private BigDecimal amount;

    @Schema(description = "备注", example = "午餐")
    private String remark;

    @Schema(description = "记录时间 (yyyy-MM-dd'T'HH:mm:ss)", example = "2026-06-08T12:30:00")
    private String recordTime;

    @Schema(description = "标签 ID 列表")
    private List<Long> tagIds;
}
