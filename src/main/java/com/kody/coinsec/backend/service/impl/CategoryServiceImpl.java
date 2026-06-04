package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.CategoryRequest;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import com.kody.coinsec.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryEntity> getCategories(String type) {
        long userId = StpUtil.getLoginIdAsLong();
        if (type != null && !type.isBlank()) {
            return categoryRepository.findByUserIdAndTypeAndIsDeletedFalseOrderBySortAsc(userId, type);
        }
        return categoryRepository.findByUserIdAndIsDeletedFalseOrderBySortAsc(userId);
    }

    @Override
    public CategoryEntity createCategory(CategoryRequest request) {
        long userId = StpUtil.getLoginIdAsLong();

        CategoryEntity category = CategoryEntity.builder()
                .userId(userId)
                .name(request.getName())
                .type(request.getType())
                .icon(request.getIcon())
                .sort(request.getSort() != null ? request.getSort() : 0)
                .build();

        return categoryRepository.save(category);
    }

    @Override
    public void updateCategory(Long id, CategoryRequest request) {
        CategoryEntity category = findById(id);

        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getSort() != null) {
            category.setSort(request.getSort());
        }

        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        CategoryEntity category = findById(id);
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }

    private CategoryEntity findById(Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        return categoryRepository.findById(id)
                .filter(c -> c.getUserId().equals(userId) && !c.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
    }
}
