package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.UpdateNicknameRequest;
import com.kody.coinsec.backend.dto.UpdatePasswordRequest;
import com.kody.coinsec.backend.entity.model.UserEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserEntity getUserInfo();

    void updateNickname(UpdateNicknameRequest request);

    void updatePassword(UpdatePasswordRequest request);

    String uploadAvatar(MultipartFile file);
}
