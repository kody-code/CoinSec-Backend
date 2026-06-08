package com.kody.coinsec.backend.controller;

import com.kody.coinsec.backend.common.result.Result;
import com.kody.coinsec.backend.dto.UpdateNicknameRequest;
import com.kody.coinsec.backend.dto.UpdatePasswordRequest;
import com.kody.coinsec.backend.entity.model.UserEntity;
import com.kody.coinsec.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "个人信息查看与修改")
@SecurityRequirement(name = "satoken")
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取个人信息", description = "获取当前登录用户的信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回用户信息"),
            @ApiResponse(responseCode = "401", description = "未登录或 token 已过期")
    })
    @GetMapping("/info")
    public Result<UserEntity> getInfo() {
        return Result.success(userService.getUserInfo());
    }

    @Operation(summary = "修改昵称", description = "修改当前用户的显示昵称")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "未登录或 token 已过期")
    })
    @PutMapping("/nickname")
    public Result<Void> updateNickname(@RequestBody UpdateNicknameRequest request) {
        userService.updateNickname(request);
        return Result.success();
    }

    @Operation(summary = "修改密码", description = "需要提供旧密码验证身份")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "未登录或旧密码错误")
    })
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return Result.success();
    }

    @Operation(summary = "上传头像", description = "上传用户头像图片文件")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "上传成功，返回头像 URL"),
            @ApiResponse(responseCode = "401", description = "未登录或 token 已过期")
    })
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(
            @Parameter(description = "头像图片文件 (jpg/png, 最大 5MB)") @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(file);
        return Result.success(Map.of("avatarUrl", avatarUrl));
    }
}
