package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.RecordRequest;
import com.kody.coinsec.backend.dto.RecordResponse;
import com.kody.coinsec.backend.dto.StatisticsResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface RecordService {

    RecordResponse createRecord(RecordRequest request);

    RecordResponse updateRecord(Long recordId, RecordRequest request);

    void deleteRecord(Long recordId);

    Page<RecordResponse> getRecords(int page, int size, List<Long> categoryIds, String type,
                                    LocalDate startDate, LocalDate endDate, Long accountId);

    StatisticsResponse getStatistics(LocalDate startDate, LocalDate endDate, Long accountId);
}
