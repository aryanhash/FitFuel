package com.mealplanner.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_meal_plans", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"meal_date", "meal_type", "created_for_year"})
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DailyMealPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;
    
    @Column(name = "meal_type", nullable = false)
    private String mealType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Meal meal;
    
    @Column(name = "created_for_year", nullable = false)
    private Integer createdForYear;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public DailyMealPlan() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with required fields
    public DailyMealPlan(LocalDate mealDate, String mealType, Meal meal, Integer createdForYear) {
        this();
        this.mealDate = mealDate;
        this.mealType = mealType;
        this.meal = meal;
        this.createdForYear = createdForYear;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getMealDate() {
        return mealDate;
    }
    
    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }
    
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
    public Meal getMeal() {
        return meal;
    }
    
    public void setMeal(Meal meal) {
        this.meal = meal;
    }
    
    public Integer getCreatedForYear() {
        return createdForYear;
    }
    
    public void setCreatedForYear(Integer createdForYear) {
        this.createdForYear = createdForYear;
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