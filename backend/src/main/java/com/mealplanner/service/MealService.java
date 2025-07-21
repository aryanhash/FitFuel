package com.mealplanner.service;

import com.mealplanner.entity.Meal;
import com.mealplanner.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MealService {
    
    @Autowired
    private MealRepository mealRepository;
    
    /**
     * Get all meals
     */
    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }
    
    /**
     * Get meal by ID
     */
    public Optional<Meal> getMealById(Long id) {
        return mealRepository.findById(id);
    }
    
    /**
     * Get meals by type
     */
    public List<Meal> getMealsByType(String mealType) {
        return mealRepository.findByMealType(mealType);
    }
    
    /**
     * Get meals by diet type
     */
    public List<Meal> getMealsByDietType(String dietType) {
        return mealRepository.findByDietType(dietType);
    }
    
    /**
     * Get meals by cuisine type
     */
    public List<Meal> getMealsByCuisineType(String cuisineType) {
        return mealRepository.findByCuisineType(cuisineType);
    }
    
    /**
     * Get meals with calorie limit
     */
    public List<Meal> getMealsWithCalorieLimit(Integer maxCalories) {
        return mealRepository.findByCaloriesLessThanEqual(maxCalories);
    }
    
    /**
     * Search meals by name
     */
    public List<Meal> searchMealsByName(String name) {
        return mealRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Save a new meal
     */
    public Meal saveMeal(Meal meal) {
        return mealRepository.save(meal);
    }
    
    /**
     * Update an existing meal
     */
    public Meal updateMeal(Long id, Meal mealDetails) {
        Optional<Meal> optionalMeal = mealRepository.findById(id);
        if (optionalMeal.isPresent()) {
            Meal meal = optionalMeal.get();
            meal.setName(mealDetails.getName());
            meal.setDescription(mealDetails.getDescription());
            meal.setMealType(mealDetails.getMealType());
            meal.setCalories(mealDetails.getCalories());
            meal.setProtein(mealDetails.getProtein());
            meal.setCarbs(mealDetails.getCarbs());
            meal.setFat(mealDetails.getFat());
            meal.setFiber(mealDetails.getFiber());
            meal.setSugar(mealDetails.getSugar());
            meal.setSodium(mealDetails.getSodium());
            meal.setPrepTime(mealDetails.getPrepTime());
            meal.setCookTime(mealDetails.getCookTime());
            meal.setServings(mealDetails.getServings());
            meal.setDifficultyLevel(mealDetails.getDifficultyLevel());
            meal.setCuisineType(mealDetails.getCuisineType());
            meal.setDietType(mealDetails.getDietType());
            meal.setIngredients(mealDetails.getIngredients());
            meal.setInstructions(mealDetails.getInstructions());
            meal.setImageUrl(mealDetails.getImageUrl());
            return mealRepository.save(meal);
        }
        return null;
    }
    
    /**
     * Delete a meal
     */
    public boolean deleteMeal(Long id) {
        if (mealRepository.existsById(id)) {
            mealRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Get random meal by type
     */
    public Optional<Meal> getRandomMealByType(String mealType) {
        return mealRepository.findRandomByMealType(mealType);
    }
    
    /**
     * Get suitable meals based on criteria
     */
    public List<Meal> getSuitableMeals(String mealType, Integer maxCalories, String dietType) {
        return mealRepository.findSuitableMeals(mealType, maxCalories, dietType);
    }
} 