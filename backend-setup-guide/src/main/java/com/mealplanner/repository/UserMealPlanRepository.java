package com.mealplanner.repository;

import com.mealplanner.entity.User;
import com.mealplanner.entity.UserMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMealPlanRepository extends JpaRepository<UserMealPlan, Long> {
    
    // Find meal plans by user and date
    List<UserMealPlan> findByUserAndDate(User user, LocalDate date);
    
    // Find meal plans by user, date, and meal type
    Optional<UserMealPlan> findByUserAndDateAndMealType(User user, LocalDate date, String mealType);
    
    // Find meal plans by user and year
    List<UserMealPlan> findByUserAndYear(User user, Integer year);
    
    // Find meal plans by user and meal type
    List<UserMealPlan> findByUserAndMealType(User user, String mealType);
    
    // Find favorite meal plans by user
    List<UserMealPlan> findByUserAndIsFavoriteTrue(User user);
    
    // Find meal plans by user in date range
    @Query("SELECT ump FROM UserMealPlan ump WHERE ump.user = :user AND ump.date BETWEEN :startDate AND :endDate")
    List<UserMealPlan> findByUserAndDateBetween(@Param("user") User user, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    // Find meal plans by user and rating
    List<UserMealPlan> findByUserAndRating(User user, Integer rating);
    
    // Count meal plans by user and meal type
    long countByUserAndMealType(User user, String mealType);
    
    // Find most recent meal plans by user
    @Query("SELECT ump FROM UserMealPlan ump WHERE ump.user = :user ORDER BY ump.date DESC")
    List<UserMealPlan> findMostRecentByUser(@Param("user") User user);
    
    // Find meal plans by user and month
    @Query("SELECT ump FROM UserMealPlan ump WHERE ump.user = :user AND YEAR(ump.date) = :year AND MONTH(ump.date) = :month")
    List<UserMealPlan> findByUserAndMonth(@Param("user") User user, 
                                         @Param("year") Integer year, 
                                         @Param("month") Integer month);
}
