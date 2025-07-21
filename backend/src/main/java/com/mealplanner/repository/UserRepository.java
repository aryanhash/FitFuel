package com.mealplanner.repository;

import com.mealplanner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find user by email and password hash (for authentication)
     */
    Optional<User> findByEmailAndPasswordHash(String email, String passwordHash);
    
    /**
     * Find users by diet preference
     */
    @Query("SELECT u FROM User u WHERE u.dietPreference = :dietPreference")
    java.util.List<User> findByDietPreference(@Param("dietPreference") User.DietPreference dietPreference);
} 