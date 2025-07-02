package com.mealplanner.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plans")
public class MealPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "total_calories")
    private Integer totalCalories;
    
    @Column(name = "total_protein_g", precision = 6, scale = 2)
    private BigDecimal totalProteinG;
    
    @Column(name = "total_carbs_g", precision = 6, scale = 2)
    private BigDecimal totalCarbsG;
    
    @Column(name = "total_fat_g", precision = 6, scale = 2)
    private BigDecimal totalFatG;
    
    @Column
    private String status = "active"; // 'active', 'completed', 'archived'
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealPlanDay> mealPlanDays = new ArrayList<>();
    
    // Constructors
    public MealPlan() {}
    
    public MealPlan(User user, String name, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Integer getTotalCalories() {
        return totalCalories;
    }
    
    public void setTotalCalories(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }
    
    public BigDecimal getTotalProteinG() {
        return totalProteinG;
    }
    
    public void setTotalProteinG(BigDecimal totalProteinG) {
        this.totalProteinG = totalProteinG;
    }
    
    public BigDecimal getTotalCarbsG() {
        return totalCarbsG;
    }
    
    public void setTotalCarbsG(BigDecimal totalCarbsG) {
        this.totalCarbsG = totalCarbsG;
    }
    
    public BigDecimal getTotalFatG() {
        return totalFatG;
    }
    
    public void setTotalFatG(BigDecimal totalFatG) {
        this.totalFatG = totalFatG;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<MealPlanDay> getMealPlanDays() {
        return mealPlanDays;
    }
    
    public void setMealPlanDays(List<MealPlanDay> mealPlanDays) {
        this.mealPlanDays = mealPlanDays;
    }
    
    // Helper methods
    public int getDurationInDays() {
        return (int) startDate.until(endDate).getDays() + 1;
    }
    
    public boolean isActive() {
        return "active".equals(status);
    }
    
    public boolean isCompleted() {
        return "completed".equals(status);
    }
    
    public boolean isArchived() {
        return "archived".equals(status);
    }
    
    public boolean containsDate(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    public void addMealPlanDay(MealPlanDay mealPlanDay) {
        mealPlanDays.add(mealPlanDay);
        mealPlanDay.setMealPlan(this);
    }
    
    public void removeMealPlanDay(MealPlanDay mealPlanDay) {
        mealPlanDays.remove(mealPlanDay);
        mealPlanDay.setMealPlan(null);
    }
    
    public void calculateTotals() {
        this.totalCalories = mealPlanDays.stream()
                .mapToInt(day -> day.getTotalCalories() != null ? day.getTotalCalories() : 0)
                .sum();
        
        this.totalProteinG = mealPlanDays.stream()
                .map(MealPlanDay::getTotalProteinG)
                .filter(protein -> protein != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalCarbsG = mealPlanDays.stream()
                .map(MealPlanDay::getTotalCarbsG)
                .filter(carbs -> carbs != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalFatG = mealPlanDays.stream()
                .map(MealPlanDay::getTotalFatG)
                .filter(fat -> fat != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public String toString() {
        return "MealPlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
} 