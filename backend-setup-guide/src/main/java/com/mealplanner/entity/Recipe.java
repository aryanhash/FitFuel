package com.mealplanner.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "ingredients", columnDefinition = "TEXT[]")
    private List<String> ingredients;
    
    @Column(name = "prep_time")
    private Integer prepTimeMinutes;
    
    @Column(name = "cook_time")
    private Integer cookTimeMinutes;
    
    @Column(name = "total_time")
    private Integer totalTimeMinutes;
    
    @Column(name = "servings")
    private Integer servings;
    
    @Column(name = "difficulty_level")
    private String difficultyLevel;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "cuisine_type")
    private String cuisineType;
    
    @Column(name = "diet_type")
    private String dietType;
    
    @NotNull
    @Column(name = "type", nullable = false)
    private String type; // "Veg" or "Non-Veg"
    
    @Column(name = "calories")
    private Double calories;
    
    @Column(name = "protein")
    private Double proteinGrams;
    
    @Column(name = "carbs")
    private Double carbsGrams;
    
    @Column(name = "fat")
    private Double fatGrams;
    
    @Column(name = "fiber")
    private Double fiberGrams;
    
    @Column(name = "sugar")
    private Double sugarGrams;
    
    @Column(name = "sodium")
    private Double sodiumMilligrams;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "external_id")
    private String externalId;
    
    @Column(name = "source")
    private String source; // "LOCAL", "NUTRITIONIX", "EDAMAM"
    
    @Column(name = "external_url")
    private String externalUrl;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    // Constructors
    public Recipe() {}
    
    public Recipe(String name, String category, String type) {
        this.name = name;
        this.category = category;
        this.type = type;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    
    public Integer getPrepTimeMinutes() { return prepTimeMinutes; }
    public void setPrepTimeMinutes(Integer prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }
    
    public Integer getCookTimeMinutes() { return cookTimeMinutes; }
    public void setCookTimeMinutes(Integer cookTimeMinutes) { this.cookTimeMinutes = cookTimeMinutes; }
    
    public Integer getTotalTimeMinutes() { return totalTimeMinutes; }
    public void setTotalTimeMinutes(Integer totalTimeMinutes) { this.totalTimeMinutes = totalTimeMinutes; }
    
    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }
    
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
    
    public String getDietType() { return dietType; }
    public void setDietType(String dietType) { this.dietType = dietType; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }
    
    public Double getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(Double proteinGrams) { this.proteinGrams = proteinGrams; }
    
    public Double getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(Double carbsGrams) { this.carbsGrams = carbsGrams; }
    
    public Double getFatGrams() { return fatGrams; }
    public void setFatGrams(Double fatGrams) { this.fatGrams = fatGrams; }
    
    public Double getFiberGrams() { return fiberGrams; }
    public void setFiberGrams(Double fiberGrams) { this.fiberGrams = fiberGrams; }
    
    public Double getSugarGrams() { return sugarGrams; }
    public void setSugarGrams(Double sugarGrams) { this.sugarGrams = sugarGrams; }
    
    public Double getSodiumMilligrams() { return sodiumMilligrams; }
    public void setSodiumMilligrams(Double sodiumMilligrams) { this.sodiumMilligrams = sodiumMilligrams; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getExternalUrl() { return externalUrl; }
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
