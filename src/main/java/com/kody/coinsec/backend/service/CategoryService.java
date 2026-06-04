package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.CategoryRequest;
import com.kody.coinsec.backend.entity.model.CategoryEntity;

import java.util.List;

public interface CategoryService {

    List<CategoryEntity> getCategories(String type);

    CategoryEntity createCategory(CategoryRequest request);

    void updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
