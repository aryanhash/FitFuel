package com.mealplanner.repository;

import com.mealplanner.entity.User;
import com.mealplanner.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    
    /**
     * Find user preferences by user
     */
    Optional<UserPreferences> findByUser(User user);
    
    /**
     * Find user preferences by user ID
     */
    Optional<UserPreferences> findByUserId(Long userId);
    
    /**
     * Check if user preferences exist for a user
     */
    boolean existsByUser(User user);
    
    /**
     * Check if user preferences exist for a user ID
     */
    boolean existsByUserId(Long userId);
} 