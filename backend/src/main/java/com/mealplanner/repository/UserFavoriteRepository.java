package com.mealplanner.repository;

import com.mealplanner.entity.Recipe;
import com.mealplanner.entity.User;
import com.mealplanner.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    
    /**
     * Find all favorites for a user
     */
    List<UserFavorite> findByUser(User user);
    
    /**
     * Find specific favorite by user and recipe
     */
    Optional<UserFavorite> findByUserAndRecipe(User user, Recipe recipe);
    
    /**
     * Check if recipe is favorited by user
     */
    boolean existsByUserAndRecipe(User user, Recipe recipe);
    
    /**
     * Delete favorite by user and recipe
     */
    void deleteByUserAndRecipe(User user, Recipe recipe);
}
