package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.TransferRequest;
import com.kody.coinsec.backend.dto.TransferResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TransferService {

    TransferResponse createTransfer(TransferRequest request);

    void deleteTransfer(Long transferId);

    void deleteTransferByRecord(Long accountId, BigDecimal amount, LocalDateTime recordTime, String type);

    Page<TransferResponse> getTransfers(int page, int size, LocalDate startDate, LocalDate endDate);
}
