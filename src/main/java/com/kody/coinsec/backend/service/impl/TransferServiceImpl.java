package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.TransferRequest;
import com.kody.coinsec.backend.dto.TransferResponse;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.entity.model.TransferEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.TransferRepository;
import com.kody.coinsec.backend.mapper.dao.TransferSpecification;
import com.kody.coinsec.backend.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

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

        TransferEntity transfer = TransferEntity.builder()
                .userId(userId)
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .remark(request.getRemark())
                .transferTime(parseTime(request.getTransferTime()))
                .build();
        TransferEntity saved = transferRepository.save(transfer);

        return toResponse(saved, fromAccount.getName(), toAccount.getName());
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
