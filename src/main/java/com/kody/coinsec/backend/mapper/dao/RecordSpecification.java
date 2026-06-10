package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.RecordEntity;
import com.kody.coinsec.backend.entity.model.TagEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecordSpecification {

    public static Specification<RecordEntity> withFilters(
            Long userId, List<Long> categoryIds, String type,
            LocalDateTime startDate, LocalDateTime endDate, Long accountId,
            String keyword, List<Long> tagIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("userId"), userId));
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(root.get("categoryId").in(categoryIds));
            }
            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("recordTime"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("recordTime"), endDate));
            }
            if (accountId != null) {
                predicates.add(cb.equal(root.get("accountId"), accountId));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("remark")), "%" + keyword.toLowerCase() + "%"));
            }
            if (tagIds != null && !tagIds.isEmpty()) {
                Join<RecordEntity, TagEntity> tagJoin = root.join("tags");
                predicates.add(tagJoin.get("tagId").in(tagIds));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
