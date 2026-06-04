package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.entity.model.TransferEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransferSpecification {

    public static Specification<TransferEntity> withFilters(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transferTime"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transferTime"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
