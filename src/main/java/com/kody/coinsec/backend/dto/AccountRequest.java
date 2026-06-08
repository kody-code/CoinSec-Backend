package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {

    @Schema(description = "账户名称", example = "微信钱包")
    private String name;

    @Schema(description = "图标文件名", example = "wechat")
    private String icon;

    @Schema(description = "初始余额", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "状态: 1-启用, 0-禁用", example = "1")
    private Integer status;
}
