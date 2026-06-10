package com.kody.coinsec.backend.mapper.dao;

import com.kody.coinsec.backend.dto.StatisticsResponse;
import com.kody.coinsec.backend.entity.model.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kody.coinsec.backend.dto.AnnualStatisticsResponse;
import com.kody.coinsec.backend.dto.MonthlyStatisticsResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, Long>,
        JpaSpecificationExecutor<RecordEntity> {

    List<RecordEntity> findByUserIdAndAccountIdAndIsDeletedFalse(Long userId, Long accountId);

    List<RecordEntity> findByUserIdAndAccountIdAndAmountAndRecordTimeAndTypeAndIsDeletedFalse(
            Long userId, Long accountId, BigDecimal amount, LocalDateTime recordTime, String type);

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
                AND r.type = 'expense'
                AND r.categoryId = :categoryId
                AND r.recordTime BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumExpenseByCategoryAndDateRange(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
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

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM RecordEntity r
            WHERE r.userId = :userId AND r.isDeleted = false
                AND r.type = :type
                AND r.accountId = :accountId
                AND r.recordTime BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumByTypeAndDateRangeAndAccountId(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("accountId") Long accountId);

    @Query("""
            SELECT new com.kody.coinsec.backend.dto.StatisticsResponse$CategoryStat(
                r.categoryId, c.name, c.type, SUM(r.amount), COUNT(r))
            FROM RecordEntity r
            JOIN CategoryEntity c ON r.categoryId = c.categoryId
            WHERE r.userId = :userId AND r.isDeleted = false
                AND r.accountId = :accountId
                AND r.recordTime BETWEEN :startDate AND :endDate
            GROUP BY r.categoryId, c.name, c.type
            ORDER BY SUM(r.amount) DESC
            """)
    List<StatisticsResponse.CategoryStat> findCategoryStatsByAccountId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("accountId") Long accountId);

    @Query(value = """
            SELECT new com.kody.coinsec.backend.dto.MonthlyStatisticsResponse(
                cast(extract(MONTH from r.recordTime) as int),
                COALESCE(SUM(CASE WHEN r.type = 'income' THEN r.amount ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN r.type = 'expense' THEN r.amount ELSE 0 END), 0)
            )
            FROM RecordEntity r
            WHERE r.userId = :userId AND r.isDeleted = false
                AND extract(YEAR from r.recordTime) = :year
            GROUP BY extract(MONTH from r.recordTime)
            ORDER BY extract(MONTH from r.recordTime)
            """)
    List<MonthlyStatisticsResponse> findMonthlyStatistics(
            @Param("userId") Long userId,
            @Param("year") Integer year);

    @Query(value = """
            SELECT new com.kody.coinsec.backend.dto.AnnualStatisticsResponse(
                cast(extract(YEAR from r.recordTime) as int),
                COALESCE(SUM(CASE WHEN r.type = 'income' THEN r.amount ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN r.type = 'expense' THEN r.amount ELSE 0 END), 0)
            )
            FROM RecordEntity r
            WHERE r.userId = :userId AND r.isDeleted = false
                AND extract(YEAR from r.recordTime) BETWEEN :startYear AND :endYear
            GROUP BY extract(YEAR from r.recordTime)
            ORDER BY extract(YEAR from r.recordTime)
            """)
    List<AnnualStatisticsResponse> findAnnualStatistics(
            @Param("userId") Long userId,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear);

    @Query("""
            SELECT r.recordId, t.tagId
            FROM RecordEntity r
            JOIN r.tags t
            WHERE r.recordId IN :recordIds
            """)
    List<Object[]> findTagIdsByRecordIds(@Param("recordIds") List<Long> recordIds);
}
