package com.mealplanner.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_plan_meals")
public class MealPlanMeal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_day_id", nullable = false)
    private MealPlanDay mealPlanDay;
    
    @NotBlank
    @Column(name = "meal_type", nullable = false)
    private String mealType; // 'breakfast', 'lunch', 'dinner', 'snack'
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    
    @Column(name = "custom_meal_name")
    private String customMealName;
    
    @Column(name = "custom_meal_description", columnDefinition = "TEXT")
    private String customMealDescription;
    
    @Column
    private Integer calories;
    
    @Column(name = "protein_g", precision = 5, scale = 2)
    private BigDecimal proteinG;
    
    @Column(name = "carbs_g", precision = 5, scale = 2)
    private BigDecimal carbsG;
    
    @Column(name = "fat_g", precision = 5, scale = 2)
    private BigDecimal fatG;
    
    @Column(name = "meal_time")
    private LocalTime mealTime;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // Constructors
    public MealPlanMeal() {}
    
    public MealPlanMeal(MealPlanDay mealPlanDay, String mealType) {
        this.mealPlanDay = mealPlanDay;
        this.mealType = mealType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public MealPlanDay getMealPlanDay() {
        return mealPlanDay;
    }
    
    public void setMealPlanDay(MealPlanDay mealPlanDay) {
        this.mealPlanDay = mealPlanDay;
    }
    
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
    public Recipe getRecipe() {
        return recipe;
    }
    
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
    public String getCustomMealName() {
        return customMealName;
    }
    
    public void setCustomMealName(String customMealName) {
        this.customMealName = customMealName;
    }
    
    public String getCustomMealDescription() {
        return customMealDescription;
    }
    
    public void setCustomMealDescription(String customMealDescription) {
        this.customMealDescription = customMealDescription;
    }
    
    public Integer getCalories() {
        return calories;
    }
    
    public void setCalories(Integer calories) {
        this.calories = calories;
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
    
    public LocalTime getMealTime() {
        return mealTime;
    }
    
    public void setMealTime(LocalTime mealTime) {
        this.mealTime = mealTime;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Helper methods
    public String getMealName() {
        if (recipe != null) {
            return recipe.getName();
        }
        return customMealName != null ? customMealName : mealType;
    }
    
    public String getMealDescription() {
        if (recipe != null) {
            return recipe.getDescription();
        }
        return customMealDescription;
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
    
    public boolean hasRecipe() {
        return recipe != null;
    }
    
    public boolean isCustomMeal() {
        return recipe == null && customMealName != null;
    }
    
    @Override
    public String toString() {
        return "MealPlanMeal{" +
                "id=" + id +
                ", mealType='" + mealType + '\'' +
                ", mealName='" + getMealName() + '\'' +
                ", calories=" + calories +
                '}';
    }
} 