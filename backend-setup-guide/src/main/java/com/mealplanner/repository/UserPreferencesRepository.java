package com.mealplanner.repository;

import com.mealplanner.entity.User;
import com.mealplanner.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    
    // Find preferences by user
    Optional<UserPreferences> findByUser(User user);
    
    // Check if preferences exist for user
    boolean existsByUser(User user);
}
