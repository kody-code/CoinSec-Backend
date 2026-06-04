package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.RecordRequest;
import com.kody.coinsec.backend.dto.RecordResponse;
import com.kody.coinsec.backend.dto.StatisticsResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface RecordService {

    RecordResponse createRecord(RecordRequest request);

    Page<RecordResponse> getRecords(int page, int size, Long categoryId, String type,
                                    LocalDate startDate, LocalDate endDate);

    StatisticsResponse getStatistics(LocalDate startDate, LocalDate endDate);
}
