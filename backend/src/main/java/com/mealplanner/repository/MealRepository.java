package com.mealplanner.repository;

import com.mealplanner.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    
    /**
     * Find meals by meal type
     */
    List<Meal> findByMealType(String mealType);
    
    /**
     * Find meals by diet type
     */
    List<Meal> findByDietType(String dietType);
    
    /**
     * Find meals by cuisine type
     */
    List<Meal> findByCuisineType(String cuisineType);
    
    /**
     * Find meals with calorie limit
     */
    List<Meal> findByCaloriesLessThanEqual(Integer maxCalories);
    
    /**
     * Search meals by name (case insensitive)
     */
    List<Meal> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find a random meal by type
     */
    @Query(value = "SELECT * FROM meals WHERE meal_type = :mealType ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Meal> findRandomByMealType(@Param("mealType") String mealType);
    
    /**
     * Find suitable meals based on criteria
     */
    @Query("SELECT m FROM Meal m WHERE m.mealType = :mealType " +
           "AND (m.calories IS NULL OR m.calories <= :maxCalories) " +
           "AND (m.dietType = :dietType OR :dietType IS NULL)")
    List<Meal> findSuitableMeals(@Param("mealType") String mealType, 
                                 @Param("maxCalories") Integer maxCalories, 
                                 @Param("dietType") String dietType);
    
    /**
     * Find meals by multiple criteria
     */
    @Query("SELECT m FROM Meal m WHERE " +
           "(:mealType IS NULL OR m.mealType = :mealType) AND " +
           "(:dietType IS NULL OR m.dietType = :dietType) AND " +
           "(:cuisineType IS NULL OR m.cuisineType = :cuisineType) AND " +
           "(:maxCalories IS NULL OR m.calories <= :maxCalories)")
    List<Meal> findByMultipleCriteria(@Param("mealType") String mealType,
                                     @Param("dietType") String dietType,
                                     @Param("cuisineType") String cuisineType,
                                     @Param("maxCalories") Integer maxCalories);
} 