package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.AuthResponse;
import com.kody.coinsec.backend.dto.LoginRequest;
import com.kody.coinsec.backend.dto.SetupRequest;
import com.kody.coinsec.backend.entity.model.CategoryEntity;
import com.kody.coinsec.backend.entity.model.UserEntity;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import com.kody.coinsec.backend.mapper.dao.UserRepository;
import com.kody.coinsec.backend.service.AuthService;
import com.kody.coinsec.backend.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private static final List<CategoryTemplate> DEFAULT_EXPENSES = List.of(
            new CategoryTemplate("餐饮", "restaurant", 1),
            new CategoryTemplate("交通", "directions_car", 2),
            new CategoryTemplate("购物", "shopping_cart", 3),
            new CategoryTemplate("日用", "inventory_2", 4),
            new CategoryTemplate("住房", "home", 5),
            new CategoryTemplate("通讯", "phone_android", 6),
            new CategoryTemplate("医疗", "local_hospital", 7),
            new CategoryTemplate("教育", "school", 8),
            new CategoryTemplate("娱乐", "sports_esports", 9),
            new CategoryTemplate("服饰", "checkroom", 10),
            new CategoryTemplate("其他", "more_horiz", 11)
    );

    private static final List<CategoryTemplate> DEFAULT_INCOMES = List.of(
            new CategoryTemplate("工资", "payments", 1),
            new CategoryTemplate("奖金", "redeem", 2),
            new CategoryTemplate("理财", "trending_up", 3),
            new CategoryTemplate("兼职", "work", 4),
            new CategoryTemplate("红包", "card_giftcard", 5),
            new CategoryTemplate("其他", "more_horiz", 6)
    );

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

        createDefaultCategories(saved.getUserId());

        StpUtil.login(saved.getUserId());
        String token = StpUtil.getTokenValue();

        return AuthResponse.builder()
                .userId(saved.getUserId())
                .token(token)
                .build();
    }

    private void createDefaultCategories(Long userId) {
        List<CategoryEntity> defaults = new ArrayList<>();
        DEFAULT_EXPENSES.forEach(t -> defaults.add(CategoryEntity.builder()
                .userId(userId).name(t.name).type("expense").icon(t.icon).sort(t.sort).build()));
        DEFAULT_INCOMES.forEach(t -> defaults.add(CategoryEntity.builder()
                .userId(userId).name(t.name).type("income").icon(t.icon).sort(t.sort).build()));
        categoryRepository.saveAll(defaults);
    }

    private record CategoryTemplate(String name, String icon, int sort) {}

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
