package com.mealplanner.controller;

import com.mealplanner.dto.MealDto;
import com.mealplanner.dto.MealPlanResponseDto;
import com.mealplanner.entity.User;
import com.mealplanner.service.MealService;
import com.mealplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/meal-plan")
@CrossOrigin(origins = "http://localhost:3000")
public class MealPlanController {
    
    @Autowired
    private MealService mealService;
    
    @Autowired
    private UserService userService;
    
    // Get meal plan for a specific day
    @GetMapping("/day")
    public ResponseEntity<MealPlanResponseDto> getMealPlanForDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String mealType) {
        
        try {
            // For demo purposes, get the first user or create one if none exists
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
                    user.setPasswordHash("test123");
                    user = userService.createUser(user);
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
    
    // Get meal plans for a date range
    @GetMapping("/range")
    public ResponseEntity<List<MealPlanResponseDto>> getMealPlanForRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            User user = userService.getUserById(1L).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<MealPlanResponseDto> mealPlans = new ArrayList<>();
            LocalDate currentDate = startDate;
            
            while (!currentDate.isAfter(endDate)) {
                List<MealDto> allMeals = new ArrayList<>();
                String[] mealTypes = {"BREAKFAST", "LUNCH", "DINNER", "SNACK"};
                
                for (String type : mealTypes) {
                    List<MealDto> meals = mealService.getMealsForUser(user, currentDate, type);
                    allMeals.addAll(meals);
                }
                
                mealPlans.add(new MealPlanResponseDto(currentDate, allMeals));
                currentDate = currentDate.plusDays(1);
            }
            
            return ResponseEntity.ok(mealPlans);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get all available recipes
    @GetMapping("/recipes")
    public ResponseEntity<List<MealDto>> getAllRecipes() {
        try {
            List<MealDto> recipes = new ArrayList<>();
            mealService.getAllRecipes().forEach(recipe -> {
                MealDto mealDto = new MealDto();
                mealDto.setId(recipe.getId());
                mealDto.setName(recipe.getName());
                mealDto.setDescription(recipe.getDescription());
                mealDto.setCategory(recipe.getCategory());
                mealDto.setType(recipe.getType());
                mealDto.setCalories(recipe.getCalories());
                mealDto.setImageUrl(recipe.getImageUrl());
                recipes.add(mealDto);
            });
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get recipes by category
    @GetMapping("/recipes/category/{category}")
    public ResponseEntity<List<MealDto>> getRecipesByCategory(@PathVariable String category) {
        try {
            List<MealDto> recipes = new ArrayList<>();
            mealService.getRecipesByCategory(category).forEach(recipe -> {
                MealDto mealDto = new MealDto();
                mealDto.setId(recipe.getId());
                mealDto.setName(recipe.getName());
                mealDto.setDescription(recipe.getDescription());
                mealDto.setCategory(recipe.getCategory());
                mealDto.setType(recipe.getType());
                mealDto.setCalories(recipe.getCalories());
                mealDto.setImageUrl(recipe.getImageUrl());
                recipes.add(mealDto);
            });
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
