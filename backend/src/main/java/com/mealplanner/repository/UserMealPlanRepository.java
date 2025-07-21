package com.mealplanner.repository;

import com.mealplanner.entity.UserMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserMealPlanRepository extends JpaRepository<UserMealPlan, Long> {
    
    /**
     * Find meal plans by user and year
     */
    List<UserMealPlan> findByUserIdAndYear(Long userId, Integer year);
    
    /**
     * Find meal plans by user, year, and month
     */
    @Query("SELECT ump FROM UserMealPlan ump WHERE ump.user.id = :userId AND ump.year = :year AND EXTRACT(MONTH FROM ump.date) = :month ORDER BY ump.date, ump.mealTime")
    List<UserMealPlan> findByUserIdAndYearAndMonth(@Param("userId") Long userId, @Param("year") Integer year, @Param("month") Integer month);
    
    /**
     * Find meal plans by user and date range
     */
    @Query("SELECT ump FROM UserMealPlan ump WHERE ump.user.id = :userId AND ump.date BETWEEN :startDate AND :endDate ORDER BY ump.date, ump.mealTime")
    List<UserMealPlan> findByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find meal plans by user, date, and meal time
     */
    List<UserMealPlan> findByUserIdAndDateAndMealTime(Long userId, LocalDate date, UserMealPlan.MealTime mealTime);
    
    /**
     * Find meal plans by user and specific date
     */
    List<UserMealPlan> findByUserIdAndDate(Long userId, LocalDate date);
    
    /**
     * Check if meal plan exists for user, date, meal time, and year
     */
    boolean existsByUserIdAndDateAndMealTimeAndYear(Long userId, LocalDate date, UserMealPlan.MealTime mealTime, Integer year);
    
    /**
     * Find eaten meals by user and date range
     */
    @Query("SELECT ump FROM UserMealPlan ump WHERE ump.user.id = :userId AND ump.isEaten = true AND ump.date BETWEEN :startDate AND :endDate")
    List<UserMealPlan> findEatenMealsByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find skipped meals by user and date range
     */
    @Query("SELECT ump FROM UserMealPlan ump WHERE ump.user.id = :userId AND ump.isSkipped = true AND ump.date BETWEEN :startDate AND :endDate")
    List<UserMealPlan> findSkippedMealsByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Count meals by user and year
     */
    long countByUserIdAndYear(Long userId, Integer year);
    
    /**
     * Count eaten meals by user and year
     */
    long countByUserIdAndYearAndIsEatenTrue(Long userId, Integer year);
    
    /**
     * Count skipped meals by user and year
     */
    long countByUserIdAndYearAndIsSkippedTrue(Long userId, Integer year);
    
    /**
     * Delete meal plans by user and year
     */
    void deleteByUserIdAndYear(Long userId, Integer year);
    
    /**
     * Find meal plans by recipe (for analytics)
     */
    List<UserMealPlan> findByRecipeId(Long recipeId);
    
    /**
     * Find meal plans by user, recipe, and year
     */
    List<UserMealPlan> findByUserIdAndRecipeIdAndYear(Long userId, Long recipeId, Integer year);
} 