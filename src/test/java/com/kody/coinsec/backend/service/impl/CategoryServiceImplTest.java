package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.CategoryRequest;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

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
    @DisplayName("获取分类列表-不传type返回全部")
    void getCategories_All() {
        when(categoryRepository.findByUserIdAndIsDeletedFalseOrderBySortAsc(1L))
                .thenReturn(List.of(new CategoryEntity()));

        var result = categoryService.getCategories(null);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取分类列表-按类型筛选")
    void getCategories_ByType() {
        when(categoryRepository.findByUserIdAndTypeAndIsDeletedFalseOrderBySortAsc(1L, "expense"))
                .thenReturn(List.of(new CategoryEntity()));

        var result = categoryService.getCategories("expense");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("创建分类-成功")
    void createCategory_Success() {
        when(categoryRepository.save(any())).thenAnswer(invocation -> {
            CategoryEntity saved = invocation.getArgument(0);
            return CategoryEntity.builder()
                    .categoryId(1L)
                    .userId(saved.getUserId())
                    .name(saved.getName())
                    .type(saved.getType())
                    .icon(saved.getIcon())
                    .sort(saved.getSort())
                    .build();
        });

        CategoryRequest request = new CategoryRequest();
        request.setName("餐饮");
        request.setType("expense");
        request.setIcon("restaurant");
        request.setSort(1);

        CategoryEntity result = categoryService.createCategory(request);

        assertEquals(1L, result.getCategoryId());
        assertEquals("餐饮", result.getName());
        assertEquals("expense", result.getType());
        assertEquals("restaurant", result.getIcon());
    }

    @Test
    @DisplayName("删除分类-逻辑删除")
    void deleteCategory_Success() {
        CategoryEntity category = CategoryEntity.builder()
                .categoryId(1L)
                .userId(1L)
                .name("餐饮")
                .build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        assertTrue(category.getIsDeleted());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("删除分类-不存在时抛出异常")
    void deleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> categoryService.deleteCategory(99L));
    }
}
