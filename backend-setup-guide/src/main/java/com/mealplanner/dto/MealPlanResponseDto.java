package com.mealplanner.dto;

import java.time.LocalDate;
import java.util.List;

public class MealPlanResponseDto {
    private LocalDate date;
    private List<MealDto> meals;
    private String error;
    
    // Constructors
    public MealPlanResponseDto() {}
    
    public MealPlanResponseDto(LocalDate date, List<MealDto> meals) {
        this.date = date;
        this.meals = meals;
    }
    
    public MealPlanResponseDto(LocalDate date, String error) {
        this.date = date;
        this.error = error;
    }
    
    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public List<MealDto> getMeals() { return meals; }
    public void setMeals(List<MealDto> meals) { this.meals = meals; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
