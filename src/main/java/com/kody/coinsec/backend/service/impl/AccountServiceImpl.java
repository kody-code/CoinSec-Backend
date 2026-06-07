package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.AccountRequest;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.entity.model.RecordEntity;
import com.kody.coinsec.backend.entity.model.TransferEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.RecordRepository;
import com.kody.coinsec.backend.mapper.dao.TransferRepository;
import com.kody.coinsec.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RecordRepository recordRepository;
    private final TransferRepository transferRepository;

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
    @Transactional
    public void deleteAccount(Long id) {
        AccountEntity account = findById(id);
        long userId = StpUtil.getLoginIdAsLong();

        deleteAssociatedRecords(userId, id);
        deleteAssociatedTransfers(userId, id);

        account.setIsDeleted(true);
        accountRepository.save(account);
    }

    private void deleteAssociatedRecords(long userId, Long accountId) {
        List<RecordEntity> records = recordRepository
                .findByUserIdAndAccountIdAndIsDeletedFalse(userId, accountId);
        for (RecordEntity r : records) {
            r.setIsDeleted(true);
            recordRepository.save(r);
        }
    }

    private void deleteAssociatedTransfers(long userId, Long accountId) {
        List<TransferEntity> asFrom = transferRepository
                .findByUserIdAndFromAccountIdAndIsDeletedFalse(userId, accountId);
        for (TransferEntity t : asFrom) {
            t.setIsDeleted(true);
            transferRepository.save(t);
        }
        List<TransferEntity> asTo = transferRepository
                .findByUserIdAndToAccountIdAndIsDeletedFalse(userId, accountId);
        for (TransferEntity t : asTo) {
            t.setIsDeleted(true);
            transferRepository.save(t);
        }
    }

    private AccountEntity findById(Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        return accountRepository.findById(id)
                .filter(a -> a.getUserId().equals(userId) && !a.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "账户不存在"));
    }
}
