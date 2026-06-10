package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.TagRequest;
import com.kody.coinsec.backend.dto.TagResponse;
import com.kody.coinsec.backend.entity.model.TagEntity;
import com.kody.coinsec.backend.mapper.dao.TagRepository;
import com.kody.coinsec.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> getTags() {
        long userId = StpUtil.getLoginIdAsLong();
        return tagRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TagResponse createTag(TagRequest request) {
        long userId = StpUtil.getLoginIdAsLong();

        TagEntity entity = TagEntity.builder()
                .userId(userId)
                .name(request.getName())
                .color(request.getColor())
                .build();

        TagEntity saved = tagRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void updateTag(Long id, TagRequest request) {
        TagEntity entity = findById(id);

        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getColor() != null) {
            entity.setColor(request.getColor());
        }

        tagRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        TagEntity entity = findById(id);
        entity.setIsDeleted(true);
        tagRepository.save(entity);
    }

    private TagEntity findById(Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        return tagRepository.findByUserIdAndTagIdAndIsDeletedFalse(userId, id)
                .orElseThrow(() -> new BusinessException(404, "标签不存在"));
    }

    private TagResponse toResponse(TagEntity entity) {
        return TagResponse.builder()
                .tagId(entity.getTagId())
                .name(entity.getName())
                .color(entity.getColor())
                .build();
    }
}
