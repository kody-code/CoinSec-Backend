package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CategoryRequest {

    @Schema(description = "分类名称", example = "餐饮")
    private String name;

    @Schema(description = "类型: income(收入) / expense(支出)", example = "expense")
    private String type;

    @Schema(description = "图标文件名", example = "food")
    private String icon;

    @Schema(description = "排序号，越小越靠前", example = "0")
    private Integer sort;
}
