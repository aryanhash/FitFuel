package com.mealplanner.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipes")
public class Meal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "category", nullable = false)
    private String mealType;
    
    @Column(name = "calories")
    private Integer calories;
    
    @Column(name = "protein")
    private Double protein;
    
    @Column(name = "carbs")
    private Double carbs;
    
    @Column(name = "fat")
    private Double fat;
    
    @Column(name = "fiber")
    private Double fiber;
    
    @Column(name = "sugar")
    private Double sugar;
    
    @Column(name = "sodium")
    private Double sodium;
    
    @Column(name = "prep_time")
    private Integer prepTime;
    
    @Column(name = "cook_time")
    private Integer cookTime;
    
    @Column(name = "servings")
    private Integer servings;
    
    @Column(name = "difficulty_level")
    private String difficultyLevel;
    
    @Column(name = "cuisine_type")
    private String cuisineType;
    
    @Column(name = "type")
    private String dietType;
    
    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Meal() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with required fields
    public Meal(String name, String mealType) {
        this();
        this.name = name;
        this.mealType = mealType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
    public Integer getCalories() {
        return calories;
    }
    
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
    
    public Double getProtein() {
        return protein;
    }
    
    public void setProtein(Double protein) {
        this.protein = protein;
    }
    
    public Double getCarbs() {
        return carbs;
    }
    
    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }
    
    public Double getFat() {
        return fat;
    }
    
    public void setFat(Double fat) {
        this.fat = fat;
    }
    
    public Double getFiber() {
        return fiber;
    }
    
    public void setFiber(Double fiber) {
        this.fiber = fiber;
    }
    
    public Double getSugar() {
        return sugar;
    }
    
    public void setSugar(Double sugar) {
        this.sugar = sugar;
    }
    
    public Double getSodium() {
        return sodium;
    }
    
    public void setSodium(Double sodium) {
        this.sodium = sodium;
    }
    
    public Integer getPrepTime() {
        return prepTime;
    }
    
    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }
    
    public Integer getCookTime() {
        return cookTime;
    }
    
    public void setCookTime(Integer cookTime) {
        this.cookTime = cookTime;
    }
    
    public Integer getServings() {
        return servings;
    }
    
    public void setServings(Integer servings) {
        this.servings = servings;
    }
    
    public String getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    public String getCuisineType() {
        return cuisineType;
    }
    
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }
    
    public String getDietType() {
        return dietType;
    }
    
    public void setDietType(String dietType) {
        this.dietType = dietType;
    }
    
    public String getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 