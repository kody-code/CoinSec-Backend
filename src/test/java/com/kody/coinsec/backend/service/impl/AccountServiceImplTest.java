package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.AccountRequest;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.RecordRepository;
import com.kody.coinsec.backend.mapper.dao.TransferRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RecordRepository recordRepository;
    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

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
    @DisplayName("获取账户列表")
    void getAccounts_Success() {
        when(accountRepository.findByUserIdAndIsDeletedFalseOrderByAccountIdAsc(1L))
                .thenReturn(List.of(new AccountEntity()));

        var result = accountService.getAccounts();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("创建账户-成功")
    void createAccount_Success() {
        when(accountRepository.save(any())).thenAnswer(invocation -> {
            AccountEntity saved = invocation.getArgument(0);
            return AccountEntity.builder()
                    .accountId(1L)
                    .userId(saved.getUserId())
                    .name(saved.getName())
                    .icon(saved.getIcon())
                    .balance(saved.getBalance())
                    .build();
        });

        AccountRequest request = new AccountRequest();
        request.setName("支付宝");
        request.setIcon("alipay");
        request.setBalance(new BigDecimal("500.00"));

        AccountEntity result = accountService.createAccount(request);

        assertEquals(1L, result.getAccountId());
        assertEquals("支付宝", result.getName());
        assertEquals(0, new BigDecimal("500.00").compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("删除账户-逻辑删除")
    void deleteAccount_Success() {
        AccountEntity account = AccountEntity.builder()
                .accountId(1L).userId(1L).name("微信").build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(recordRepository.findByUserIdAndAccountIdAndIsDeletedFalse(1L, 1L))
                .thenReturn(java.util.Collections.emptyList());
        when(transferRepository.findByUserIdAndFromAccountIdAndIsDeletedFalse(1L, 1L))
                .thenReturn(java.util.Collections.emptyList());
        when(transferRepository.findByUserIdAndToAccountIdAndIsDeletedFalse(1L, 1L))
                .thenReturn(java.util.Collections.emptyList());

        accountService.deleteAccount(1L);

        assertTrue(account.getIsDeleted());
        verify(accountRepository).save(account);
    }
}
