package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.UpdateNicknameRequest;
import com.kody.coinsec.backend.dto.UpdatePasswordRequest;
import com.kody.coinsec.backend.entity.model.UserEntity;
import com.kody.coinsec.backend.mapper.dao.UserRepository;
import com.kody.coinsec.backend.service.UserService;
import com.kody.coinsec.backend.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public UserEntity getUserInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    @Override
    public void updateNickname(UpdateNicknameRequest request) {
        UserEntity user = getUserInfo();
        user.setNickname(request.getNickname());
        userRepository.save(user);
    }

    @Override
    public void updatePassword(UpdatePasswordRequest request) {
        UserEntity user = getUserInfo();

        if (!PasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(PasswordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "avatar_" + UUID.randomUUID() + ext;

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            UserEntity user = getUserInfo();
            user.setAvatar("/uploads/" + filename);
            userRepository.save(user);

            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new BusinessException("头像上传失败");
        }
    }
}
