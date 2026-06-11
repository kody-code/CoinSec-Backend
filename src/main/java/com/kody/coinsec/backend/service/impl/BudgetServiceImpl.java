package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.BudgetOverviewResponse;
import com.kody.coinsec.backend.dto.BudgetRequest;
import com.kody.coinsec.backend.dto.BudgetResponse;
import com.kody.coinsec.backend.entity.model.BudgetEntity;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.mapper.dao.BudgetRepository;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import com.kody.coinsec.backend.mapper.dao.RecordRepository;
import com.kody.coinsec.backend.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final RecordRepository recordRepository;

    @Override
    public List<BudgetResponse> getBudgets() {
        long userId = StpUtil.getLoginIdAsLong();
        return budgetRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BudgetResponse createBudget(BudgetRequest request) {
        long userId = StpUtil.getLoginIdAsLong();
        validateCategory(request.getCategoryId(), userId);

        BudgetEntity entity = BudgetEntity.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .budgetAmount(request.getBudgetAmount())
                .periodType(request.getPeriodType())
                .periodYear(request.getPeriodYear())
                .periodMonth(request.getPeriodMonth())
                .build();

        BudgetEntity saved = budgetRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void updateBudget(Long id, BudgetRequest request) {
        BudgetEntity entity = findById(id);

        if (request.getCategoryId() != null) {
            validateCategory(request.getCategoryId(), entity.getUserId());
            entity.setCategoryId(request.getCategoryId());
        }
        if (request.getBudgetAmount() != null) {
            entity.setBudgetAmount(request.getBudgetAmount());
        }
        if (request.getPeriodType() != null) {
            entity.setPeriodType(request.getPeriodType());
        }
        if (request.getPeriodYear() != null) {
            entity.setPeriodYear(request.getPeriodYear());
        }
        if (request.getPeriodMonth() != null) {
            entity.setPeriodMonth(request.getPeriodMonth());
        }

        budgetRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteBudget(Long id) {
        BudgetEntity entity = findById(id);
        entity.setIsDeleted(true);
        budgetRepository.save(entity);
    }

    @Override
    public List<BudgetOverviewResponse> getOverview(String periodType, Integer periodYear, Integer periodMonth) {
        long userId = StpUtil.getLoginIdAsLong();

        List<BudgetEntity> budgets = periodMonth != null
                ? budgetRepository.findByUserIdAndPeriodTypeAndPeriodYearAndPeriodMonthAndIsDeletedFalse(
                        userId, periodType, periodYear, periodMonth)
                : budgetRepository.findByUserIdAndPeriodTypeAndPeriodYearAndIsDeletedFalse(
                        userId, periodType, periodYear);

        return budgets.stream().map(budget -> {
            LocalDateTime start = getPeriodStart(periodType, budget.getPeriodYear(), budget.getPeriodMonth());
            LocalDateTime end = getPeriodEnd(periodType, budget.getPeriodYear(), budget.getPeriodMonth());

            BigDecimal spent = recordRepository.sumExpenseByCategoryAndDateRange(
                    userId, budget.getCategoryId(), start, end);
            BigDecimal remaining = budget.getBudgetAmount().subtract(spent);
            double percentage = budget.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0
                    ? spent.divide(budget.getBudgetAmount(), 4, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            String categoryName = categoryRepository.findById(budget.getCategoryId())
                    .map(CategoryEntity::getName)
                    .orElse(null);

            return BudgetOverviewResponse.builder()
                    .budgetId(budget.getBudgetId())
                    .categoryId(budget.getCategoryId())
                    .categoryName(categoryName)
                    .budgetAmount(budget.getBudgetAmount())
                    .spentAmount(spent)
                    .remaining(remaining)
                    .percentage(percentage)
                    .build();
        }).toList();
    }

    private BudgetEntity findById(Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        return budgetRepository.findByUserIdAndBudgetIdAndIsDeletedFalse(userId, id)
                .orElseThrow(() -> new BusinessException(404, "预算不存在"));
    }

    private void validateCategory(Long categoryId, Long userId) {
        categoryRepository.findById(categoryId)
                .filter(c -> c.getUserId().equals(userId) && !c.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
    }

    private BudgetResponse toResponse(BudgetEntity entity) {
        String categoryName = categoryRepository.findById(entity.getCategoryId())
                .map(CategoryEntity::getName)
                .orElse(null);

        return BudgetResponse.builder()
                .budgetId(entity.getBudgetId())
                .categoryId(entity.getCategoryId())
                .categoryName(categoryName)
                .budgetAmount(entity.getBudgetAmount())
                .periodType(entity.getPeriodType())
                .periodYear(entity.getPeriodYear())
                .periodMonth(entity.getPeriodMonth())
                .build();
    }

    private LocalDateTime getPeriodStart(String periodType, Integer year, Integer month) {
        if ("YEARLY".equals(periodType)) {
            return LocalDate.of(year, 1, 1).atStartOfDay();
        }
        return LocalDate.of(year, month, 1).atStartOfDay();
    }

    private LocalDateTime getPeriodEnd(String periodType, Integer year, Integer month) {
        if ("YEARLY".equals(periodType)) {
            return LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
        }
        return LocalDate.of(year, month, 1)
                .plusMonths(1)
                .minusDays(1)
                .atTime(LocalTime.MAX);
    }
}
