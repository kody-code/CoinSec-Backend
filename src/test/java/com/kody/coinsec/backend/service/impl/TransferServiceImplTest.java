package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.TransferRequest;
import com.kody.coinsec.backend.dto.TransferResponse;
import com.kody.coinsec.backend.entity.model.AccountEntity;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.entity.model.TransferEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private TransferRepository transferRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RecordRepository recordRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

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
    @DisplayName("转账-成功")
    void createTransfer_Success() {
        AccountEntity from = AccountEntity.builder()
                .accountId(1L).userId(1L).name("微信")
                .balance(new BigDecimal("1000.00")).build();
        AccountEntity to = AccountEntity.builder()
                .accountId(2L).userId(1L).name("支付宝")
                .balance(new BigDecimal("500.00")).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));
        when(transferRepository.save(any())).thenAnswer(invocation -> {
            TransferEntity saved = invocation.getArgument(0);
            return TransferEntity.builder()
                    .transferId(1L)
                    .userId(saved.getUserId())
                    .fromAccountId(saved.getFromAccountId())
                    .toAccountId(saved.getToAccountId())
                    .amount(saved.getAmount())
                    .transferTime(saved.getTransferTime())
                    .remark(saved.getRemark())
                    .build();
        });
        when(categoryRepository.findByUserIdAndNameAndTypeAndIsDeletedFalse(1L, "转账", "expense"))
                .thenReturn(Optional.of(CategoryEntity.builder().categoryId(10L).build()));
        when(categoryRepository.findByUserIdAndNameAndTypeAndIsDeletedFalse(1L, "转账", "income"))
                .thenReturn(Optional.of(CategoryEntity.builder().categoryId(11L).build()));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("200.00"));
        request.setRemark("转到微信");

        TransferResponse response = transferService.createTransfer(request);

        assertEquals(1L, response.getTransferId());
        assertEquals(0, new BigDecimal("800.00").compareTo(from.getBalance()));
        assertEquals(0, new BigDecimal("700.00").compareTo(to.getBalance()));
        verify(accountRepository, times(2)).save(any());
        verify(recordRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("转账-相同账户时抛出异常")
    void createTransfer_SameAccount_ThrowsException() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(1L);

        assertThrows(BusinessException.class, () -> transferService.createTransfer(request));
    }

    @Test
    @DisplayName("转账-转出账户不存在时抛出异常")
    void createTransfer_FromAccountNotFound_ThrowsException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(99L);
        request.setToAccountId(1L);

        assertThrows(BusinessException.class, () -> transferService.createTransfer(request));
    }

    @Test
    @DisplayName("删除转账-成功并回退余额")
    void deleteTransfer_Success() {
        TransferEntity transfer = TransferEntity.builder()
                .transferId(1L).userId(1L)
                .fromAccountId(1L).toAccountId(2L)
                .amount(new BigDecimal("200.00")).isDeleted(false).build();
        AccountEntity from = AccountEntity.builder()
                .accountId(1L).userId(1L).name("微信")
                .balance(new BigDecimal("800.00")).build();
        AccountEntity to = AccountEntity.builder()
                .accountId(2L).userId(1L).name("支付宝")
                .balance(new BigDecimal("700.00")).build();

        when(transferRepository.findById(1L)).thenReturn(Optional.of(transfer));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));
        when(recordRepository.findByUserIdAndAccountIdAndAmountAndRecordTimeAndTypeAndIsDeletedFalse(
                1L, 1L, new BigDecimal("200.00"), null, "expense"))
                .thenReturn(Collections.emptyList());
        when(recordRepository.findByUserIdAndAccountIdAndAmountAndRecordTimeAndTypeAndIsDeletedFalse(
                1L, 2L, new BigDecimal("200.00"), null, "income"))
                .thenReturn(Collections.emptyList());

        transferService.deleteTransfer(1L);

        assertTrue(transfer.getIsDeleted());
        assertEquals(0, new BigDecimal("1000.00").compareTo(from.getBalance()));
        assertEquals(0, new BigDecimal("500.00").compareTo(to.getBalance()));
        verify(transferRepository).save(transfer);
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("删除转账-记录不存在时抛出异常")
    void deleteTransfer_NotFound_ThrowsException() {
        when(transferRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> transferService.deleteTransfer(99L));
    }
}