package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.UpdateNicknameRequest;
import com.kody.coinsec.backend.dto.UpdatePasswordRequest;
import com.kody.coinsec.backend.entity.model.UserEntity;
import com.kody.coinsec.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public Result<UserEntity> getInfo() {
        return Result.success(userService.getUserInfo());
    }

    @PutMapping("/nickname")
    public Result<Void> updateNickname(@RequestBody UpdateNicknameRequest request) {
        userService.updateNickname(request);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return Result.success();
    }

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(file);
        return Result.success(Map.of("avatarUrl", avatarUrl));
    }
}
