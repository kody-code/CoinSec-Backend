package com.kody.coinsec.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.kody.coinsec.backend.common.exception.BusinessException;
import com.kody.coinsec.backend.dto.TagRequest;
import com.kody.coinsec.backend.entity.model.TagEntity;
import com.kody.coinsec.backend.mapper.dao.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    @Test
    @DisplayName("获取标签列表-成功")
    void getTags_Success() {
        when(tagRepository.findByUserIdAndIsDeletedFalse(1L))
                .thenReturn(List.of(createTagEntity(1L, "工作餐")));

        var result = tagService.getTags();

        assertEquals(1, result.size());
        assertEquals("工作餐", result.getFirst().getName());
    }

    @Test
    @DisplayName("创建标签-成功")
    void createTag_Success() {
        when(tagRepository.save(any())).thenAnswer(invocation -> {
            TagEntity saved = invocation.getArgument(0);
            return TagEntity.builder()
                    .tagId(1L)
                    .userId(saved.getUserId())
                    .name(saved.getName())
                    .color(saved.getColor())
                    .build();
        });

        TagRequest request = new TagRequest();
        request.setName("工作餐");
        request.setColor("#FF6B6B");

        var result = tagService.createTag(request);

        assertEquals(1L, result.getTagId());
        assertEquals("工作餐", result.getName());
        assertEquals("#FF6B6B", result.getColor());
    }

    @Test
    @DisplayName("删除标签-逻辑删除")
    void deleteTag_Success() {
        when(tagRepository.findByUserIdAndTagIdAndIsDeletedFalse(1L, 1L))
                .thenReturn(Optional.of(createTagEntity(1L, "工作餐")));

        tagService.deleteTag(1L);

        verify(tagRepository).save(any());
    }

    @Test
    @DisplayName("删除标签-不存在时抛出异常")
    void deleteTag_NotFound_ThrowsException() {
        when(tagRepository.findByUserIdAndTagIdAndIsDeletedFalse(1L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> tagService.deleteTag(99L));
    }

    private TagEntity createTagEntity(Long id, String name) {
        return TagEntity.builder()
                .tagId(id)
                .userId(1L)
                .name(name)
                .color("#FF6B6B")
                .build();
    }
}
