package com.mealplanner.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;
    
    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;
    
    @NotNull
    @Column(nullable = false)
    private Integer servings;
    
    @Column(name = "difficulty_level")
    private String difficultyLevel; // 'easy', 'medium', 'hard'
    
    @Column(name = "cuisine_type")
    private String cuisineType;
    
    @Column(name = "meal_type")
    private String mealType; // 'breakfast', 'lunch', 'dinner', 'snack'
    
    @Column(name = "calories_per_serving")
    private Integer caloriesPerServing;
    
    @Column(name = "protein_g", precision = 5, scale = 2)
    private BigDecimal proteinG;
    
    @Column(name = "carbs_g", precision = 5, scale = 2)
    private BigDecimal carbsG;
    
    @Column(name = "fat_g", precision = 5, scale = 2)
    private BigDecimal fatG;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "is_vegetarian")
    private Boolean isVegetarian = true;
    
    @Column(name = "is_vegan")
    private Boolean isVegan = true;
    
    @Column(name = "tags")
    private List<String> tags;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Recipe() {}
    
    public Recipe(String name, String description, Integer servings) {
        this.name = name;
        this.description = description;
        this.servings = servings;
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
    
    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }
    
    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }
    
    public Integer getCookTimeMinutes() {
        return cookTimeMinutes;
    }
    
    public void setCookTimeMinutes(Integer cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
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
    
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
    public Integer getCaloriesPerServing() {
        return caloriesPerServing;
    }
    
    public void setCaloriesPerServing(Integer caloriesPerServing) {
        this.caloriesPerServing = caloriesPerServing;
    }
    
    public BigDecimal getProteinG() {
        return proteinG;
    }
    
    public void setProteinG(BigDecimal proteinG) {
        this.proteinG = proteinG;
    }
    
    public BigDecimal getCarbsG() {
        return carbsG;
    }
    
    public void setCarbsG(BigDecimal carbsG) {
        this.carbsG = carbsG;
    }
    
    public BigDecimal getFatG() {
        return fatG;
    }
    
    public void setFatG(BigDecimal fatG) {
        this.fatG = fatG;
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
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public Boolean getIsVegetarian() {
        return isVegetarian;
    }
    
    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }
    
    public Boolean getIsVegan() {
        return isVegan;
    }
    
    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public Integer getTotalTimeMinutes() {
        return (prepTimeMinutes != null ? prepTimeMinutes : 0) + 
               (cookTimeMinutes != null ? cookTimeMinutes : 0);
    }
    
    public boolean isBreakfast() {
        return "breakfast".equalsIgnoreCase(mealType);
    }
    
    public boolean isLunch() {
        return "lunch".equalsIgnoreCase(mealType);
    }
    
    public boolean isDinner() {
        return "dinner".equalsIgnoreCase(mealType);
    }
    
    public boolean isSnack() {
        return "snack".equalsIgnoreCase(mealType);
    }
    
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag.toLowerCase());
    }
    
    public boolean isSuitableForDiet(String diet) {
        switch (diet.toLowerCase()) {
            case "vegetarian":
                return isVegetarian;
            case "vegan":
                return isVegan;
            default:
                return true;
        }
    }
    
    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mealType='" + mealType + '\'' +
                ", caloriesPerServing=" + caloriesPerServing +
                '}';
    }
} 