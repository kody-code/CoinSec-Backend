package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @Schema(description = "转出账户 ID", example = "1")
    private Long fromAccountId;

    @Schema(description = "转入账户 ID", example = "2")
    private Long toAccountId;

    @Schema(description = "转账金额", example = "500.00")
    private BigDecimal amount;

    @Schema(description = "备注", example = "还钱")
    private String remark;

    @Schema(description = "转账时间 (yyyy-MM-dd'T'HH:mm:ss)", example = "2026-06-08T12:30:00")
    private String transferTime;
}
