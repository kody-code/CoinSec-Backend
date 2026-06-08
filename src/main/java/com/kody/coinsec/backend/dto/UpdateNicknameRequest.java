package com.kody.coinsec.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateNicknameRequest {

    @Schema(description = "新昵称", example = "小明")
    private String nickname;
}
