package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @Schema(description = "旧密码", example = "123456")
    private String oldPassword;

    @Schema(description = "新密码", example = "654321")
    private String newPassword;
}
