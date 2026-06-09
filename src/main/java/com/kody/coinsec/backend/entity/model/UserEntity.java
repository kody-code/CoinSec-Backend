package com.kody.coinsec.backend.entity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Schema(description = "用户 ID")
    private Long userId;

    @Column(nullable = false, length = 50)
    @Schema(description = "用户名")
    private String username;

    @Column(nullable = false, length = 100)
    @Schema(hidden = true)
    private String password;

    @Column(length = 50)
    @Schema(description = "昵称")
    private String nickname;

    @Column(length = 255)
    @Schema(description = "头像 URL")
    private String avatar;

    @Column(name = "create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "default_income_account_id")
    @Schema(description = "默认支出账户 ID")
    private Long defaultIncomeAccountId;

    @Column(name = "default_expense_account_id")
    @Schema(description = "默认收入账户 ID")
    private Long defaultExpenseAccountId;

    @Column(name = "is_deleted")
    @Schema(hidden = true)
    @Builder.Default
    private Boolean isDeleted = false;
}
