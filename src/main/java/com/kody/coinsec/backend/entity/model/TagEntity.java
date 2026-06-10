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
@Table(name = "tags")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    @Schema(description = "标签 ID")
    private Long tagId;

    @Column(name = "user_id", nullable = false)
    @Schema(hidden = true)
    private Long userId;

    @Column(nullable = false, length = 30)
    @Schema(description = "标签名称")
    private String name;

    @Column(length = 20)
    @Schema(description = "标签颜色（HEX 色值）")
    private String color;

    @Column(name = "is_deleted")
    @Builder.Default
    @Schema(hidden = true)
    private Boolean isDeleted = false;
}
