package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class TagRequest {

    @Schema(description = "标签名称", example = "工作餐")
    private String name;

    @Schema(description = "标签颜色（HEX 色值）", example = "#FF6B6B")
    private String color;

    @Schema(description = "标签 ID 列表（用于记录-标签关联）")
    private List<Long> tagIds;
}
