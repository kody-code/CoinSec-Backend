package com.kody.coinsec.backend.entity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @Schema(description = "分类 ID")
    private Long categoryId;

    @Column(name = "user_id", nullable = false)
    @Schema(hidden = true)
    private Long userId;

    @Column(nullable = false, length = 30)
    @Schema(description = "分类名称")
    private String name;

    @Column(nullable = false, length = 10)
    @Schema(description = "类型: income(收入) / expense(支出)")
    private String type;

    @Column(length = 255)
    @Schema(description = "图标文件名")
    private String icon;

    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "排序号，越小越靠前")
    private Integer sort = 0;

    @Column(name = "is_deleted")
    @Builder.Default
    @Schema(hidden = true)
    private Boolean isDeleted = false;
}
