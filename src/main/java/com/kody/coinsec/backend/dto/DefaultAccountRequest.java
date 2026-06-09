package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DefaultAccountRequest {

    @Schema(description = "默认支出账户 ID，传 null 表示不修改")
    private Long defaultIncomeAccountId;

    @Schema(description = "默认收入账户 ID，传 null 表示不修改")
    private Long defaultExpenseAccountId;
}
