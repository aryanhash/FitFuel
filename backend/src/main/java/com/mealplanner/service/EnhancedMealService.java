package com.mealplanner.service;

import com.mealplanner.dto.MealDto;
import com.mealplanner.entity.Recipe;
import com.mealplanner.entity.User;
import com.mealplanner.entity.UserMealPlan;
import com.mealplanner.entity.UserPreferences;
import com.mealplanner.repository.RecipeRepository;
import com.mealplanner.repository.UserMealPlanRepository;
import com.mealplanner.repository.UserPreferencesRepository;
import com.mealplanner.repository.UserRepository;
import com.mealplanner.util.MealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnhancedMealService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMealPlanRepository userMealPlanRepository;
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    @Autowired
    private ProxyService proxyService;
    
    @Autowired
    private MealMapper mealMapper;
    
    @Autowired
    private NutritionixService nutritionixService;
    
    /**
     * Get meals for a specific user and date range with preferences
     */
    public List<MealDto> getMealsForUser(User user, LocalDate date, String mealType) {
        // First, check if user already has meal plans for this date and meal type
        Optional<UserMealPlan> existingPlan = userMealPlanRepository.findByUserAndDateAndMealType(user, date, mealType);
        
        if (existingPlan.isPresent()) {
            // Return existing meal plan
            return List.of(mealMapper.toMealDto(existingPlan.get().getRecipe()));
        }
        
        // If no existing plans, get user preferences and create new meals
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
            .orElse(null);
        
        // First, try to get meals from local database
        List<Recipe> localRecipes = getLocalRecipes(mealType, preferences);
        
        // Use only local database recipes - no external API calls
        // if (localRecipes.isEmpty()) {
        //     // If no local recipes, fetch from Nutritionix API
        //     localRecipes = fetchRecipesFromNutritionix(mealType, preferences);
        // }
        
        // Filter and sort recipes based on user preferences
        List<Recipe> filteredRecipes = filterRecipesByPreferences(localRecipes, preferences);
        
        // Create new user meal plans for this date
        for (Recipe recipe : filteredRecipes) {
            UserMealPlan newPlan = new UserMealPlan();
            newPlan.setUser(user);
            newPlan.setRecipe(recipe);
            newPlan.setDate(date);
            newPlan.setMealType(mealType);
            userMealPlanRepository.save(newPlan);
        }
        
        // Convert to DTOs
        return filteredRecipes.stream()
            .map(mealMapper::toMealDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Get a random meal for user with preferences
     */
    public MealDto getRandomMealForUser(User user, String mealType) {
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
            .orElse(null);
        
        List<Recipe> recipes = getLocalRecipes(mealType, preferences);
        
        if (recipes.isEmpty()) {
            recipes = fetchRecipesFromNutritionix(mealType, preferences);
        }
        
        if (!recipes.isEmpty()) {
            Random random = new Random();
            Recipe randomRecipe = recipes.get(random.nextInt(recipes.size()));
            return mealMapper.toMealDto(randomRecipe);
        }
        
        return null;
    }
    
    /**
     * Search meals with advanced filtering
     */
    public List<MealDto> searchMeals(User user, String query, Map<String, Object> filters) {
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
            .orElse(null);
        
        // Search local database first
        List<Recipe> localResults = searchLocalRecipes(query, filters, preferences);
        
        // If insufficient results, search external APIs
        if (localResults.size() < 5) {
            List<Recipe> externalResults = searchExternalRecipes(query, filters, preferences);
            localResults.addAll(externalResults);
        }
        
        // Remove duplicates and return
        Set<Long> seenIds = new HashSet<>();
        return localResults.stream()
            .filter(recipe -> seenIds.add(recipe.getId()))
            .map(mealMapper::toMealDto)
            .limit(20) // Limit results
            .collect(Collectors.toList());
    }
    
    /**
     * Cache recipe from external API to local database
     */
    public Recipe cacheRecipeFromExternal(MealDto externalMeal, String source, String externalId) {
        // Check if recipe already exists
        Optional<Recipe> existingRecipe = recipeRepository.findByExternalIdAndSource(externalId, source);
        
        if (existingRecipe.isPresent()) {
            // Update existing recipe
            Recipe recipe = existingRecipe.get();
            updateRecipeFromExternal(recipe, externalMeal);
            return recipeRepository.save(recipe);
        } else {
            // Create new recipe
            Recipe recipe = createRecipeFromExternal(externalMeal, source, externalId);
            return recipeRepository.save(recipe);
        }
    }
    
    /**
     * Get local recipes from database
     */
    private List<Recipe> getLocalRecipes(String mealType, UserPreferences preferences) {
        if (preferences == null) {
            return recipeRepository.findByCategoryAndDietType(mealType, "MIXED");
        }
        
        List<Recipe> recipes = recipeRepository.findByCategoryAndDietType(
            mealType, preferences.getDietType());
        
        // Filter by other preferences if needed
        if (preferences.getPreferredCuisines() != null && !preferences.getPreferredCuisines().isEmpty()) {
            recipes = recipes.stream()
                .filter(recipe -> preferences.getPreferredCuisines().contains(recipe.getCuisineType()))
                .collect(Collectors.toList());
        }
        
        return recipes;
    }
    
    /**
     * Fetch recipes from external APIs
     */
    private List<Recipe> fetchRecipesFromExternalAPIs(String mealType, UserPreferences preferences) {
        List<Recipe> recipes = new ArrayList<>();
        
        // Try Edamam first
        try {
            List<MealDto> edamamRecipes = fetchFromEdamam(mealType, preferences);
            for (MealDto mealDto : edamamRecipes) {
                Recipe recipe = cacheRecipeFromExternal(mealDto, "edamam", mealDto.getExternalId());
                recipes.add(recipe);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch from Edamam: " + e.getMessage());
        }
        
        // Try Spoonacular if needed
        if (recipes.size() < 5) {
            try {
                List<MealDto> spoonacularRecipes = fetchFromSpoonacular(mealType, preferences);
                for (MealDto mealDto : spoonacularRecipes) {
                    Recipe recipe = cacheRecipeFromExternal(mealDto, "spoonacular", mealDto.getExternalId());
                    recipes.add(recipe);
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch from Spoonacular: " + e.getMessage());
            }
        }
        
        return recipes;
    }
    
    /**
     * Search local recipes
     */
    private List<Recipe> searchLocalRecipes(String query, Map<String, Object> filters, UserPreferences preferences) {
        // Implementation depends on your search requirements
        // This is a simplified version
        return recipeRepository.findByNameContainingIgnoreCase(query);
    }
    
    /**
     * Search external recipes
     */
    private List<Recipe> searchExternalRecipes(String query, Map<String, Object> filters, UserPreferences preferences) {
        List<Recipe> recipes = new ArrayList<>();
        
        try {
            List<MealDto> externalRecipes = searchFromEdamam(query, filters);
            for (MealDto mealDto : externalRecipes) {
                Recipe recipe = cacheRecipeFromExternal(mealDto, "edamam", mealDto.getExternalId());
                recipes.add(recipe);
            }
        } catch (Exception e) {
            System.err.println("External search failed: " + e.getMessage());
        }
        
        return recipes;
    }
    
    /**
     * Filter recipes based on user preferences
     */
    private List<Recipe> filterRecipesByPreferences(List<Recipe> recipes, UserPreferences preferences) {
        if (preferences == null) {
            return recipes;
        }
        
        return recipes.stream()
            .filter(recipe -> !hasAllergies(recipe, preferences))
            .filter(recipe -> !hasDislikes(recipe, preferences))
            .filter(recipe -> meetsCalorieTarget(recipe, preferences))
            .filter(recipe -> matchesCookingSkill(recipe, preferences))
            .sorted((r1, r2) -> {
                // Sort by relevance to user preferences
                return calculateRelevanceScore(r2, preferences) - calculateRelevanceScore(r1, preferences);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Check if recipe contains allergens
     */
    private boolean hasAllergies(Recipe recipe, UserPreferences preferences) {
        if (preferences.getAllergies() == null || preferences.getAllergies().isEmpty()) {
            return false;
        }
        
        String ingredientsText = String.join(" ", recipe.getIngredients());
        return preferences.getAllergies().stream()
            .anyMatch(allergen -> ingredientsText.toLowerCase().contains(allergen.toLowerCase()));
    }
    
    /**
     * Check if recipe contains disliked foods
     */
    private boolean hasDislikes(Recipe recipe, UserPreferences preferences) {
        if (preferences.getDislikes() == null || preferences.getDislikes().isEmpty()) {
            return false;
        }
        
        String recipeText = recipe.getName() + " " + String.join(" ", recipe.getIngredients());
        return preferences.getDislikes().stream()
            .anyMatch(dislike -> recipeText.toLowerCase().contains(dislike.toLowerCase()));
    }
    
    /**
     * Check if recipe meets calorie target
     */
    private boolean meetsCalorieTarget(Recipe recipe, UserPreferences preferences) {
        if (preferences.getDailyCalorieTarget() == null || recipe.getCalories() == null) {
            return true;
        }
        
        int targetPerMeal = preferences.getDailyCalorieTarget() / 4; // Assuming 4 meals per day
        return recipe.getCalories() <= targetPerMeal * 1.2; // Allow 20% variance
    }
    
    /**
     * Check if recipe matches cooking skill level
     */
    private boolean matchesCookingSkill(Recipe recipe, UserPreferences preferences) {
        if (preferences.getCookingSkillLevel() == null) {
            return true;
        }
        
        String skillLevel = preferences.getCookingSkillLevel();
        String difficulty = recipe.getDifficultyLevel();
        
        return switch (skillLevel) {
            case "BEGINNER" -> "EASY".equals(difficulty);
            case "INTERMEDIATE" -> !"HARD".equals(difficulty);
            case "ADVANCED" -> true; // Advanced users can cook anything
            default -> true;
        };
    }
    
    /**
     * Calculate relevance score for recipe based on user preferences
     */
    private int calculateRelevanceScore(Recipe recipe, UserPreferences preferences) {
        int score = 0;
        
        // Base score
        score += 10;
        
        // Cuisine preference match
        if (preferences.getPreferredCuisines() != null && 
            preferences.getPreferredCuisines().contains(recipe.getCuisineType())) {
            score += 20;
        }
        
        // Diet type match
        if (preferences.getDietType().equals(recipe.getDietType())) {
            score += 15;
        }
        
        // Cooking time preference (prefer shorter times)
        if (recipe.getTotalTime() != null && recipe.getTotalTime() < 30) {
            score += 10;
        }
        
        return score;
    }
    
    /**
     * Fetch recipes from Edamam API
     */
    private List<MealDto> fetchFromEdamam(String mealType, UserPreferences preferences) {
        // This would use the ProxyService to make the actual API call
        // Implementation depends on the Edamam API response structure
        return new ArrayList<>();
    }
    
    /**
     * Fetch recipes from Spoonacular API
     */
    private List<MealDto> fetchFromSpoonacular(String mealType, UserPreferences preferences) {
        // Implementation for Spoonacular API
        return new ArrayList<>();
    }
    
    /**
     * Search recipes from Edamam
     */
    private List<MealDto> searchFromEdamam(String query, Map<String, Object> filters) {
        // Implementation for Edamam search
        return new ArrayList<>();
    }
    
    /**
     * Create recipe from external meal DTO
     */
    private Recipe createRecipeFromExternal(MealDto externalMeal, String source, String externalId) {
        Recipe recipe = new Recipe();
        recipe.setName(externalMeal.getName());
        recipe.setDescription(externalMeal.getDescription());
        recipe.setCategory(externalMeal.getMealType());
        recipe.setCuisineType(externalMeal.getCuisineType());
        recipe.setDietType(externalMeal.getDietType());
        recipe.setPrepTime(externalMeal.getPrepTimeMinutes());
        recipe.setCookTime(externalMeal.getCookTimeMinutes());
        recipe.setServings(externalMeal.getServings());
        recipe.setDifficultyLevel(externalMeal.getDifficultyLevel());
        recipe.setIngredients(externalMeal.getIngredients());
        recipe.setInstructions(externalMeal.getInstructions());
        recipe.setCalories(externalMeal.getCalories());
        recipe.setProtein(externalMeal.getProteinGrams());
        recipe.setCarbs(externalMeal.getCarbsGrams());
        recipe.setFat(externalMeal.getFatGrams());
        recipe.setFiber(externalMeal.getFiberGrams());
        recipe.setSugar(externalMeal.getSugarGrams());
        recipe.setSodium(externalMeal.getSodiumMilliGrams());
        recipe.setImageUrl(externalMeal.getImageUrl());
        recipe.setSource(source);
        recipe.setExternalId(externalId);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());
        
        return recipe;
    }
    
    /**
     * Update existing recipe from external meal DTO
     */
    private void updateRecipeFromExternal(Recipe recipe, MealDto externalMeal) {
        // Update only certain fields to preserve local modifications
        recipe.setCalories(externalMeal.getCalories());
        recipe.setProtein(externalMeal.getProteinGrams());
        recipe.setCarbs(externalMeal.getCarbsGrams());
        recipe.setFat(externalMeal.getFatGrams());
        recipe.setFiber(externalMeal.getFiberGrams());
        recipe.setSugar(externalMeal.getSugarGrams());
        recipe.setSodium(externalMeal.getSodiumMilliGrams());
        recipe.setImageUrl(externalMeal.getImageUrl());
        recipe.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * Fetch recipes from Nutritionix API
     */
    private List<Recipe> fetchRecipesFromNutritionix(String mealType, UserPreferences preferences) {
        try {
            // Build search query based on meal type
            String query = buildNutritionixQuery(mealType, preferences);
            Integer maxCalories = preferences != null ? preferences.getDailyCalorieTarget() / 4 : 500;
            String diet = preferences != null ? preferences.getDietType() : null;
            
            // Search for recipes
            List<MealDto> mealDtos = nutritionixService.searchRecipes(query, mealType, maxCalories, diet);
            
            // Convert and cache recipes
            List<Recipe> recipes = new ArrayList<>();
            for (MealDto mealDto : mealDtos) {
                Recipe recipe = nutritionixService.cacheMealFromNutritionix(mealDto);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
            
            return recipes;
            
        } catch (Exception e) {
            System.err.println("Error fetching recipes from Nutritionix: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Build search query for Nutritionix based on meal type and preferences
     */
    private String buildNutritionixQuery(String mealType, UserPreferences preferences) {
        StringBuilder query = new StringBuilder();
        
        // Add meal type specific keywords
        switch (mealType.toUpperCase()) {
            case "BREAKFAST":
                query.append("breakfast cereal eggs toast oatmeal");
                break;
            case "LUNCH":
                query.append("lunch sandwich salad soup");
                break;
            case "DINNER":
                query.append("dinner chicken beef fish pasta rice");
                break;
            case "SNACK":
                query.append("snack nuts fruits yogurt");
                break;
            default:
                query.append("food meal");
        }
        
        // Add dietary preferences
        if (preferences != null && preferences.getDietType() != null) {
            String diet = preferences.getDietType().toLowerCase();
            switch (diet) {
                case "vegetarian":
                    query.append(" vegetarian");
                    break;
                case "vegan":
                    query.append(" vegan");
                    break;
                case "keto":
                    query.append(" keto low-carb");
                    break;
                case "paleo":
                    query.append(" paleo");
                    break;
                case "mediterranean":
                    query.append(" mediterranean");
                    break;
            }
        }
        
        return query.toString();
    }
    
    /**
     * Populate sample meal data for testing
     */
    public void populateSampleData() {
        try {
            // Get existing recipes from database instead of creating new ones
            List<Recipe> breakfastRecipes = recipeRepository.findByCategory("Breakfast");
            List<Recipe> lunchRecipes = recipeRepository.findByCategory("Lunch");
            List<Recipe> dinnerRecipes = recipeRepository.findByCategory("Dinner");
            
            if (breakfastRecipes.isEmpty() || lunchRecipes.isEmpty() || dinnerRecipes.isEmpty()) {
                System.out.println("No existing recipes found in database. Please run the database init script first.");
                return;
            }

            // Create a sample user if none exists
            User sampleUser = userRepository.findByEmail("test@example.com")
                .orElse(null);

            if (sampleUser == null) {
                sampleUser = new User();
                sampleUser.setName("Test User");
                sampleUser.setEmail("test@example.com");
                sampleUser.setPasswordHash("password123");
                sampleUser = userRepository.save(sampleUser);
            }

            // Create meal plans for 365 days starting from today
            LocalDate today = LocalDate.now();
            Random random = new Random();
            
            for (int i = 0; i < 365; i++) {
                LocalDate date = today.plusDays(i);
                
                // Select random recipes for each meal type
                Recipe breakfastRecipe = breakfastRecipes.get(random.nextInt(breakfastRecipes.size()));
                Recipe lunchRecipe = lunchRecipes.get(random.nextInt(lunchRecipes.size()));
                Recipe dinnerRecipe = dinnerRecipes.get(random.nextInt(dinnerRecipes.size()));
                
                // Check if meal plans already exist for this date
                Optional<UserMealPlan> existingBreakfast = userMealPlanRepository.findByUserAndDateAndMealType(sampleUser, date, "Breakfast");
                if (existingBreakfast.isEmpty()) {
                    UserMealPlan breakfastPlan = new UserMealPlan();
                    breakfastPlan.setUser(sampleUser);
                    breakfastPlan.setRecipe(breakfastRecipe);
                    breakfastPlan.setDate(date);
                    breakfastPlan.setMealType("Breakfast");
                    breakfastPlan.setYear(date.getYear());
                    userMealPlanRepository.save(breakfastPlan);
                }
                
                Optional<UserMealPlan> existingLunch = userMealPlanRepository.findByUserAndDateAndMealType(sampleUser, date, "Lunch");
                if (existingLunch.isEmpty()) {
                    UserMealPlan lunchPlan = new UserMealPlan();
                    lunchPlan.setUser(sampleUser);
                    lunchPlan.setRecipe(lunchRecipe);
                    lunchPlan.setDate(date);
                    lunchPlan.setMealType("Lunch");
                    lunchPlan.setYear(date.getYear());
                    userMealPlanRepository.save(lunchPlan);
                }
                
                Optional<UserMealPlan> existingDinner = userMealPlanRepository.findByUserAndDateAndMealType(sampleUser, date, "Dinner");
                if (existingDinner.isEmpty()) {
                    UserMealPlan dinnerPlan = new UserMealPlan();
                    dinnerPlan.setUser(sampleUser);
                    dinnerPlan.setRecipe(dinnerRecipe);
                    dinnerPlan.setDate(date);
                    dinnerPlan.setMealType("Dinner");
                    dinnerPlan.setYear(date.getYear());
                    userMealPlanRepository.save(dinnerPlan);
                }
            }
            
            System.out.println("365 days of meal plans populated successfully!");
            
        } catch (Exception e) {
            System.err.println("Error populating sample data: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
