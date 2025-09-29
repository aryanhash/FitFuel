package com.mealplanner.repository;

import com.mealplanner.entity.User;
import com.mealplanner.entity.UserMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMealPlanRepository extends JpaRepository<UserMealPlan, Long> {
    
    /**
     * Find meal plans by user and date
     */
    List<UserMealPlan> findByUserAndDate(User user, LocalDate date);
    
    /**
     * Find meal plans by user and date range
     */
    List<UserMealPlan> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find meal plan by user, date, and meal type
     */
    Optional<UserMealPlan> findByUserAndDateAndMealType(User user, LocalDate date, String mealType);
    
    /**
     * Find all meal plans for a user
     */
    List<UserMealPlan> findByUser(User user);
    
    /**
     * Find meal plans by user and meal type
     */
    List<UserMealPlan> findByUserAndMealType(User user, String mealType);
    
    /**
     * Find favorite meal plans for a user
     */
    List<UserMealPlan> findByUserAndIsFavoriteTrue(User user);
    
    /**
     * Find meal plans by user and rating
     */
    List<UserMealPlan> findByUserAndRating(User user, Integer rating);
    
    /**
     * Find meal plans by user and minimum rating
     */
    List<UserMealPlan> findByUserAndRatingGreaterThanEqual(User user, Integer minRating);
    
    /**
     * Count meal plans by user and date
     */
    long countByUserAndDate(User user, LocalDate date);
    
    /**
     * Check if meal plan exists for user, date, and meal type
     */
    boolean existsByUserAndDateAndMealType(User user, LocalDate date, String mealType);
    
    /**
     * Delete meal plans by user and date
     */
    void deleteByUserAndDate(User user, LocalDate date);
    
    /**
     * Delete meal plan by user, date, and meal type
     */
    void deleteByUserAndDateAndMealType(User user, LocalDate date, String mealType);
}