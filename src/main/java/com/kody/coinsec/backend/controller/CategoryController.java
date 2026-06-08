package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.CategoryRequest;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "收支分类的增删改查")
@SecurityRequirement(name = "satoken")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类列表", description = "获取所有收支分类，可按类型筛选")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回分类列表")
    })
    @GetMapping
    public Result<List<CategoryEntity>> list(
            @Parameter(description = "分类类型: income(收入) / expense(支出)，不传则返回全部")
            @RequestParam(required = false) String type) {
        return Result.success(categoryService.getCategories(type));
    }

    @Operation(summary = "创建分类", description = "新增一个收支分类")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功，返回 categoryId")
    })
    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody CategoryRequest request) {
        CategoryEntity category = categoryService.createCategory(request);
        return Result.success(Map.of("categoryId", category.getCategoryId()));
    }

    @Operation(summary = "更新分类", description = "修改指定分类的名称、类型、图标、排序等")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "分类 ID") @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return Result.success();
    }

    @Operation(summary = "删除分类", description = "逻辑删除指定分类")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "分类 ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
