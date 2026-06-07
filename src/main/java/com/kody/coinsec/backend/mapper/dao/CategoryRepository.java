package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByUserIdAndIsDeletedFalseOrderBySortAsc(Long userId);

    List<CategoryEntity> findByUserIdAndTypeAndIsDeletedFalseOrderBySortAsc(
            Long userId, String type);

    Optional<CategoryEntity> findByUserIdAndNameAndTypeAndIsDeletedFalse(
            Long userId, String name, String type);
}
