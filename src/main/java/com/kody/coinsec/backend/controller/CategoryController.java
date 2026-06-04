package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.CategoryRequest;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryEntity>> list(@RequestParam(required = false) String type) {
        return Result.success(categoryService.getCategories(type));
    }

    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody CategoryRequest request) {
        CategoryEntity category = categoryService.createCategory(request);
        return Result.success(Map.of("categoryId", category.getCategoryId()));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
