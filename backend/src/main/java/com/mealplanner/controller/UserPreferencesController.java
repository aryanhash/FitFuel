package com.mealplanner.controller;

import com.mealplanner.dto.UserPreferencesDto;
import com.mealplanner.entity.User;
import com.mealplanner.entity.UserPreferences;
import com.mealplanner.service.EnhancedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserPreferencesController {
    
    @Autowired
    private EnhancedUserService userService;
    
    /**
     * Get user preferences
     */
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<?> getUserPreferences(@PathVariable Long userId) {
        try {
            Optional<UserPreferences> preferences = userService.getUserPreferences(userId);
            
            if (preferences.isPresent()) {
                return ResponseEntity.ok(preferences.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User preferences not found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get preferences: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Update user preferences
     */
    @PutMapping("/preferences/{userId}")
    public ResponseEntity<?> updateUserPreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferencesDto preferencesDto) {
        
        try {
            UserPreferences updatedPreferences = userService.updateUserPreferences(userId, preferencesDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Preferences updated successfully");
            response.put("preferences", updatedPreferences);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update preferences: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Create new user
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserCreateRequest request) {
        try {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPasswordHash(request.getPasswordHash()); // In real app, hash this properly
            
            User createdUser = userService.createUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("userId", createdUser.getId());
            response.put("user", createdUser);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user profile
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Optional<UserPreferences> preferences = userService.getUserPreferences(userId);
            Map<String, Object> userInsights = userService.getUserDietaryInsights(userId);
            Map<String, Object> stats = userService.getUserMealPlanStats(userId);
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("user", user);
            profile.put("preferences", preferences.orElse(null));
            profile.put("insights", userInsights);
            profile.put("stats", stats);
            
            return ResponseEntity.ok(profile);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Request class for user creation
     */
    public static class UserCreateRequest {
        private String name;
        private String email;
        private String passwordHash;
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPasswordHash() {
            return passwordHash;
        }
        
        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }
    }
}
