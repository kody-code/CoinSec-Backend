package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameAndIsDeletedFalse(String username);

    long countByIsDeletedFalse();
}
