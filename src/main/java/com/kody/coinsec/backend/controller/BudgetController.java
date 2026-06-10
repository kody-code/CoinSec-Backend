package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.BudgetOverviewResponse;
import com.kody.coinsec.backend.dto.BudgetRequest;
import com.kody.coinsec.backend.dto.BudgetResponse;
import com.kody.coinsec.backend.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "预算管理", description = "预算的增删改查与进度概览")
@SecurityRequirement(name = "satoken")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "预算列表", description = "获取当前用户所有预算")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回预算列表")
    })
    @GetMapping
    public Result<List<BudgetResponse>> list() {
        return Result.success(budgetService.getBudgets());
    }

    @Operation(summary = "创建预算", description = "新增一个预算")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功，返回 budgetId")
    })
    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody BudgetRequest request) {
        BudgetResponse budget = budgetService.createBudget(request);
        return Result.success(Map.of("budgetId", budget.getBudgetId()));
    }

    @Operation(summary = "更新预算", description = "修改指定预算")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "预算不存在")
    })
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "预算 ID") @PathVariable Long id,
            @RequestBody BudgetRequest request) {
        budgetService.updateBudget(id, request);
        return Result.success();
    }

    @Operation(summary = "删除预算", description = "逻辑删除指定预算")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "预算不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "预算 ID") @PathVariable Long id) {
        budgetService.deleteBudget(id);
        return Result.success();
    }

    @Operation(summary = "预算进度概览", description = "获取各分类预算的已支出、剩余金额和进度百分比")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回预算进度数据")
    })
    @GetMapping("/overview")
    public Result<List<BudgetOverviewResponse>> overview(
            @Parameter(description = "周期类型: MONTHLY / YEARLY", required = true)
            @RequestParam String periodType,
            @Parameter(description = "年份", required = true)
            @RequestParam Integer periodYear,
            @Parameter(description = "月份，periodType=MONTHLY 时必填")
            @RequestParam(required = false) Integer periodMonth) {
        return Result.success(budgetService.getOverview(periodType, periodYear, periodMonth));
    }
}
