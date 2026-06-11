package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.TagRequest;
import com.kody.coinsec.backend.dto.TagResponse;
import com.kody.coinsec.backend.service.TagService;
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
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "标签管理", description = "标签的增删改查")
@SecurityRequirement(name = "satoken")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "标签列表", description = "获取当前用户所有标签")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回标签列表")
    })
    @GetMapping
    public Result<List<TagResponse>> list() {
        return Result.success(tagService.getTags());
    }

    @Operation(summary = "创建标签", description = "新增一个标签")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功，返回 tagId")
    })
    @PostMapping
    public Result<Map<String, Long>> create(@RequestBody TagRequest request) {
        TagResponse tag = tagService.createTag(request);
        return Result.success(Map.of("tagId", tag.getTagId()));
    }

    @Operation(summary = "更新标签", description = "修改指定标签")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "标签 ID") @PathVariable Long id,
            @RequestBody TagRequest request) {
        tagService.updateTag(id, request);
        return Result.success();
    }

    @Operation(summary = "删除标签", description = "逻辑删除指定标签")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "标签 ID") @PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}
