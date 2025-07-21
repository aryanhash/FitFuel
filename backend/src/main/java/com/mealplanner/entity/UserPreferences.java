package com.mealplanner.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "daily_calorie_target")
    private Integer dailyCalorieTarget;
    
    @Column(name = "food_preference")
    private String foodPreference; // e.g., "vegetarian", "vegan", "keto", etc.
    
    @ElementCollection
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "user_preferences_id"))
    @Column(name = "allergy")
    private List<String> allergies;
    
    @ElementCollection
    @CollectionTable(name = "user_dislikes", joinColumns = @JoinColumn(name = "user_preferences_id"))
    @Column(name = "dislike")
    private List<String> dislikes;
    
    @Column(name = "preferred_cuisine")
    private String preferredCuisine;
    
    @Column(name = "cooking_skill_level")
    private String cookingSkillLevel; // "beginner", "intermediate", "advanced"
    
    @Column(name = "max_prep_time")
    private Integer maxPrepTime; // in minutes
    
    @Column(name = "max_cook_time")
    private Integer maxCookTime; // in minutes
    
    @Column(name = "serving_size")
    private Integer servingSize;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public UserPreferences() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with required fields
    public UserPreferences(User user) {
        this();
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Integer getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }
    
    public void setDailyCalorieTarget(Integer dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }
    
    public String getFoodPreference() {
        return foodPreference;
    }
    
    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }
    
    public List<String> getAllergies() {
        return allergies;
    }
    
    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }
    
    public List<String> getDislikes() {
        return dislikes;
    }
    
    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }
    
    public String getPreferredCuisine() {
        return preferredCuisine;
    }
    
    public void setPreferredCuisine(String preferredCuisine) {
        this.preferredCuisine = preferredCuisine;
    }
    
    public String getCookingSkillLevel() {
        return cookingSkillLevel;
    }
    
    public void setCookingSkillLevel(String cookingSkillLevel) {
        this.cookingSkillLevel = cookingSkillLevel;
    }
    
    public Integer getMaxPrepTime() {
        return maxPrepTime;
    }
    
    public void setMaxPrepTime(Integer maxPrepTime) {
        this.maxPrepTime = maxPrepTime;
    }
    
    public Integer getMaxCookTime() {
        return maxCookTime;
    }
    
    public void setMaxCookTime(Integer maxCookTime) {
        this.maxCookTime = maxCookTime;
    }
    
    public Integer getServingSize() {
        return servingSize;
    }
    
    public void setServingSize(Integer servingSize) {
        this.servingSize = servingSize;
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