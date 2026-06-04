package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.AuthResponse;
import com.kody.coinsec.backend.dto.LoginRequest;
import com.kody.coinsec.backend.dto.SetupRequest;
import com.kody.coinsec.backend.entity.model.UserEntity;
import com.kody.coinsec.backend.mapper.dao.UserRepository;
import com.kody.coinsec.backend.service.AuthService;
import com.kody.coinsec.backend.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthResponse setup(SetupRequest request) {
        if (userRepository.countByIsDeletedFalse() > 0) {
            throw new BusinessException("管理员已存在，不能重复初始化");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(PasswordEncoder.encode(request.getPassword()))
                .nickname(request.getUsername())
                .createTime(LocalDateTime.now())
                .build();
        UserEntity saved = userRepository.save(user);

        StpUtil.login(saved.getUserId());
        String token = StpUtil.getTokenValue();

        return AuthResponse.builder()
                .userId(saved.getUserId())
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsernameAndIsDeletedFalse(request.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if (!PasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        StpUtil.login(user.getUserId());
        String token = StpUtil.getTokenValue();

        return AuthResponse.builder()
                .userId(user.getUserId())
                .token(token)
                .userInfo(AuthResponse.UserInfo.builder()
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .build())
                .build();
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }
}
