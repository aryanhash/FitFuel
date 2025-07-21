package com.mealplanner.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_meal_plans", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "date", "meal_time", "year"})
})
public class UserMealPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_time", nullable = false)
    private MealTime mealTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "is_eaten")
    private Boolean isEaten;
    
    @Column(name = "is_skipped")
    private Boolean isSkipped;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum MealTime {
        BREAKFAST, LUNCH, DINNER
    }
    
    // Default constructor
    public UserMealPlan() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isEaten = false;
        this.isSkipped = false;
    }
    
    // Constructor with required fields
    public UserMealPlan(User user, LocalDate date, MealTime mealTime, Recipe recipe, Integer year) {
        this();
        this.user = user;
        this.date = date;
        this.mealTime = mealTime;
        this.recipe = recipe;
        this.year = year;
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
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public MealTime getMealTime() {
        return mealTime;
    }
    
    public void setMealTime(MealTime mealTime) {
        this.mealTime = mealTime;
    }
    
    public Recipe getRecipe() {
        return recipe;
    }
    
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public Boolean getIsEaten() {
        return isEaten;
    }
    
    public void setIsEaten(Boolean isEaten) {
        this.isEaten = isEaten;
    }
    
    public Boolean getIsSkipped() {
        return isSkipped;
    }
    
    public void setIsSkipped(Boolean isSkipped) {
        this.isSkipped = isSkipped;
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