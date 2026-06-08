package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "登录 token，后续请求需在 satoken 请求头中携带")
    private String token;

    @Schema(description = "用户基本信息")
    private UserInfo userInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        @Schema(description = "用户名")
        private String username;

        @Schema(description = "昵称")
        private String nickname;
    }
}
