package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findByUserIdAndIsDeletedFalseOrderByAccountIdAsc(Long userId);
}
