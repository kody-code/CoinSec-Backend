package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.BudgetRequest;
import com.kody.coinsec.backend.entity.model.BudgetEntity;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.mapper.dao.BudgetRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RecordRepository recordRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;

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
    @DisplayName("获取预算列表-成功")
    void getBudgets_Success() {
        when(budgetRepository.findByUserIdAndIsDeletedFalse(1L))
                .thenReturn(List.of(createBudgetEntity(1L, 1L)));

        var result = budgetService.getBudgets();

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getBudgetId());
    }

    @Test
    @DisplayName("创建预算-成功")
    void createBudget_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(createCategoryEntity()));
        when(budgetRepository.save(any())).thenAnswer(invocation -> {
            BudgetEntity saved = invocation.getArgument(0);
            return BudgetEntity.builder()
                    .budgetId(1L)
                    .userId(saved.getUserId())
                    .categoryId(saved.getCategoryId())
                    .budgetAmount(saved.getBudgetAmount())
                    .periodType(saved.getPeriodType())
                    .periodYear(saved.getPeriodYear())
                    .periodMonth(saved.getPeriodMonth())
                    .build();
        });

        BudgetRequest request = new BudgetRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(new BigDecimal("3000.00"));
        request.setPeriodType("MONTHLY");
        request.setPeriodYear(2026);
        request.setPeriodMonth(6);

        var result = budgetService.createBudget(request);

        assertEquals(1L, result.getBudgetId());
        assertEquals(0, new BigDecimal("3000.00").compareTo(result.getBudgetAmount()));
    }

    @Test
    @DisplayName("删除预算-逻辑删除")
    void deleteBudget_Success() {
        BudgetEntity entity = createBudgetEntity(1L, 1L);
        when(budgetRepository.findByUserIdAndBudgetIdAndIsDeletedFalse(1L, 1L))
                .thenReturn(Optional.of(entity));

        budgetService.deleteBudget(1L);

        assertTrue(entity.getIsDeleted());
        verify(budgetRepository).save(entity);
    }

    @Test
    @DisplayName("删除预算-不存在时抛出异常")
    void deleteBudget_NotFound_ThrowsException() {
        when(budgetRepository.findByUserIdAndBudgetIdAndIsDeletedFalse(1L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> budgetService.deleteBudget(99L));
    }

    private BudgetEntity createBudgetEntity(Long id, Long categoryId) {
        return BudgetEntity.builder()
                .budgetId(id)
                .userId(1L)
                .categoryId(categoryId)
                .budgetAmount(new BigDecimal("3000.00"))
                .periodType("MONTHLY")
                .periodYear(2026)
                .periodMonth(6)
                .build();
    }

    private CategoryEntity createCategoryEntity() {
        return CategoryEntity.builder()
                .categoryId(1L)
                .userId(1L)
                .name("餐饮")
                .type("expense")
                .build();
    }
}
