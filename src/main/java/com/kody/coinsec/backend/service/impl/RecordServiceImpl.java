package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.AnnualStatisticsResponse;
import com.kody.coinsec.backend.dto.MonthlyStatisticsResponse;
import com.kody.coinsec.backend.dto.RecordRequest;
import com.kody.coinsec.backend.dto.RecordResponse;
import com.kody.coinsec.backend.dto.StatisticsResponse;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.entity.model.RecordEntity;
import com.kody.coinsec.backend.entity.model.TagEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import com.kody.coinsec.backend.mapper.dao.RecordRepository;
import com.kody.coinsec.backend.mapper.dao.RecordSpecification;
import com.kody.coinsec.backend.mapper.dao.TagRepository;
import com.kody.coinsec.backend.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

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
                .tags(resolveTags(request.getTagIds(), userId))
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
        if (request.getTagIds() != null) {
            record.setTags(resolveTags(request.getTagIds(), userId));
        }

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
                                           LocalDate startDate, LocalDate endDate, Long accountId,
                                           String keyword, List<Long> tagIds) {
        long userId = StpUtil.getLoginIdAsLong();
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        var spec = RecordSpecification.withFilters(userId, categoryIds, type, start, end, accountId, keyword, tagIds);
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

    @Override
    public List<MonthlyStatisticsResponse> getMonthlyStatistics(Integer year) {
        long userId = StpUtil.getLoginIdAsLong();
        return recordRepository.findMonthlyStatistics(userId, year);
    }

    @Override
    public List<AnnualStatisticsResponse> getAnnualStatistics(Integer startYear, Integer endYear) {
        long userId = StpUtil.getLoginIdAsLong();
        return recordRepository.findAnnualStatistics(userId, startYear, endYear);
    }

    @Override
    public void exportRecords(LocalDate startDate, LocalDate endDate, String type, HttpServletResponse response) {
        long userId = StpUtil.getLoginIdAsLong();
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        var spec = RecordSpecification.withFilters(userId, null, type, start, end, null, null, null);

        List<RecordEntity> records = recordRepository.findAll(spec,
                Sort.by(Sort.Direction.DESC, "recordTime"));

        StringBuilder csv = new StringBuilder("ID,类型,金额,分类,账户,备注,时间\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (RecordEntity record : records) {
            String categoryName = categoryRepository.findById(record.getCategoryId())
                    .map(CategoryEntity::getName).orElse("");
            String accountName = accountRepository.findById(record.getAccountId())
                    .map(AccountEntity::getName).orElse("");

            csv.append(record.getRecordId()).append(",")
                    .append(record.getType()).append(",")
                    .append(record.getAmount()).append(",")
                    .append(categoryName).append(",")
                    .append(accountName).append(",")
                    .append(record.getRemark() != null ? record.getRemark() : "").append(",")
                    .append(record.getRecordTime().format(fmt))
                    .append("\n");
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=records.csv");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(csv.toString());
            response.getWriter().flush();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    @Override
    @Transactional
    public void updateRecordTags(Long recordId, List<Long> tagIds) {
        long userId = StpUtil.getLoginIdAsLong();
        RecordEntity record = recordRepository.findById(recordId)
                .filter(r -> r.getUserId().equals(userId) && !r.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "记录不存在"));

        record.setTags(resolveTags(tagIds, userId));
        recordRepository.save(record);
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

    private Set<TagEntity> resolveTags(List<Long> tagIds, Long userId) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }
        List<TagEntity> tags = tagRepository.findAllById(tagIds);
        for (TagEntity tag : tags) {
            if (!tag.getUserId().equals(userId) || tag.getIsDeleted()) {
                throw new BusinessException(404, "标签不存在: " + tag.getTagId());
            }
        }
        return new HashSet<>(tags);
    }

    private RecordResponse toResponse(RecordEntity r) {
        String categoryName = categoryRepository.findById(r.getCategoryId())
                .map(CategoryEntity::getName).orElse(null);
        String accountName = accountRepository.findById(r.getAccountId())
                .map(AccountEntity::getName).orElse(null);

        List<Long> tagIds = r.getTags().stream()
                .map(TagEntity::getTagId)
                .toList();

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
                .tagIds(tagIds)
                .build();
    }

    private LocalDateTime parseTime(String time) {
        if (time == null || time.isBlank()) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(time, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
