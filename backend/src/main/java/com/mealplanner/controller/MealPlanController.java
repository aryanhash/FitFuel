package com.mealplanner.controller;

import com.mealplanner.dto.MealDto;
import com.mealplanner.dto.MealPlanResponseDto;
import com.mealplanner.entity.DailyMealPlan;
import com.mealplanner.entity.Meal;
import com.mealplanner.entity.User;
import com.mealplanner.repository.DailyMealPlanRepository;
import com.mealplanner.service.EdamamRecipeService;
import com.mealplanner.service.MealPlanService;
import com.mealplanner.service.MealService;
import com.mealplanner.service.UserService;
import com.mealplanner.util.MealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DISABLED - Using EnhancedMealPlanController instead
// @RestController
@RequestMapping("/api/meal-plan")
@CrossOrigin(origins = "*")
public class MealPlanController {
    
    @Autowired
    private MealPlanService mealPlanService;
    
    @Autowired
    private MealService mealService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DailyMealPlanRepository dailyMealPlanRepository;
    
    @Autowired
    private EdamamRecipeService edamamRecipeService;
    
    @Autowired
    private MealMapper mealMapper;
    
    /**
     * Get meal plan for a specific date
     */
    @GetMapping("/day")
    public ResponseEntity<MealPlanResponseDto> getMealPlanForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<DailyMealPlan> mealPlans = mealPlanService.getMealPlanForDate(date);
            
            MealPlanResponseDto response = mealMapper.toMealPlanResponseDto(date, mealPlans);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MealPlanResponseDto errorResponse = new MealPlanResponseDto(date, "Error: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Get meal plan for a specific date and year
     */
    @GetMapping("/day/{year}")
    public ResponseEntity<?> getMealPlanForDateAndYear(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable int year) {
        try {
            List<DailyMealPlan> mealPlans = mealPlanService.getMealPlanForDateAndYear(date, year);
            
            if (mealPlans.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No meals for this day");
                response.put("date", date);
                response.put("year", year);
                response.put("meals", List.of());
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("date", date);
            response.put("year", year);
            response.put("meals", mealPlans);
            response.put("totalMeals", mealPlans.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get meal plan: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get meal plan for a date range
     */
    @GetMapping("/range")
    public ResponseEntity<?> getMealPlanForDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<DailyMealPlan> mealPlans = mealPlanService.getMealPlanForDateRange(startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("meals", mealPlans);
            response.put("totalMeals", mealPlans.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get meal plan: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get meal plan for current month
     */
    @GetMapping("/current-month")
    public ResponseEntity<?> getMealPlanForCurrentMonth() {
        try {
            List<DailyMealPlan> mealPlans = mealPlanService.getMealPlanForCurrentMonth();
            
            Map<String, Object> response = new HashMap<>();
            response.put("month", LocalDate.now().getMonthValue());
            response.put("year", LocalDate.now().getYear());
            response.put("meals", mealPlans);
            response.put("totalMeals", mealPlans.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get current month meal plan: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Generate meal plan for current year
     */
    @PostMapping("/generate/{year}")
    public ResponseEntity<?> generateMealPlanForYear(@PathVariable int year) {
        try {
            mealPlanService.generateMealPlanForYear(year);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Meal plan generated successfully for year " + year);
            response.put("year", year);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate meal plan: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }

    }
    
    /**
     * Generate meal plan for current year (default)
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateMealPlanForCurrentYear() {
        int currentYear = Year.now().getValue();
        return generateMealPlanForYear(currentYear);
    }
    
    /**
     * Update meal for a specific date and meal type
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateMealForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String mealType,
            @RequestParam Long mealId) {
        try {
            DailyMealPlan updatedPlan = mealPlanService.updateMealForDate(date, mealType, mealId);
            
            if (updatedPlan == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to update meal plan");
                return ResponseEntity.badRequest().body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Meal plan updated successfully");
            response.put("updatedPlan", updatedPlan);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update meal plan: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get meal plan statistics
     */
    @GetMapping("/stats/{year}")
    public ResponseEntity<?> getMealPlanStats(@PathVariable int year) {
        try {
            Map<String, Object> stats = mealPlanService.getMealPlanStats(year);
            
            Map<String, Object> response = new HashMap<>();
            response.put("year", year);
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get meal plan stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get meal plan statistics for current year
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getCurrentYearMealPlanStats() {
        int currentYear = Year.now().getValue();
        return getMealPlanStats(currentYear);
    }
    
    /**
     * Check if meal plan exists for a year
     */
    @GetMapping("/exists/{year}")
    public ResponseEntity<?> checkMealPlanExists(@PathVariable int year) {
        try {
            boolean exists = mealPlanService.hasMealPlanForYear(year);
            
            Map<String, Object> response = new HashMap<>();
            response.put("year", year);
            response.put("exists", exists);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check meal plan existence: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get available meals for a meal type
     */
    @GetMapping("/meals/{mealType}")
    public ResponseEntity<?> getAvailableMeals(@PathVariable String mealType) {
        try {
            List<Meal> meals = mealService.getMealsByType(mealType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mealType", mealType);
            response.put("meals", meals);
            response.put("totalMeals", meals.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get available meals: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "MealPlanController is working");
        response.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-day")
    public ResponseEntity<?> testDay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            // Direct repository call to test
            List<DailyMealPlan> mealPlans = dailyMealPlanRepository.findByMealDateAndCreatedForYear(date, 2025);
            
            Map<String, Object> response = new HashMap<>();
            response.put("date", date);
            response.put("count", mealPlans.size());
            response.put("success", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("success", false);
            return ResponseEntity.ok(error);
        }
    }
    
    /**
     * Search recipes from external API
     */
    @GetMapping("/search-recipes")
    public ResponseEntity<?> searchRecipes(
            @RequestParam String mealType,
            @RequestParam(required = false) String dietType,
            @RequestParam(defaultValue = "10") int maxResults) {
        try {
            List<MealDto> recipes = edamamRecipeService.searchRecipes(mealType, dietType, maxResults);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mealType", mealType);
            response.put("dietType", dietType);
            response.put("recipes", recipes);
            response.put("totalRecipes", recipes.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to search recipes: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get random recipe from external API
     */
    @GetMapping("/random-recipe")
    public ResponseEntity<?> getRandomRecipe(
            @RequestParam String mealType,
            @RequestParam(required = false) String dietType) {
        try {
            MealDto recipe = edamamRecipeService.getRandomRecipe(mealType, dietType);
            
            if (recipe != null) {
                return ResponseEntity.ok(recipe);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No recipes found");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get random recipe: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 