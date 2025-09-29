package com.mealplanner.service;

import com.mealplanner.dto.MealDto;
import com.mealplanner.entity.Recipe;
import com.mealplanner.entity.User;
import com.mealplanner.entity.UserMealPlan;
import com.mealplanner.entity.UserPreferences;
import com.mealplanner.repository.RecipeRepository;
import com.mealplanner.repository.UserMealPlanRepository;
import com.mealplanner.repository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class MealService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserMealPlanRepository userMealPlanRepository;
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    // Get meals for a specific user, date, and meal type
    public List<MealDto> getMealsForUser(User user, LocalDate date, String mealType) {
        List<MealDto> meals = new ArrayList<>();
        
        // First, check if user has meal plans for this date and meal type
        Optional<UserMealPlan> existingMealPlan = userMealPlanRepository.findByUserAndDateAndMealType(user, date, mealType);
        
        if (existingMealPlan.isPresent()) {
            // Convert UserMealPlan to MealDto
            MealDto mealDto = convertToMealDto(existingMealPlan.get().getRecipe());
            meals.add(mealDto);
        } else {
            // If no meal plan exists, get recipes from database based on preferences
            List<Recipe> recipes = getRecipesForUser(user, mealType);
            
            for (Recipe recipe : recipes) {
                meals.add(convertToMealDto(recipe));
            }
        }
        
        return meals;
    }
    
    // Get recipes based on user preferences
    private List<Recipe> getRecipesForUser(User user, String mealType) {
        // Get user preferences
        Optional<UserPreferences> preferencesOpt = userPreferencesRepository.findByUser(user);
        
        String dietType = "VEGETARIAN"; // default
        if (preferencesOpt.isPresent()) {
            dietType = preferencesOpt.get().getDietType();
        }
        
        // Find recipes by category (mealType) and diet type
        List<Recipe> recipes = recipeRepository.findByCategoryAndDietType(mealType, dietType);
        
        // If no recipes found with diet type, get all recipes for the meal type
        if (recipes.isEmpty()) {
            recipes = recipeRepository.findByCategory(mealType);
        }
        
        // If still no recipes, get any recipes
        if (recipes.isEmpty()) {
            recipes = recipeRepository.findAll();
        }
        
        // Return up to 3 random recipes
        if (recipes.size() > 3) {
            Random random = new Random();
            List<Recipe> randomRecipes = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                randomRecipes.add(recipes.get(random.nextInt(recipes.size())));
            }
            return randomRecipes;
        }
        
        return recipes;
    }
    
    // Convert Recipe to MealDto
    private MealDto convertToMealDto(Recipe recipe) {
        MealDto mealDto = new MealDto();
        mealDto.setId(recipe.getId());
        mealDto.setName(recipe.getName());
        mealDto.setDescription(recipe.getDescription());
        mealDto.setInstructions(recipe.getInstructions());
        mealDto.setIngredients(recipe.getIngredients());
        mealDto.setPrepTimeMinutes(recipe.getPrepTimeMinutes());
        mealDto.setCookTimeMinutes(recipe.getCookTimeMinutes());
        mealDto.setTotalTimeMinutes(recipe.getTotalTimeMinutes());
        mealDto.setServings(recipe.getServings());
        mealDto.setDifficultyLevel(recipe.getDifficultyLevel());
        mealDto.setCategory(recipe.getCategory());
        mealDto.setCuisineType(recipe.getCuisineType());
        mealDto.setDietType(recipe.getDietType());
        mealDto.setType(recipe.getType());
        mealDto.setCalories(recipe.getCalories());
        mealDto.setProteinGrams(recipe.getProteinGrams());
        mealDto.setCarbsGrams(recipe.getCarbsGrams());
        mealDto.setFatGrams(recipe.getFatGrams());
        mealDto.setFiberGrams(recipe.getFiberGrams());
        mealDto.setSugarGrams(recipe.getSugarGrams());
        mealDto.setSodiumMilligrams(recipe.getSodiumMilligrams());
        mealDto.setImageUrl(recipe.getImageUrl());
        mealDto.setExternalId(recipe.getExternalId());
        mealDto.setSourceApi(recipe.getSource());
        mealDto.setExternalUrl(recipe.getExternalUrl());
        
        return mealDto;
    }
    
    // Create a meal plan for a user
    public UserMealPlan createMealPlan(User user, Recipe recipe, LocalDate date, String mealType) {
        UserMealPlan mealPlan = new UserMealPlan(user, recipe, date, mealType);
        return userMealPlanRepository.save(mealPlan);
    }
    
    // Get all recipes
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    
    // Get recipes by category
    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findByCategory(category);
    }
    
    // Get recipe by ID
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }
}
