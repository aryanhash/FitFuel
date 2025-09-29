package com.mealplanner.dto;

import java.util.List;

public class UserPreferencesDto {
    private Long userId;
    private String dietaryPreference;
    private Integer calorieGoal;
    private List<String> allergens;
    private List<String> excludedIngredients;
    private String cookingSkillLevel;
    private List<String> preferredCuisine;
    private List<String> preferredMealTypes;

    // Constructors
    public UserPreferencesDto() {}

    public UserPreferencesDto(Long userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDietaryPreference() { return dietaryPreference; }
    public void setDietaryPreference(String dietaryPreference) { this.dietaryPreference = dietaryPreference; }
    public Integer getCalorieGoal() { return calorieGoal; }
    public void setCalorieGoal(Integer calorieGoal) { this.calorieGoal = calorieGoal; }
    public List<String> getAllergens() { return allergens; }
    public void setAllergens(List<String> allergens) { this.allergens = allergens; }
    public List<String> getExcludedIngredients() { return excludedIngredients; }
    public void setExcludedIngredients(List<String> excludedIngredients) { this.excludedIngredients = excludedIngredients; }
    public String getCookingSkillLevel() { return cookingSkillLevel; }
    public void setCookingSkillLevel(String cookingSkillLevel) { this.cookingSkillLevel = cookingSkillLevel; }
    public List<String> getPreferredCuisine() { return preferredCuisine; }
    public void setPreferredCuisine(List<String> preferredCuisine) { this.preferredCuisine = preferredCuisine; }
    public List<String> getPreferredMealTypes() { return preferredMealTypes; }
    public void setPreferredMealTypes(List<String> preferredMealTypes) { this.preferredMealTypes = preferredMealTypes; }
}