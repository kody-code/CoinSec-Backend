package com.kody.coinsec.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {

    private String name;
    private String icon;
    private BigDecimal balance;
    private Integer status;
}
