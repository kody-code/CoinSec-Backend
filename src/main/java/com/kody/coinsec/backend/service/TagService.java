package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.TagRequest;
import com.kody.coinsec.backend.dto.TagResponse;

import java.util.List;

public interface TagService {

    List<TagResponse> getTags();

    TagResponse createTag(TagRequest request);

    void updateTag(Long id, TagRequest request);

    void deleteTag(Long id);
}
