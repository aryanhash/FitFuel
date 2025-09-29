package com.mealplanner.controller;

import com.mealplanner.dto.MealDto;
import com.mealplanner.service.NutritionixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test/nutritionix")
@CrossOrigin(origins = "http://localhost:3000")
public class NutritionixTestController {
    
    @Autowired
    private NutritionixService nutritionixService;
    
    /**
     * Test Nutritionix API with a simple search
     */
    @GetMapping("/search")
    public ResponseEntity<?> testNutritionixSearch(
            @RequestParam(defaultValue = "chicken") String query,
            @RequestParam(defaultValue = "DINNER") String mealType,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(required = false) String diet) {
        
        try {
            List<MealDto> meals = nutritionixService.searchRecipes(query, mealType, maxCalories, diet);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("query", query);
            response.put("mealType", mealType);
            response.put("maxCalories", maxCalories);
            response.put("diet", diet);
            response.put("resultsCount", meals.size());
            response.put("meals", meals);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Test detailed nutrition lookup
     */
    @GetMapping("/nutrition")
    public ResponseEntity<?> testNutritionixNutrition(
            @RequestParam String foodName,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(defaultValue = "serving") String unit) {
        
        try {
            MealDto meal = nutritionixService.getDetailedNutrition(foodName, quantity, unit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("foodName", foodName);
            response.put("quantity", quantity);
            response.put("unit", unit);
            response.put("meal", meal);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
