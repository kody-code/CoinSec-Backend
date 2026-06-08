package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.AccountRequest;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.service.AccountService;
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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "账户管理", description = "账户的增删改查（微信、支付宝、银行卡等）")
@SecurityRequirement(name = "satoken")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "获取账户列表", description = "获取当前用户的所有账户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回账户列表")
    })
    @GetMapping
    public Result<List<AccountEntity>> list() {
        return Result.success(accountService.getAccounts());
    }

    @Operation(summary = "创建账户", description = "新增一个账户（如微信、支付宝、银行卡）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功，返回 accountId")
    })
    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody AccountRequest request) {
        AccountEntity account = accountService.createAccount(request);
        return Result.success(Map.of("accountId", account.getAccountId()));
    }

    @Operation(summary = "更新账户", description = "修改指定账户的名称、图标、余额等")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "账户不存在")
    })
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "账户 ID") @PathVariable Long id,
            @RequestBody AccountRequest request) {
        accountService.updateAccount(id, request);
        return Result.success();
    }

    @Operation(summary = "删除账户", description = "逻辑删除指定账户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "账户不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "账户 ID") @PathVariable Long id) {
        accountService.deleteAccount(id);
        return Result.success();
    }
}
