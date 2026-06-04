package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.AccountRequest;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public Result<List<AccountEntity>> list() {
        return Result.success(accountService.getAccounts());
    }

    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody AccountRequest request) {
        AccountEntity account = accountService.createAccount(request);
        return Result.success(Map.of("accountId", account.getAccountId()));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AccountRequest request) {
        accountService.updateAccount(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return Result.success();
    }
}
