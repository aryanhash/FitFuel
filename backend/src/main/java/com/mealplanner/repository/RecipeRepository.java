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
    
    /**
     * Find recipes by category
     */
    List<Recipe> findByCategory(String category);
    
    /**
     * Find recipes by diet type
     */
    List<Recipe> findByDietType(String dietType);
    
    /**
     * Find recipes by category and diet type
     */
    List<Recipe> findByCategoryAndDietType(String category, String dietType);
    
    /**
     * Find recipes by cuisine type
     */
    List<Recipe> findByCuisineType(String cuisineType);
    
    /**
     * Find recipes with calorie limit
     */
    List<Recipe> findByCaloriesLessThanEqual(Integer maxCalories);
    
    /**
     * Search recipes by name (case insensitive)
     */
    List<Recipe> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find recipes by source
     */
    List<Recipe> findBySource(String source);
    
    /**
     * Find recipe by external ID and source
     */
    Optional<Recipe> findByExternalIdAndSource(String externalId, String source);
    
    /**
     * Find random recipe by category
     */
    @Query(value = "SELECT * FROM recipes WHERE category = :category ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Recipe> findRandomByCategory(@Param("category") String category);
    
    /**
     * Find random recipe by category and diet type
     */
    @Query(value = "SELECT * FROM recipes WHERE category = :category AND diet_type = :dietType ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Recipe> findRandomByCategoryAndDietType(@Param("category") String category, @Param("dietType") String dietType);
    
    /**
     * Find suitable recipes based on multiple criteria
     */
    @Query("SELECT r FROM Recipe r WHERE r.category = :category AND r.calories <= :maxCalories AND r.dietType = :dietType ORDER BY FUNCTION('RANDOM')")
    List<Recipe> findSuitableRecipes(@Param("category") String category, @Param("maxCalories") Integer maxCalories, @Param("dietType") String dietType);
    
    /**
     * Find recipes by multiple criteria with limit
     */
    @Query("SELECT r FROM Recipe r WHERE r.category = :category AND r.dietType = :dietType ORDER BY FUNCTION('RANDOM')")
    List<Recipe> findRecipesByCriteria(@Param("category") String category, @Param("dietType") String dietType, org.springframework.data.domain.Pageable pageable);
    
    /**
     * Find recipes by ingredients (contains any of the ingredients)
     */
    @Query(value = "SELECT * FROM recipes WHERE :ingredient = ANY(ingredients)", nativeQuery = true)
    List<Recipe> findByIngredient(@Param("ingredient") String ingredient);
    
    /**
     * Find recipes by cooking time
     */
    List<Recipe> findByTotalTimeLessThanEqual(Integer maxTime);
    
    /**
     * Find recipes by difficulty level
     */
    List<Recipe> findByDifficultyLevel(String difficultyLevel);
    
    /**
     * Find recipes with images
     */
    List<Recipe> findByImageUrlIsNotNull();
    
    /**
     * Find recently updated recipes
     */
    List<Recipe> findTop10ByOrderByLastUpdatedDesc();
    
    /**
     * Count recipes by category
     */
    long countByCategory(String category);
    
    /**
     * Count recipes by diet type
     */
    long countByDietType(String dietType);
    
    /**
     * Count recipes by source
     */
    long countBySource(String source);
}
