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
@Table(name = "meal_plan_days")
public class MealPlanDay {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;
    
    @NotBlank
    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // 'monday', 'tuesday', etc.
    
    @NotNull
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "total_calories")
    private Integer totalCalories;
    
    @Column(name = "total_protein_g", precision = 6, scale = 2)
    private BigDecimal totalProteinG;
    
    @Column(name = "total_carbs_g", precision = 6, scale = 2)
    private BigDecimal totalCarbsG;
    
    @Column(name = "total_fat_g", precision = 6, scale = 2)
    private BigDecimal totalFatG;
    
    @OneToMany(mappedBy = "mealPlanDay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealPlanMeal> meals = new ArrayList<>();
    
    // Constructors
    public MealPlanDay() {}
    
    public MealPlanDay(MealPlan mealPlan, String dayOfWeek, LocalDate date) {
        this.mealPlan = mealPlan;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public MealPlan getMealPlan() {
        return mealPlan;
    }
    
    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
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
    
    public List<MealPlanMeal> getMeals() {
        return meals;
    }
    
    public void setMeals(List<MealPlanMeal> meals) {
        this.meals = meals;
    }
    
    // Helper methods
    public void addMeal(MealPlanMeal meal) {
        meals.add(meal);
        meal.setMealPlanDay(this);
    }
    
    public void removeMeal(MealPlanMeal meal) {
        meals.remove(meal);
        meal.setMealPlanDay(null);
    }
    
    public void calculateTotals() {
        this.totalCalories = meals.stream()
                .mapToInt(meal -> meal.getCalories() != null ? meal.getCalories() : 0)
                .sum();
        
        this.totalProteinG = meals.stream()
                .map(MealPlanMeal::getProteinG)
                .filter(protein -> protein != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalCarbsG = meals.stream()
                .map(MealPlanMeal::getCarbsG)
                .filter(carbs -> carbs != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalFatG = meals.stream()
                .map(MealPlanMeal::getFatG)
                .filter(fat -> fat != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public MealPlanMeal getMealByType(String mealType) {
        return meals.stream()
                .filter(meal -> mealType.equalsIgnoreCase(meal.getMealType()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public String toString() {
        return "MealPlanDay{" +
                "id=" + id +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", date=" + date +
                ", totalCalories=" + totalCalories +
                '}';
    }
} 