package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.dto.StatisticsResponse;
import com.kody.coinsec.backend.entity.model.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, Long>,
        JpaSpecificationExecutor<RecordEntity> {

    @Query("""
            SELECT new com.kody.coinsec.backend.dto.StatisticsResponse$CategoryStat(
                r.categoryId, c.name, c.type, SUM(r.amount), COUNT(r))
            FROM RecordEntity r
            JOIN CategoryEntity c ON r.categoryId = c.categoryId
            WHERE r.userId = :userId AND r.isDeleted = false
                AND r.recordTime BETWEEN :startDate AND :endDate
            GROUP BY r.categoryId, c.name, c.type
            ORDER BY SUM(r.amount) DESC
            """)
    List<StatisticsResponse.CategoryStat> findCategoryStats(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM RecordEntity r
            WHERE r.userId = :userId AND r.isDeleted = false
                AND r.type = :type
                AND r.recordTime BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumByTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
