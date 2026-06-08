package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SetupRequest {

    @Schema(description = "管理员用户名", example = "admin")
    private String username;

    @Schema(description = "管理员密码", example = "123456")
    private String password;
}
