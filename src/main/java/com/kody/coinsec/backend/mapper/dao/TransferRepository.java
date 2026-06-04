package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransferRepository extends JpaRepository<TransferEntity, Long>,
        JpaSpecificationExecutor<TransferEntity> {
}
