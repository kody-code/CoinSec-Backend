package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<TagEntity, Long> {

    List<TagEntity> findByUserIdAndIsDeletedFalse(Long userId);

    Optional<TagEntity> findByUserIdAndTagIdAndIsDeletedFalse(Long userId, Long tagId);
}
