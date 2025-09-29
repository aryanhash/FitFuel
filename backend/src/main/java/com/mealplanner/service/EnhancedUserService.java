package com.mealplanner.service;

import com.mealplanner.dto.UserPreferencesDto;
import com.mealplanner.dto.MealDto;
import com.mealplanner.entity.User;
import com.mealplanner.entity.UserPreferences;
import com.mealplanner.entity.UserMealPlan;
import com.mealplanner.entity.UserFavorite;
import com.mealplanner.entity.UserMealHistory;
import com.mealplanner.entity.Recipe;
import com.mealplanner.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnhancedUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    @Autowired
    private UserMealPlanRepository userMealPlanRepository;
    
    @Autowired
    private UserFavoriteRepository userFavoriteRepository;
    
    @Autowired
    private UserMealHistoryRepository userMealHistoryRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Create new user
     */
    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        
        // Create default preferences
        UserPreferences defaultPreferences = new UserPreferences();
        defaultPreferences.setUser(savedUser);
        defaultPreferences.setDietType("MIXED");
        defaultPreferences.setDailyCalorieTarget(2000);
        defaultPreferences.setCookingSkillLevel("INTERMEDIATE");
        
        userPreferencesRepository.save(defaultPreferences);
        
        return savedUser;
    }
    
    /**
     * Update user preferences
     */
    public UserPreferences updateUserPreferences(Long userId, UserPreferencesDto preferencesDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
            .orElse(new UserPreferences());
        
        preferences.setUser(user);
        preferences.setDietType(preferencesDto.getDietaryPreference());
        preferences.setDailyCalorieTarget(preferencesDto.getCalorieGoal());
        preferences.setCookingSkillLevel(preferencesDto.getCookingSkillLevel());
        preferences.setUpdatedAt(LocalDateTime.now());
        
        return userPreferencesRepository.save(preferences);
    }
    
    /**
     * Get user preferences
     */
    public Optional<UserPreferences> getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return userPreferencesRepository.findByUser(user);
    }
    
    /**
     * Add meal to user's meal plan
     */
    public UserMealPlan addMealToPlan(Long userId, LocalDate date, String mealType, Long recipeId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        // Check if meal plan already exists for this date and meal type
        Optional<UserMealPlan> existingPlan = userMealPlanRepository
            .findByUserAndDateAndMealType(user, date, mealType);
        
        UserMealPlan mealPlan;
        if (existingPlan.isPresent()) {
            mealPlan = existingPlan.get();
            mealPlan.setRecipe(recipe);
            mealPlan.setUpdatedAt(LocalDateTime.now());
        } else {
            mealPlan = new UserMealPlan();
            mealPlan.setUser(user);
            mealPlan.setDate(date);
            mealPlan.setMealType(mealType);
            mealPlan.setRecipe(recipe);
            mealPlan.setCreatedAt(LocalDateTime.now());
            mealPlan.setUpdatedAt(LocalDateTime.now());
        }
        
        return userMealPlanRepository.save(mealPlan);
    }
    
    /**
     * Get user's meal plan for a date
     */
    public List<UserMealPlan> getUserMealPlan(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userMealPlanRepository.findByUserAndDate(user, date);
    }
    
    /**
     * Get user's meal plan for date range
     */
    public List<UserMealPlan> getUserMealPlan(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userMealPlanRepository.findByUserAndDateBetween(user, startDate, endDate);
    }
    
    /**
     * Add recipe to favorites
     */
    public UserFavorite addToFavorites(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        // Check if already in favorites
        Optional<UserFavorite> existingFavorite = userFavoriteRepository
            .findByUserAndRecipe(user, recipe);
        
        if (existingFavorite.isPresent()) {
            return existingFavorite.get();
        }
        
        UserFavorite favorite = new UserFavorite();
        favorite.setUser(user);
        favorite.setRecipe(recipe);
        favorite.setCreatedAt(LocalDateTime.now());
        
        return userFavoriteRepository.save(favorite);
    }
    
    /**
     * Remove recipe from favorites
     */
    public void removeFromFavorites(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        userFavoriteRepository.deleteByUserAndRecipe(user, recipe);
    }
    
    /**
     * Get user's favorite recipes
     */
    public List<Recipe> getUserFavorites(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userFavoriteRepository.findByUser(user)
            .stream()
            .map(UserFavorite::getRecipe)
            .collect(Collectors.toList());
    }
    
    /**
     * Log meal consumption for AI recommendations
     */
    public UserMealHistory logMealConsumption(Long userId, Long recipeId, String mealType, 
                                             Integer rating, String notes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        UserMealHistory history = new UserMealHistory();
        history.setUser(user);
        history.setRecipe(recipe);
        history.setDateConsumed(LocalDate.now());
        history.setMealType(mealType);
        history.setRating(rating);
        history.setNotes(notes);
        history.setCreatedAt(LocalDateTime.now());
        
        return userMealHistoryRepository.save(history);
    }
    
    /**
     * Get user's meal history
     */
    public List<UserMealHistory> getUserMealHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userMealHistoryRepository.findByUserAndDateConsumedBetween(user, startDate, endDate);
    }
    
    /**
     * Rate a meal
     */
    public UserMealPlan rateMeal(Long userId, LocalDate date, String mealType, Integer rating, String notes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserMealPlan mealPlan = userMealPlanRepository
            .findByUserAndDateAndMealType(user, date, mealType)
            .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        
        mealPlan.setRating(rating);
        mealPlan.setNotes(notes);
        mealPlan.setUpdatedAt(LocalDateTime.now());
        
        return userMealPlanRepository.save(mealPlan);
    }
    
    /**
     * Get user's dietary insights (for AI recommendations)
     */
    public Map<String, Object> getUserDietaryInsights(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get preferences
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
            .orElse(new UserPreferences());
        
        // Get recent meal history
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<UserMealHistory> recentHistory = userMealHistoryRepository
            .findByUserAndDateConsumedAfter(user, thirtyDaysAgo);
        
        // Get favorites
        List<Recipe> favorites = getUserFavorites(userId);
        
        // Calculate insights
        Map<String, Object> insights = new HashMap<>();
        insights.put("dietType", preferences.getDietType());
        insights.put("calorieTarget", preferences.getDailyCalorieTarget());
        insights.put("preferredCuisines", preferences.getPreferredCuisines());
        insights.put("recentMealCount", recentHistory.size());
        insights.put("averageRating", calculateAverageRating(recentHistory));
        insights.put("favoriteCategories", getFavoriteCategories(favorites));
        insights.put("allergies", preferences.getAllergies());
        insights.put("dislikes", preferences.getDislikes());
        
        return insights;
    }
    
    /**
     * Calculate average rating from meal history
     */
    private Double calculateAverageRating(List<UserMealHistory> history) {
        if (history.isEmpty()) {
            return null;
        }
        
        return history.stream()
            .filter(h -> h.getRating() != null)
            .mapToInt(UserMealHistory::getRating)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Get favorite categories from favorites
     */
    private List<String> getFavoriteCategories(List<Recipe> favorites) {
        return favorites.stream()
            .map(Recipe::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Get user's meal plan statistics
     */
    public Map<String, Object> getUserMealPlanStats(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        
        List<UserMealPlan> monthlyPlans = userMealPlanRepository
            .findByUserAndDateBetween(user, startOfMonth, endOfMonth);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMealsThisMonth", monthlyPlans.size());
        stats.put("favoriteMeals", getUserFavorites(userId).size());
        stats.put("averageRating", calculateAverageRating(
            monthlyPlans.stream()
                .filter(p -> p.getRating() != null)
                .map(p -> {
                    UserMealHistory history = new UserMealHistory();
                    history.setRating(p.getRating());
                    return history;
                })
                .collect(Collectors.toList())
        ));
        
        return stats;
    }
}
