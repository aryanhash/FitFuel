package com.mealplanner.repository;

import com.mealplanner.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // Find recipes by category
    List<Recipe> findByCategory(String category);
    
    // Find recipes by type (Veg/Non-Veg)
    List<Recipe> findByType(String type);
    
    // Find recipes by diet type
    List<Recipe> findByDietType(String dietType);
    
    // Find recipes by cuisine type
    List<Recipe> findByCuisineType(String cuisineType);
    
    // Find recipes by difficulty level
    List<Recipe> findByDifficultyLevel(String difficultyLevel);
    
    // Find recipes by external ID and source
    Optional<Recipe> findByExternalIdAndSource(String externalId, String source);
    
    // Search recipes by name (case insensitive)
    List<Recipe> findByNameContainingIgnoreCase(String name);
    
    // Find recipes by ingredient (using native query for PostgreSQL array)
    @Query(value = "SELECT * FROM recipes WHERE :ingredient = ANY(ingredients)", nativeQuery = true)
    List<Recipe> findByIngredient(@Param("ingredient") String ingredient);
    
    // Find recipes by multiple criteria
    List<Recipe> findByCategoryAndTypeAndDietType(String category, String type, String dietType);
    
    // Find recipes with calorie range
    @Query("SELECT r FROM Recipe r WHERE r.calories BETWEEN :minCalories AND :maxCalories")
    List<Recipe> findByCalorieRange(@Param("minCalories") Double minCalories, @Param("maxCalories") Double maxCalories);
    
    // Find recipes by prep time range
    @Query("SELECT r FROM Recipe r WHERE r.prepTimeMinutes BETWEEN :minTime AND :maxTime")
    List<Recipe> findByPrepTimeRange(@Param("minTime") Integer minTime, @Param("maxTime") Integer maxTime);
}
