package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.AccountRequest;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public List<AccountEntity> getAccounts() {
        long userId = StpUtil.getLoginIdAsLong();
        return accountRepository.findByUserIdAndIsDeletedFalseOrderByAccountIdAsc(userId);
    }

    @Override
    public AccountEntity createAccount(AccountRequest request) {
        long userId = StpUtil.getLoginIdAsLong();
        AccountEntity account = AccountEntity.builder()
                .userId(userId)
                .name(request.getName())
                .icon(request.getIcon())
                .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .build();
        return accountRepository.save(account);
    }

    @Override
    public void updateAccount(Long id, AccountRequest request) {
        AccountEntity account = findById(id);
        if (request.getName() != null) {
            account.setName(request.getName());
        }
        if (request.getIcon() != null) {
            account.setIcon(request.getIcon());
        }
        if (request.getStatus() != null) {
            account.setStatus(request.getStatus());
        }
        accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long id) {
        AccountEntity account = findById(id);
        account.setIsDeleted(true);
        accountRepository.save(account);
    }

    private AccountEntity findById(Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        return accountRepository.findById(id)
                .filter(a -> a.getUserId().equals(userId) && !a.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "账户不存在"));
    }
}
