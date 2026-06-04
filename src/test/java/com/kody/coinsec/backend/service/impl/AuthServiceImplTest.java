package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.AuthResponse;
import com.kody.coinsec.backend.dto.LoginRequest;
import com.kody.coinsec.backend.dto.SetupRequest;
import com.kody.coinsec.backend.entity.model.UserEntity;
import com.kody.coinsec.backend.mapper.dao.AccountRepository;
import com.kody.coinsec.backend.mapper.dao.CategoryRepository;
import com.kody.coinsec.backend.mapper.dao.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    @Test
    @DisplayName("首次初始化-成功创建管理员")
    void setup_Success() {
        when(userRepository.countByIsDeletedFalse()).thenReturn(0L);
        when(userRepository.save(any())).thenAnswer(invocation -> {
            UserEntity saved = invocation.getArgument(0);
            return UserEntity.builder()
                    .userId(1L)
                    .username(saved.getUsername())
                    .password(saved.getPassword())
                    .nickname(saved.getNickname())
                    .createTime(saved.getCreateTime())
                    .build();
        });
        stpUtilMock.when(StpUtil::getTokenValue).thenReturn("mock-token");

        SetupRequest request = new SetupRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        AuthResponse response = authService.setup(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("mock-token", response.getToken());
        verify(userRepository).save(any());
        stpUtilMock.verify(() -> StpUtil.login(1L));
    }

    @Test
    @DisplayName("首次初始化-管理员已存在时抛出异常")
    void setup_AlreadyExists_ThrowsException() {
        when(userRepository.countByIsDeletedFalse()).thenReturn(1L);

        SetupRequest request = new SetupRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.setup(request));
        assertEquals("管理员已存在，不能重复初始化", ex.getMessage());
    }

    @Test
    @DisplayName("登录-用户名密码正确时返回token")
    void login_Success() {
        UserEntity user = UserEntity.builder()
                .userId(1L)
                .username("admin")
                .password(com.kody.coinsec.backend.util.PasswordEncoder.encode("123456"))
                .nickname("管理员")
                .build();
        when(userRepository.findByUsernameAndIsDeletedFalse("admin"))
                .thenReturn(Optional.of(user));
        stpUtilMock.when(StpUtil::getTokenValue).thenReturn("mock-token");

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("mock-token", response.getToken());
        assertNotNull(response.getUserInfo());
        assertEquals("admin", response.getUserInfo().getUsername());
        stpUtilMock.verify(() -> StpUtil.login(1L));
    }

    @Test
    @DisplayName("登录-用户名不存在时抛出异常")
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsernameAndIsDeletedFalse("wrong"))
                .thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setUsername("wrong");
        request.setPassword("123456");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(request));
        assertEquals(401, ex.getCode());
    }

    @Test
    @DisplayName("登录-密码错误时抛出异常")
    void login_WrongPassword_ThrowsException() {
        UserEntity user = UserEntity.builder()
                .userId(1L)
                .username("admin")
                .password(com.kody.coinsec.backend.util.PasswordEncoder.encode("correct"))
                .build();
        when(userRepository.findByUsernameAndIsDeletedFalse("admin"))
                .thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(request));
        assertEquals(401, ex.getCode());
    }

    @Test
    @DisplayName("登出-成功")
    void logout_Success() {
        authService.logout();
        stpUtilMock.verify(StpUtil::logout);
    }
}
