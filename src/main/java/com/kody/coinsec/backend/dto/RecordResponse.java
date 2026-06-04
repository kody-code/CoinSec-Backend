package com.kody.coinsec.backend.dto;

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
public class RecordResponse {

    private Long recordId;
    private Long categoryId;
    private String categoryName;
    private Long accountId;
    private String accountName;
    private String type;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime recordTime;
}
