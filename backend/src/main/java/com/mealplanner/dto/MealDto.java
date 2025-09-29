package com.mealplanner.dto;

import java.util.List;

public class MealDto {
    private Long id;
    private String externalId;
    private String sourceApi;
    private String name;
    private String description;
    private String imageUrl;
    private String sourceUrl;
    private String mealType;
    private String cuisineType;
    private String dietType;
    private String difficultyLevel;
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer servings;
    private Integer calories;
    private Double proteinGrams;
    private Double fatGrams;
    private Double carbsGrams;
    private Double fiberGrams;
    private Double sugarGrams;
    private Double sodiumMilliGrams;
    private List<String> ingredients;
    private List<String> instructions;
    private String nutritionInfo;
    private List<String> tags;
    private List<String> dietLabels;
    private List<String> healthLabels;
    private List<String> cuisineTypes;
    private List<String> mealTypes;
    private List<String> dishTypes;

    // Constructors
    public MealDto() {}

    public MealDto(String name, String mealType, Integer calories) {
        this.name = name;
        this.mealType = mealType;
        this.calories = calories;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getSourceApi() { return sourceApi; }
    public void setSourceApi(String sourceApi) { this.sourceApi = sourceApi; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
    public String getDietType() { return dietType; }
    public void setDietType(String dietType) { this.dietType = dietType; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public Integer getPrepTimeMinutes() { return prepTimeMinutes; }
    public void setPrepTimeMinutes(Integer prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }
    public Integer getCookTimeMinutes() { return cookTimeMinutes; }
    public void setCookTimeMinutes(Integer cookTimeMinutes) { this.cookTimeMinutes = cookTimeMinutes; }
    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }
    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }
    public Double getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(Double proteinGrams) { this.proteinGrams = proteinGrams; }
    public Double getFatGrams() { return fatGrams; }
    public void setFatGrams(Double fatGrams) { this.fatGrams = fatGrams; }
    public Double getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(Double carbsGrams) { this.carbsGrams = carbsGrams; }
    public Double getFiberGrams() { return fiberGrams; }
    public void setFiberGrams(Double fiberGrams) { this.fiberGrams = fiberGrams; }
    public Double getSugarGrams() { return sugarGrams; }
    public void setSugarGrams(Double sugarGrams) { this.sugarGrams = sugarGrams; }
    public Double getSodiumMilliGrams() { return sodiumMilliGrams; }
    public void setSodiumMilliGrams(Double sodiumMilliGrams) { this.sodiumMilliGrams = sodiumMilliGrams; }
    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }
    public String getNutritionInfo() { return nutritionInfo; }
    public void setNutritionInfo(String nutritionInfo) { this.nutritionInfo = nutritionInfo; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<String> getDietLabels() { return dietLabels; }
    public void setDietLabels(List<String> dietLabels) { this.dietLabels = dietLabels; }
    public List<String> getHealthLabels() { return healthLabels; }
    public void setHealthLabels(List<String> healthLabels) { this.healthLabels = healthLabels; }
    public List<String> getCuisineTypes() { return cuisineTypes; }
    public void setCuisineTypes(List<String> cuisineTypes) { this.cuisineTypes = cuisineTypes; }
    public List<String> getMealTypes() { return mealTypes; }
    public void setMealTypes(List<String> mealTypes) { this.mealTypes = mealTypes; }
    public List<String> getDishTypes() { return dishTypes; }
    public void setDishTypes(List<String> dishTypes) { this.dishTypes = dishTypes; }
}