package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.RecordRequest;
import com.kody.coinsec.backend.dto.RecordResponse;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.entity.model.RecordEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import com.kody.coinsec.backend.mapper.dao.RecordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordServiceImplTest {

    @Mock
    private RecordRepository recordRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private RecordServiceImpl recordService;

    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    @Test
    @DisplayName("新增支出记录-余额减少")
    void createRecord_Expense_BalanceDecreases() {
        AccountEntity account = AccountEntity.builder()
                .accountId(1L).userId(1L).name("微信")
                .balance(new BigDecimal("1000.00"))
                .build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(recordRepository.save(any())).thenAnswer(invocation -> {
            RecordEntity saved = invocation.getArgument(0);
            return RecordEntity.builder()
                    .recordId(1L).userId(saved.getUserId())
                    .categoryId(saved.getCategoryId()).accountId(saved.getAccountId())
                    .type(saved.getType()).amount(saved.getAmount())
                    .recordTime(saved.getRecordTime())
                    .build();
        });

        RecordRequest request = new RecordRequest();
        request.setCategoryId(1L);
        request.setAccountId(1L);
        request.setType("expense");
        request.setAmount(new BigDecimal("25.00"));
        request.setRemark("午餐");

        RecordResponse response = recordService.createRecord(request);

        assertEquals(1L, response.getRecordId());
        assertEquals(0, new BigDecimal("975.00").compareTo(account.getBalance()));
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("新增收入记录-余额增加")
    void createRecord_Income_BalanceIncreases() {
        AccountEntity account = AccountEntity.builder()
                .accountId(1L).userId(1L).name("微信")
                .balance(new BigDecimal("1000.00"))
                .build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(recordRepository.save(any())).thenAnswer(invocation -> {
            RecordEntity saved = invocation.getArgument(0);
            return RecordEntity.builder()
                    .recordId(1L).userId(saved.getUserId())
                    .categoryId(saved.getCategoryId()).accountId(saved.getAccountId())
                    .type(saved.getType()).amount(saved.getAmount())
                    .recordTime(saved.getRecordTime())
                    .build();
        });

        RecordRequest request = new RecordRequest();
        request.setCategoryId(1L);
        request.setAccountId(1L);
        request.setType("income");
        request.setAmount(new BigDecimal("500.00"));

        recordService.createRecord(request);

        assertEquals(0, new BigDecimal("1500.00").compareTo(account.getBalance()));
    }

    @Test
    @DisplayName("新增记录-账户不存在时抛出异常")
    void createRecord_AccountNotFound_ThrowsException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        RecordRequest request = new RecordRequest();
        request.setAccountId(99L);

        assertThrows(BusinessException.class, () -> recordService.createRecord(request));
    }
}
