package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    @Schema(description = "转账 ID")
    private Long transferId;

    @Schema(description = "转出账户 ID")
    private Long fromAccountId;

    @Schema(description = "转出账户名称")
    private String fromAccountName;

    @Schema(description = "转入账户 ID")
    private Long toAccountId;

    @Schema(description = "转入账户名称")
    private String toAccountName;

    @Schema(description = "转账金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "转账时间")
    private LocalDateTime transferTime;
}
