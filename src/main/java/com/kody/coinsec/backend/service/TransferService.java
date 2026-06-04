package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.TransferRequest;
import com.kody.coinsec.backend.dto.TransferResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface TransferService {

    TransferResponse createTransfer(TransferRequest request);

    Page<TransferResponse> getTransfers(int page, int size, LocalDate startDate, LocalDate endDate);
}
