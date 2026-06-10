package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {

    List<BudgetEntity> findByUserIdAndIsDeletedFalse(Long userId);

    Optional<BudgetEntity> findByUserIdAndBudgetIdAndIsDeletedFalse(Long userId, Long budgetId);

    List<BudgetEntity> findByUserIdAndPeriodTypeAndPeriodYearAndIsDeletedFalse(
            Long userId, String periodType, Integer periodYear);

    List<BudgetEntity> findByUserIdAndPeriodTypeAndPeriodYearAndPeriodMonthAndIsDeletedFalse(
            Long userId, String periodType, Integer periodYear, Integer periodMonth);

    List<BudgetEntity> findByUserIdAndCategoryIdAndPeriodTypeAndPeriodYearAndPeriodMonthAndIsDeletedFalse(
            Long userId, Long categoryId, String periodType, Integer periodYear, Integer periodMonth);
}
