package com.mealplanner.controller;

import com.mealplanner.service.GroceryListService;
import com.mealplanner.repository.UserRepository;
import com.mealplanner.repository.DailyMealPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/grocery-list")
@CrossOrigin(origins = "*")
public class GroceryListController {

    @Autowired
    private GroceryListService groceryListService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DailyMealPlanRepository dailyMealPlanRepository;

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyGroceryList(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal
    ) {
        // Handle case where Principal is null (no authentication)
        String username = "test@example.com"; // Default user for testing
        if (principal != null) {
            username = principal.getName();
        }
        
        Map<String, Object> groceryList = groceryListService.getMonthlyGroceryList(username, year, month);
        return ResponseEntity.ok(groceryList);
    }

    @GetMapping("/test")
    public ResponseEntity<?> testGroceryList() {
        // Test endpoint that doesn't require authentication
        String username = "test@example.com";
        int year = 2024;
        int month = 1;
        
        Map<String, Object> groceryList = groceryListService.getMonthlyGroceryList(username, year, month);
        return ResponseEntity.ok(groceryList);
    }
    
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            // Test database connectivity
            long userCount = userRepository.count();
            long mealPlanCount = dailyMealPlanRepository.count();
            
            Map<String, Object> health = Map.of(
                "status", "healthy",
                "database", "connected",
                "userCount", userCount,
                "mealPlanCount", mealPlanCount
            );
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            Map<String, Object> health = Map.of(
                "status", "unhealthy",
                "error", e.getMessage()
            );
            return ResponseEntity.status(500).body(health);
        }
    }
} 