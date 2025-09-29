package com.mealplanner.repository;

import com.mealplanner.entity.User;
import com.mealplanner.entity.UserMealHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserMealHistoryRepository extends JpaRepository<UserMealHistory, Long> {
    
    /**
     * Find meal history for a user
     */
    List<UserMealHistory> findByUser(User user);
    
    /**
     * Find meal history for a user within date range
     */
    List<UserMealHistory> findByUserAndDateConsumedBetween(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find recent meal history for a user
     */
    List<UserMealHistory> findByUserAndDateConsumedAfter(User user, LocalDate date);
    
    /**
     * Find meal history by user and date
     */
    List<UserMealHistory> findByUserAndDateConsumed(User user, LocalDate date);
}
