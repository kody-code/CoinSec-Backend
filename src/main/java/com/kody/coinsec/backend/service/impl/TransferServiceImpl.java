package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.TransferRequest;
import com.kody.coinsec.backend.dto.TransferResponse;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.entity.model.RecordEntity;
import com.kody.coinsec.backend.entity.model.TransferEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import com.kody.coinsec.backend.mapper.dao.RecordRepository;
import com.kody.coinsec.backend.mapper.dao.TransferRepository;
import com.kody.coinsec.backend.mapper.dao.TransferSpecification;
import com.kody.coinsec.backend.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final RecordRepository recordRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public TransferResponse createTransfer(TransferRequest request) {
        long userId = StpUtil.getLoginIdAsLong();

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BusinessException("转入转出账户不能相同");
        }

        AccountEntity fromAccount = findAccount(request.getFromAccountId(), userId);
        AccountEntity toAccount = findAccount(request.getToAccountId(), userId);

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        LocalDateTime transferTime = parseTime(request.getTransferTime());

        TransferEntity transfer = TransferEntity.builder()
                .userId(userId)
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .remark(request.getRemark())
                .transferTime(transferTime)
                .build();
        TransferEntity saved = transferRepository.save(transfer);

        createTransferRecords(userId, fromAccount, toAccount, request, transferTime);

        return toResponse(saved, fromAccount.getName(), toAccount.getName());
    }

    private void createTransferRecords(long userId, AccountEntity fromAccount, AccountEntity toAccount,
                                       TransferRequest request, LocalDateTime transferTime) {
        CategoryEntity expenseCat = getOrCreateCategory(userId, "转账", "expense");
        CategoryEntity incomeCat = getOrCreateCategory(userId, "转账", "income");

        String fromRemark = request.getRemark() != null ? "转出 - " + request.getRemark() : "转出";
        RecordEntity fromRecord = RecordEntity.builder()
                .userId(userId)
                .categoryId(expenseCat.getCategoryId())
                .accountId(request.getFromAccountId())
                .type("expense")
                .amount(request.getAmount())
                .remark(fromRemark)
                .recordTime(transferTime)
                .build();
        recordRepository.save(fromRecord);

        String toRemark = request.getRemark() != null ? "转入 - " + request.getRemark() : "转入";
        RecordEntity toRecord = RecordEntity.builder()
                .userId(userId)
                .categoryId(incomeCat.getCategoryId())
                .accountId(request.getToAccountId())
                .type("income")
                .amount(request.getAmount())
                .remark(toRemark)
                .recordTime(transferTime)
                .build();
        recordRepository.save(toRecord);
    }

    private CategoryEntity getOrCreateCategory(long userId, String name, String type) {
        return categoryRepository.findByUserIdAndNameAndTypeAndIsDeletedFalse(userId, name, type)
                .orElseGet(() -> {
                    CategoryEntity cat = CategoryEntity.builder()
                            .userId(userId)
                            .name(name)
                            .type(type)
                            .icon("swap")
                            .sort(999)
                            .build();
                    return categoryRepository.save(cat);
                });
    }

    @Override
    @Transactional
    public void deleteTransfer(Long transferId) {
        long userId = StpUtil.getLoginIdAsLong();
        TransferEntity transfer = transferRepository.findById(transferId)
                .filter(t -> t.getUserId().equals(userId) && !t.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "转账记录不存在"));

        transfer.setIsDeleted(true);
        transferRepository.save(transfer);

        AccountEntity fromAccount = findAccount(transfer.getFromAccountId(), userId);
        AccountEntity toAccount = findAccount(transfer.getToAccountId(), userId);
        fromAccount.setBalance(fromAccount.getBalance().add(transfer.getAmount()));
        toAccount.setBalance(toAccount.getBalance().subtract(transfer.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        deleteTransferRecords(userId, transfer);
    }

    @Override
    @Transactional
    public void deleteTransferByRecord(Long accountId, BigDecimal amount, LocalDateTime recordTime, String type) {
        long userId = StpUtil.getLoginIdAsLong();
        TransferEntity transfer;
        if ("expense".equals(type)) {
            transfer = transferRepository
                    .findByUserIdAndFromAccountIdAndAmountAndTransferTimeAndIsDeletedFalse(
                            userId, accountId, amount, recordTime)
                    .orElseThrow(() -> new BusinessException(404, "转账记录不存在"));
        } else {
            transfer = transferRepository
                    .findByUserIdAndToAccountIdAndAmountAndTransferTimeAndIsDeletedFalse(
                            userId, accountId, amount, recordTime)
                    .orElseThrow(() -> new BusinessException(404, "转账记录不存在"));
        }
        deleteTransfer(transfer.getTransferId());
    }

    private void deleteTransferRecords(long userId, TransferEntity transfer) {
        List<RecordEntity> fromRecords = recordRepository
                .findByUserIdAndAccountIdAndAmountAndRecordTimeAndTypeAndIsDeletedFalse(
                        userId, transfer.getFromAccountId(), transfer.getAmount(),
                        transfer.getTransferTime(), "expense");
        for (RecordEntity r : fromRecords) {
            r.setIsDeleted(true);
            recordRepository.save(r);
        }

        List<RecordEntity> toRecords = recordRepository
                .findByUserIdAndAccountIdAndAmountAndRecordTimeAndTypeAndIsDeletedFalse(
                        userId, transfer.getToAccountId(), transfer.getAmount(),
                        transfer.getTransferTime(), "income");
        for (RecordEntity r : toRecords) {
            r.setIsDeleted(true);
            recordRepository.save(r);
        }
    }

    @Override
    public Page<TransferResponse> getTransfers(int page, int size, LocalDate startDate, LocalDate endDate) {
        long userId = StpUtil.getLoginIdAsLong();
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        var spec = TransferSpecification.withFilters(userId, start, end);
        var pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "transferTime"));

        return transferRepository.findAll(spec, pageable).map(t -> {
            String fromName = accountRepository.findById(t.getFromAccountId())
                    .map(AccountEntity::getName).orElse(null);
            String toName = accountRepository.findById(t.getToAccountId())
                    .map(AccountEntity::getName).orElse(null);
            return toResponse(t, fromName, toName);
        });
    }

    private TransferResponse toResponse(TransferEntity t, String fromName, String toName) {
        return TransferResponse.builder()
                .transferId(t.getTransferId())
                .fromAccountId(t.getFromAccountId())
                .fromAccountName(fromName)
                .toAccountId(t.getToAccountId())
                .toAccountName(toName)
                .amount(t.getAmount())
                .remark(t.getRemark())
                .transferTime(t.getTransferTime())
                .build();
    }

    private AccountEntity findAccount(Long accountId, Long userId) {
        return accountRepository.findById(accountId)
                .filter(a -> a.getUserId().equals(userId) && !a.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "账户不存在"));
    }

    private LocalDateTime parseTime(String time) {
        if (time == null || time.isBlank()) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
