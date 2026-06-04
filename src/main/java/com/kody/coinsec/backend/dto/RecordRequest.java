package com.kody.coinsec.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecordRequest {

    private Long categoryId;
    private Long accountId;
    private String type;
    private BigDecimal amount;
    private String remark;
    private String recordTime;
}
