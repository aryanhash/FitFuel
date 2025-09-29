package com.mealplanner.controller;

import com.mealplanner.dto.MealDto;
import com.mealplanner.dto.MealPlanResponseDto;
import com.mealplanner.dto.UserPreferencesDto;
import com.mealplanner.entity.User;
import com.mealplanner.service.*;
import com.mealplanner.util.MealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/meal-plan")
@CrossOrigin(origins = "http://localhost:3000")
public class EnhancedMealPlanController {
    
    @Autowired
    private EnhancedMealService mealService;
    
    @Autowired
    private EnhancedUserService userService;
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private MealMapper mealMapper;
    
    /**
     * Get meal plan for a specific day (simplified endpoint for frontend)
     */
    @GetMapping("/day")
    public ResponseEntity<MealPlanResponseDto> getMealPlanForDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String mealType) {
        
        try {
            // For now, get the first user or create one if none exists
            User user = userService.getUserById(1L).orElse(null);
            
            if (user == null) {
                // Try to get any existing user first
                List<User> allUsers = userService.getAllUsers();
                if (!allUsers.isEmpty()) {
                    user = allUsers.get(0);
                } else {
                    // Create a default user for testing
                    user = new User();
                    user.setName("Test User");
                    user.setEmail("test@example.com");
                    user.setPasswordHash("test123"); // Set a default password hash
                    try {
                        user = userService.createUser(user);
                    } catch (Exception e) {
                        // If user creation fails (e.g., duplicate email), try to get existing user
                        user = userService.getUserByEmail("test@example.com").orElse(null);
                        if (user == null) {
                            // Create with unique email
                            user = new User();
                            user.setName("Test User");
                            user.setEmail("test" + System.currentTimeMillis() + "@example.com");
                            user.setPasswordHash("test123");
                            user = userService.createUser(user);
                        }
                    }
                }
            }
            
            if (mealType != null) {
                // Get specific meal type
                List<MealDto> meals = mealService.getMealsForUser(user, date, mealType);
                MealPlanResponseDto response = new MealPlanResponseDto(date, meals);
                return ResponseEntity.ok(response);
            } else {
                // Get all meals for the day
                List<MealDto> allMeals = new ArrayList<>();
                String[] mealTypes = {"BREAKFAST", "LUNCH", "DINNER", "SNACK"};
                
                for (String type : mealTypes) {
                    List<MealDto> meals = mealService.getMealsForUser(user, date, type);
                    allMeals.addAll(meals);
                }
                
                MealPlanResponseDto response = new MealPlanResponseDto(date, allMeals);
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            MealPlanResponseDto errorResponse = new MealPlanResponseDto(date, "Error: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Get personalized meal plan for user
     */
    @GetMapping("/personalized/{userId}")
    public ResponseEntity<MealPlanResponseDto> getPersonalizedMealPlan(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String mealType) {
        
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (mealType != null) {
                // Get specific meal type
                List<MealDto> meals = mealService.getMealsForUser(user, date, mealType);
                MealPlanResponseDto response = new MealPlanResponseDto(date, meals);
                return ResponseEntity.ok(response);
            } else {
                // Get all meals for the day
                List<MealDto> allMeals = new ArrayList<>();
                String[] mealTypes = {"BREAKFAST", "LUNCH", "DINNER", "SNACK"};
                
                for (String type : mealTypes) {
                    List<MealDto> meals = mealService.getMealsForUser(user, date, type);
                    allMeals.addAll(meals);
                }
                
                MealPlanResponseDto response = new MealPlanResponseDto(date, allMeals);
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            MealPlanResponseDto errorResponse = new MealPlanResponseDto(date, "Error: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Generate AI-powered meal plan
     */
    @PostMapping("/ai-generate/{userId}")
    public ResponseEntity<MealPlanResponseDto> generateAIMealPlan(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String mealType) {
        
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<MealDto> aiGeneratedMeals = aiService.generatePersonalizedMealPlan(user, date, mealType);
            MealPlanResponseDto response = new MealPlanResponseDto(date, aiGeneratedMeals);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            MealPlanResponseDto errorResponse = new MealPlanResponseDto(date, "AI generation failed: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Search meals with advanced filtering
     */
    @GetMapping("/search/{userId}")
    public ResponseEntity<?> searchMeals(
            @PathVariable Long userId,
            @RequestParam String query,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false) String dietType,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(required = false) String cuisine,
            @RequestParam(defaultValue = "20") int limit) {
        
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> filters = new HashMap<>();
            if (mealType != null) filters.put("mealType", mealType);
            if (dietType != null) filters.put("dietType", dietType);
            if (maxCalories != null) filters.put("maxCalories", maxCalories);
            if (cuisine != null) filters.put("cuisine", cuisine);
            
            List<MealDto> searchResults = mealService.searchMeals(user, query, filters);
            
            Map<String, Object> response = new HashMap<>();
            response.put("query", query);
            response.put("filters", filters);
            response.put("results", searchResults);
            response.put("totalResults", searchResults.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get personalized recommendations
     */
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<?> getPersonalizedRecommendations(
            @PathVariable Long userId,
            @RequestParam(required = false) String mealType,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<MealDto> recommendations = aiService.getPersonalizedRecommendations(user, mealType, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("mealType", mealType);
            response.put("recommendations", recommendations);
            response.put("totalRecommendations", recommendations.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Recommendations failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Add meal to user's meal plan
     */
    @PostMapping("/add-meal/{userId}")
    public ResponseEntity<?> addMealToPlan(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String mealType,
            @RequestParam Long recipeId) {
        
        try {
            userService.addMealToPlan(userId, date, mealType, recipeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Meal added to plan successfully");
            response.put("userId", userId);
            response.put("date", date);
            response.put("mealType", mealType);
            response.put("recipeId", recipeId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add meal: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's meal plan for a date
     */
    @GetMapping("/user-plan/{userId}")
    public ResponseEntity<?> getUserMealPlan(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            List<MealDto> mealPlans = userService.getUserMealPlan(userId, date)
                .stream()
                .map(plan -> mealMapper.toMealDto(plan.getRecipe()))
                .collect(java.util.stream.Collectors.toList());
            
            MealPlanResponseDto response = new MealPlanResponseDto(date, mealPlans);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            MealPlanResponseDto errorResponse = new MealPlanResponseDto(date, "Error: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Add recipe to favorites
     */
    @PostMapping("/favorites/{userId}")
    public ResponseEntity<?> addToFavorites(
            @PathVariable Long userId,
            @RequestParam Long recipeId) {
        
        try {
            userService.addToFavorites(userId, recipeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Recipe added to favorites");
            response.put("userId", userId);
            response.put("recipeId", recipeId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add to favorites: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's favorite recipes
     */
    @GetMapping("/favorites/{userId}")
    public ResponseEntity<?> getUserFavorites(@PathVariable Long userId) {
        try {
            List<MealDto> favorites = userService.getUserFavorites(userId)
                .stream()
                .map(mealMapper::toMealDto)
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("favorites", favorites);
            response.put("totalFavorites", favorites.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get favorites: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Rate a meal
     */
    @PostMapping("/rate/{userId}")
    public ResponseEntity<?> rateMeal(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String mealType,
            @RequestParam Integer rating,
            @RequestParam(required = false) String notes) {
        
        try {
            userService.rateMeal(userId, date, mealType, rating, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Meal rated successfully");
            response.put("userId", userId);
            response.put("date", date);
            response.put("mealType", mealType);
            response.put("rating", rating);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to rate meal: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's dietary insights
     */
    @GetMapping("/insights/{userId}")
    public ResponseEntity<?> getUserDietaryInsights(@PathVariable Long userId) {
        try {
            Map<String, Object> insights = userService.getUserDietaryInsights(userId);
            return ResponseEntity.ok(insights);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get insights: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's meal plan statistics
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getUserMealPlanStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = userService.getUserMealPlanStats(userId);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Analyze user's eating patterns
     */
    @GetMapping("/analysis/{userId}")
    public ResponseEntity<?> analyzeEatingPatterns(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> analysis = aiService.analyzeEatingPatterns(user);
            return ResponseEntity.ok(analysis);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Analysis failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Populate sample meal data for testing
     */
    @PostMapping("/populate-sample-data")
    public ResponseEntity<Map<String, Object>> populateSampleData() {
        try {
            mealService.populateSampleData();
            return ResponseEntity.ok(Map.of("message", "Sample data populated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
