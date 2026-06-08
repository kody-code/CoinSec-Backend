package com.kody.coinsec.backend.entity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    @Schema(description = "账户 ID")
    private Long accountId;

    @Column(name = "user_id", nullable = false)
    @Schema(hidden = true)
    private Long userId;

    @Column(nullable = false, length = 30)
    @Schema(description = "账户名称")
    private String name;

    @Column(length = 255)
    @Schema(description = "图标文件名")
    private String icon;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "余额")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "状态: 1-启用, 0-禁用")
    private Integer status = 1;

    @Column(name = "is_deleted")
    @Builder.Default
    @Schema(hidden = true)
    private Boolean isDeleted = false;
}
