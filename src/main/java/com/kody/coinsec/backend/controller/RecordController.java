package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.AnnualStatisticsResponse;
import com.kody.coinsec.backend.dto.MonthlyStatisticsResponse;
import com.kody.coinsec.backend.dto.RecordRequest;
import com.kody.coinsec.backend.dto.RecordResponse;
import com.kody.coinsec.backend.dto.StatisticsResponse;
import com.kody.coinsec.backend.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
@Tag(name = "账单管理", description = "收支记录的增删改查、筛选、统计")
@SecurityRequirement(name = "satoken")
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "创建账单", description = "新增一条收入或支出记录，自动更新对应账户余额")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功，返回 recordId")
    })
    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody RecordRequest request) {
        RecordResponse record = recordService.createRecord(request);
        return Result.success(Map.of("recordId", record.getRecordId()));
    }

    @Operation(summary = "更新账单", description = "修改指定账单记录，自动调整账户余额")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "账单不存在")
    })
    @PutMapping("/{id}")
    public Result<RecordResponse> update(
            @Parameter(description = "账单 ID") @PathVariable Long id,
            @RequestBody RecordRequest request) {
        return Result.success(recordService.updateRecord(id, request));
    }

    @Operation(summary = "删除账单", description = "逻辑删除指定账单，自动回退账户余额")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "账单不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "账单 ID") @PathVariable Long id) {
        recordService.deleteRecord(id);
        return Result.success(null);
    }

    @Operation(summary = "账单列表", description = "分页查询账单记录，支持多维度筛选")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回分页数据")
    })
    @GetMapping
    public Result<Page<RecordResponse>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "分类 ID 列表，多个用逗号分隔") @RequestParam(required = false) List<Long> categoryIds,
            @Parameter(description = "类型: income(收入) / expense(支出)") @RequestParam(required = false) String type,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "账户 ID") @RequestParam(required = false) Long accountId,
            @Parameter(description = "备注关键字搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "标签 ID 列表，多个用逗号分隔") @RequestParam(required = false) List<Long> tagIds) {
        return Result.success(recordService.getRecords(page, size, categoryIds, type, startDate, endDate, accountId, keyword, tagIds));
    }

    @Operation(summary = "更新账单标签", description = "全量替换账单关联的标签")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "账单不存在")
    })
    @PutMapping("/{id}/tags")
    public Result<Void> updateTags(
            @Parameter(description = "账单 ID") @PathVariable Long id,
            @RequestBody Map<String, List<Long>> body) {
        recordService.updateRecordTags(id, body.get("tagIds"));
        return Result.success();
    }

    @Operation(summary = "收支统计", description = "按日期范围统计总收入、总支出和分类明细")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回统计结果")
    })
    @GetMapping("/statistics")
    public Result<StatisticsResponse> statistics(
            @Parameter(description = "开始日期 (yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "账户 ID，不传则统计全部账户") @RequestParam(required = false) Long accountId) {
        return Result.success(recordService.getStatistics(startDate, endDate, accountId));
    }

    @Operation(summary = "月度收支汇总", description = "按月份统计指定年份的每月收支")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回月度数据")
    })
    @GetMapping("/statistics/monthly")
    public Result<List<MonthlyStatisticsResponse>> monthlyStatistics(
            @Parameter(description = "年份", required = true) @RequestParam Integer year) {
        return Result.success(recordService.getMonthlyStatistics(year));
    }

    @Operation(summary = "年度收支对比", description = "统计指定年份范围的年度收支")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回年度数据")
    })
    @GetMapping("/statistics/annual")
    public Result<List<AnnualStatisticsResponse>> annualStatistics(
            @Parameter(description = "开始年份", required = true) @RequestParam Integer startYear,
            @Parameter(description = "结束年份", required = true) @RequestParam Integer endYear) {
        return Result.success(recordService.getAnnualStatistics(startYear, endYear));
    }

    @Operation(summary = "导出 CSV", description = "导出账单记录为 CSV 文件")
    @GetMapping("/export")
    public void export(
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "类型: income(收入) / expense(支出)，不传则导出全部") @RequestParam(required = false) String type,
            HttpServletResponse response) {
        recordService.exportRecords(startDate, endDate, type, response);
    }
}
