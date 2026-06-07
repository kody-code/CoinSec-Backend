package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<TransferEntity, Long>,
        JpaSpecificationExecutor<TransferEntity> {

    List<TransferEntity> findByUserIdAndFromAccountIdAndIsDeletedFalse(Long userId, Long fromAccountId);

    List<TransferEntity> findByUserIdAndToAccountIdAndIsDeletedFalse(Long userId, Long toAccountId);

    Optional<TransferEntity> findByUserIdAndFromAccountIdAndAmountAndTransferTimeAndIsDeletedFalse(
            Long userId, Long fromAccountId, BigDecimal amount, LocalDateTime transferTime);

    Optional<TransferEntity> findByUserIdAndToAccountIdAndAmountAndTransferTimeAndIsDeletedFalse(
            Long userId, Long toAccountId, BigDecimal amount, LocalDateTime transferTime);
}
