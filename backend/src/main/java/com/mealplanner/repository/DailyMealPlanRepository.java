package com.mealplanner.repository;

import com.mealplanner.entity.DailyMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMealPlanRepository extends JpaRepository<DailyMealPlan, Long> {
    
    /**
     * Find meal plans by date and year
     */
    List<DailyMealPlan> findByMealDateAndCreatedForYear(LocalDate mealDate, Integer createdForYear);
    
    /**
     * Find meal plans by date range and year
     */
    List<DailyMealPlan> findByMealDateBetweenAndCreatedForYear(LocalDate startDate, LocalDate endDate, Integer createdForYear);
    
    /**
     * Find meal plans by meal type and year
     */
    List<DailyMealPlan> findByMealTypeAndCreatedForYear(String mealType, Integer createdForYear);
    
    /**
     * Check if meal plan exists for date, meal type, and year
     */
    boolean existsByMealDateAndMealTypeAndCreatedForYear(LocalDate mealDate, String mealType, Integer createdForYear);
    
    /**
     * Find meal plan by date, meal type, and year
     */
    List<DailyMealPlan> findByMealDateAndMealTypeAndCreatedForYear(LocalDate mealDate, String mealType, Integer createdForYear);
    
    /**
     * Delete meal plans by year
     */
    @Modifying
    @Query("DELETE FROM DailyMealPlan d WHERE d.createdForYear = :year")
    void deleteByCreatedForYear(@Param("year") Integer year);
    
    /**
     * Find meal plans by month and year
     */
    @Query("SELECT d FROM DailyMealPlan d WHERE YEAR(d.mealDate) = :year AND MONTH(d.mealDate) = :month")
    List<DailyMealPlan> findByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);
    
    /**
     * Count meal plans by year
     */
    long countByCreatedForYear(Integer createdForYear);
    
    /**
     * Find meal plans by meal type, date range, and year
     */
    @Query("SELECT d FROM DailyMealPlan d WHERE d.mealType = :mealType " +
           "AND d.mealDate BETWEEN :startDate AND :endDate " +
           "AND d.createdForYear = :year")
    List<DailyMealPlan> findByMealTypeAndDateRangeAndYear(@Param("mealType") String mealType,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate,
                                                          @Param("year") Integer year);
    
    /**
     * Count meal plans by meal type and year
     */
    long countByMealTypeAndCreatedForYear(String mealType, Integer createdForYear);
    
    /**
     * Find earliest meal date by year
     */
    @Query("SELECT MIN(d.mealDate) FROM DailyMealPlan d WHERE d.createdForYear = :year")
    Optional<LocalDate> findEarliestMealDateByYear(@Param("year") Integer year);
    
    /**
     * Find latest meal date by year
     */
    @Query("SELECT MAX(d.mealDate) FROM DailyMealPlan d WHERE d.createdForYear = :year")
    Optional<LocalDate> findLatestMealDateByYear(@Param("year") Integer year);
    
    /**
     * Find meal plans with null meal by year
     */
    @Query("SELECT d FROM DailyMealPlan d WHERE d.meal IS NULL AND d.createdForYear = :year")
    List<DailyMealPlan> findByMealIsNullAndCreatedForYear(@Param("year") Integer year);
} 