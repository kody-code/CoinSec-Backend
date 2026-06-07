package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.RecordRequest;
import com.kody.coinsec.backend.dto.RecordResponse;
import com.kody.coinsec.backend.dto.StatisticsResponse;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.entity.model.RecordEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import com.kody.coinsec.backend.mapper.dao.RecordRepository;
import com.kody.coinsec.backend.mapper.dao.RecordSpecification;
import com.kody.coinsec.backend.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public RecordResponse createRecord(RecordRequest request) {
        long userId = StpUtil.getLoginIdAsLong();
        AccountEntity account = findAccount(request.getAccountId(), userId);

        RecordEntity record = RecordEntity.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .accountId(request.getAccountId())
                .type(request.getType())
                .amount(request.getAmount())
                .remark(request.getRemark())
                .recordTime(parseTime(request.getRecordTime()))
                .build();
        RecordEntity saved = recordRepository.save(record);

        updateBalance(account, request.getType(), request.getAmount(), false);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public RecordResponse updateRecord(Long recordId, RecordRequest request) {
        long userId = StpUtil.getLoginIdAsLong();
        RecordEntity record = recordRepository.findById(recordId)
                .filter(r -> r.getUserId().equals(userId) && !r.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "记录不存在"));

        updateBalance(
                findAccount(record.getAccountId(), userId),
                record.getType(),
                record.getAmount(),
                true
        );

        record.setCategoryId(request.getCategoryId());
        record.setAccountId(request.getAccountId());
        record.setType(request.getType());
        record.setAmount(request.getAmount());
        record.setRemark(request.getRemark());
        record.setRecordTime(parseTime(request.getRecordTime()));

        RecordEntity saved = recordRepository.save(record);

        updateBalance(
                findAccount(request.getAccountId(), userId),
                request.getType(),
                request.getAmount(),
                false
        );

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRecord(Long recordId) {
        long userId = StpUtil.getLoginIdAsLong();
        RecordEntity record = recordRepository.findById(recordId)
                .filter(r -> r.getUserId().equals(userId) && !r.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "记录不存在"));

        record.setIsDeleted(true);
        recordRepository.save(record);

        updateBalance(
                findAccount(record.getAccountId(), userId),
                record.getType(),
                record.getAmount(),
                true
        );
    }

    @Override
    public Page<RecordResponse> getRecords(int page, int size, List<Long> categoryIds, String type,
                                           LocalDate startDate, LocalDate endDate, Long accountId) {
        long userId = StpUtil.getLoginIdAsLong();
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        var spec = RecordSpecification.withFilters(userId, categoryIds, type, start, end, accountId);
        var pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "recordTime"));

        return recordRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public StatisticsResponse getStatistics(LocalDate startDate, LocalDate endDate, Long accountId) {
        long userId = StpUtil.getLoginIdAsLong();
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        BigDecimal totalIncome = accountId != null
                ? recordRepository.sumByTypeAndDateRangeAndAccountId(userId, "income", start, end, accountId)
                : recordRepository.sumByTypeAndDateRange(userId, "income", start, end);
        BigDecimal totalExpense = accountId != null
                ? recordRepository.sumByTypeAndDateRangeAndAccountId(userId, "expense", start, end, accountId)
                : recordRepository.sumByTypeAndDateRange(userId, "expense", start, end);
        List<StatisticsResponse.CategoryStat> categoryStats = accountId != null
                ? recordRepository.findCategoryStatsByAccountId(userId, start, end, accountId)
                : recordRepository.findCategoryStats(userId, start, end);

        return StatisticsResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .categoryStats(categoryStats)
                .build();
    }

    private void updateBalance(AccountEntity account, String type, BigDecimal amount, boolean isRevert) {
        if ("expense".equals(type)) {
            account.setBalance(isRevert
                    ? account.getBalance().add(amount)
                    : account.getBalance().subtract(amount));
        } else {
            account.setBalance(isRevert
                    ? account.getBalance().subtract(amount)
                    : account.getBalance().add(amount));
        }
        accountRepository.save(account);
    }

    private AccountEntity findAccount(Long accountId, Long userId) {
        return accountRepository.findById(accountId)
                .filter(a -> a.getUserId().equals(userId) && !a.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "账户不存在"));
    }

    private RecordResponse toResponse(RecordEntity r) {
        String categoryName = categoryRepository.findById(r.getCategoryId())
                .map(CategoryEntity::getName).orElse(null);
        String accountName = accountRepository.findById(r.getAccountId())
                .map(AccountEntity::getName).orElse(null);

        return RecordResponse.builder()
                .recordId(r.getRecordId())
                .categoryId(r.getCategoryId())
                .categoryName(categoryName)
                .accountId(r.getAccountId())
                .accountName(accountName)
                .type(r.getType())
                .amount(r.getAmount())
                .remark(r.getRemark())
                .recordTime(r.getRecordTime())
                .build();
    }

    private LocalDateTime parseTime(String time) {
        if (time == null || time.isBlank()) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(time, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
