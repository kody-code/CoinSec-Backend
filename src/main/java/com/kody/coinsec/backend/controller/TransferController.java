package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.TransferRequest;
import com.kody.coinsec.backend.dto.TransferResponse;
import com.kody.coinsec.backend.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody TransferRequest request) {
        TransferResponse transfer = transferService.createTransfer(request);
        return Result.success(Map.of("transferId", transfer.getTransferId()));
    }

    @GetMapping
    public Result<Page<TransferResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(transferService.getTransfers(page, size, startDate, endDate));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return Result.success(null);
    }

    @DeleteMapping("/by-record")
    public Result<Void> deleteByRecord(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime recordTime,
            @RequestParam String type) {
        transferService.deleteTransferByRecord(accountId, amount, recordTime, type);
        return Result.success(null);
    }
}
