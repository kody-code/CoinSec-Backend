package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.BudgetOverviewResponse;
import com.kody.coinsec.backend.dto.BudgetRequest;
import com.kody.coinsec.backend.dto.BudgetResponse;

import java.util.List;

public interface BudgetService {

    List<BudgetResponse> getBudgets();

    BudgetResponse createBudget(BudgetRequest request);

    void updateBudget(Long id, BudgetRequest request);

    void deleteBudget(Long id);

    List<BudgetOverviewResponse> getOverview(String periodType, Integer periodYear, Integer periodMonth);
}
