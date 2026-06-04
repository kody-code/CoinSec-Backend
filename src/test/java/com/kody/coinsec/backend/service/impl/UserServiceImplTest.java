package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.UpdateNicknameRequest;
import com.kody.coinsec.backend.dto.UpdatePasswordRequest;
import com.kody.coinsec.backend.entity.model.UserEntity;
import com.kody.coinsec.backend.mapper.dao.UserRepository;
import com.kody.coinsec.backend.util.PasswordEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private MockedStatic<StpUtil> stpUtilMock;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

        mockUser = UserEntity.builder()
                .userId(1L)
                .username("admin")
                .password(PasswordEncoder.encode("123456"))
                .nickname("管理员")
                .avatar(null)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    @Test
    @DisplayName("获取用户信息-成功")
    void getUserInfo_Success() {
        UserEntity result = userService.getUserInfo();
        assertEquals(1L, result.getUserId());
        assertEquals("admin", result.getUsername());
        assertEquals("管理员", result.getNickname());
    }

    @Test
    @DisplayName("获取用户信息-用户不存在时抛出异常")
    void getUserInfo_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.getUserInfo());
    }

    @Test
    @DisplayName("修改昵称-成功")
    void updateNickname_Success() {
        UpdateNicknameRequest request = new UpdateNicknameRequest();
        request.setNickname("新昵称");

        userService.updateNickname(request);

        assertEquals("新昵称", mockUser.getNickname());
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("修改密码-成功")
    void updatePassword_Success() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("newpass");

        userService.updatePassword(request);

        assertTrue(PasswordEncoder.matches("newpass", mockUser.getPassword()));
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("修改密码-原密码错误时抛出异常")
    void updatePassword_WrongOldPassword_ThrowsException() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("newpass");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.updatePassword(request));
        assertEquals("原密码错误", ex.getMessage());
    }
}
