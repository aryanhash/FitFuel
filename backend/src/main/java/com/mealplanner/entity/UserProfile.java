package com.mealplanner.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank
    @Column(name = "body_goal", nullable = false)
    private String bodyGoal; // 'fat_loss', 'muscle_gain', 'detox', 'maintenance'
    
    @NotBlank
    @Column(name = "food_preference", nullable = false)
    private String foodPreference; // 'vegetarian', 'vegan', 'eggs', 'non_vegetarian'
    
    @Column(name = "fasting_window_start")
    private LocalTime fastingWindowStart;
    
    @Column(name = "fasting_window_end")
    private LocalTime fastingWindowEnd;
    
    @Column(name = "workout_days")
    private List<String> workoutDays; // Array of days: ['monday', 'wednesday', 'friday']
    
    @Column(name = "daily_calorie_target")
    private Integer dailyCalorieTarget;
    
    @Column(name = "protein_target_g")
    private Integer proteinTargetG;
    
    @Column(name = "carbs_target_g")
    private Integer carbsTargetG;
    
    @Column(name = "fat_target_g")
    private Integer fatTargetG;
    
    @Column(name = "allergies")
    private List<String> allergies;
    
    @Column(name = "dislikes")
    private List<String> dislikes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserProfile() {}
    
    public UserProfile(User user, String bodyGoal, String foodPreference) {
        this.user = user;
        this.bodyGoal = bodyGoal;
        this.foodPreference = foodPreference;
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
    
    public String getBodyGoal() {
        return bodyGoal;
    }
    
    public void setBodyGoal(String bodyGoal) {
        this.bodyGoal = bodyGoal;
    }
    
    public String getFoodPreference() {
        return foodPreference;
    }
    
    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }
    
    public LocalTime getFastingWindowStart() {
        return fastingWindowStart;
    }
    
    public void setFastingWindowStart(LocalTime fastingWindowStart) {
        this.fastingWindowStart = fastingWindowStart;
    }
    
    public LocalTime getFastingWindowEnd() {
        return fastingWindowEnd;
    }
    
    public void setFastingWindowEnd(LocalTime fastingWindowEnd) {
        this.fastingWindowEnd = fastingWindowEnd;
    }
    
    public List<String> getWorkoutDays() {
        return workoutDays;
    }
    
    public void setWorkoutDays(List<String> workoutDays) {
        this.workoutDays = workoutDays;
    }
    
    public Integer getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }
    
    public void setDailyCalorieTarget(Integer dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }
    
    public Integer getProteinTargetG() {
        return proteinTargetG;
    }
    
    public void setProteinTargetG(Integer proteinTargetG) {
        this.proteinTargetG = proteinTargetG;
    }
    
    public Integer getCarbsTargetG() {
        return carbsTargetG;
    }
    
    public void setCarbsTargetG(Integer carbsTargetG) {
        this.carbsTargetG = carbsTargetG;
    }
    
    public Integer getFatTargetG() {
        return fatTargetG;
    }
    
    public void setFatTargetG(Integer fatTargetG) {
        this.fatTargetG = fatTargetG;
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
    
    // Helper methods
    public boolean hasFastingWindow() {
        return fastingWindowStart != null && fastingWindowEnd != null;
    }
    
    public boolean isWorkoutDay(String day) {
        return workoutDays != null && workoutDays.contains(day.toLowerCase());
    }
    
    public boolean hasAllergy(String food) {
        return allergies != null && allergies.contains(food.toLowerCase());
    }
    
    public boolean dislikesFood(String food) {
        return dislikes != null && dislikes.contains(food.toLowerCase());
    }
    
    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", bodyGoal='" + bodyGoal + '\'' +
                ", foodPreference='" + foodPreference + '\'' +
                ", dailyCalorieTarget=" + dailyCalorieTarget +
                '}';
    }
} 