package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.RecordEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecordSpecification {

    public static Specification<RecordEntity> withFilters(
            Long userId, Long categoryId, String type,
            LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("userId"), userId));
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
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

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
