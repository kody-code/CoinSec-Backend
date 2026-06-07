package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.RecordRequest;
import com.kody.coinsec.backend.dto.RecordResponse;
import com.kody.coinsec.backend.dto.StatisticsResponse;
import com.kody.coinsec.backend.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody RecordRequest request) {
        RecordResponse record = recordService.createRecord(request);
        return Result.success(Map.of("recordId", record.getRecordId()));
    }

    @PutMapping("/{id}")
    public Result<RecordResponse> update(@PathVariable Long id, @RequestBody RecordRequest request) {
        return Result.success(recordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return Result.success(null);
    }

    @GetMapping
    public Result<Page<RecordResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long accountId) {
        return Result.success(recordService.getRecords(page, size, categoryIds, type, startDate, endDate, accountId));
    }

    @GetMapping("/statistics")
    public Result<StatisticsResponse> statistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long accountId) {
        return Result.success(recordService.getStatistics(startDate, endDate, accountId));
    }
}
