package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.TransferRequest;
import com.kody.coinsec.backend.dto.TransferResponse;
import com.kody.coinsec.backend.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "转账管理", description = "账户间转账记录的增删查")
@SecurityRequirement(name = "satoken")
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "创建转账", description = "在两个账户间转账，自动调整双方余额并生成两条关联记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "转账成功，返回 transferId")
    })
    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody TransferRequest request) {
        TransferResponse transfer = transferService.createTransfer(request);
        return Result.success(Map.of("transferId", transfer.getTransferId()));
    }

    @Operation(summary = "转账列表", description = "分页查询转账记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回分页数据")
    })
    @GetMapping
    public Result<Page<TransferResponse>> list(
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(transferService.getTransfers(page, size, startDate, endDate));
    }

    @Operation(summary = "删除转账", description = "逻辑删除指定转账记录，自动回退双方账户余额")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "转账记录不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "转账 ID") @PathVariable Long id) {
        transferService.deleteTransfer(id);
        return Result.success(null);
    }

    @Operation(summary = "按记录删除转账", description = "根据出入账记录参数删除关联的转账")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "未找到匹配的记录")
    })
    @DeleteMapping("/by-record")
    public Result<Void> deleteByRecord(
            @Parameter(description = "账户 ID") @RequestParam Long accountId,
            @Parameter(description = "金额") @RequestParam BigDecimal amount,
            @Parameter(description = "记录时间 (yyyy-MM-dd'T'HH:mm:ss)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime recordTime,
            @Parameter(description = "类型: income(收入) / expense(支出)") @RequestParam String type) {
        transferService.deleteTransferByRecord(accountId, amount, recordTime, type);
        return Result.success(null);
    }
}
