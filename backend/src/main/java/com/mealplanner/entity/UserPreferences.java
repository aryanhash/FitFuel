package com.mealplanner.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "user_preferences")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "diet_type")
    private String dietType;
    
    @Column(name = "daily_calorie_target")
    private Integer dailyCalorieTarget;
    
    @Column(name = "allergies", columnDefinition = "TEXT[]")
    private List<String> allergies;
    
    @Column(name = "dislikes", columnDefinition = "TEXT[]")
    private List<String> dislikes;
    
    @Column(name = "preferred_cuisines", columnDefinition = "TEXT[]")
    private List<String> preferredCuisines;
    
    @Column(name = "cooking_skill_level")
    private String cookingSkillLevel;
    
    @Column(name = "meal_reminders")
    private Boolean mealReminders;
    
    @Column(name = "notification_time")
    private LocalTime notificationTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserPreferences() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public String getDietType() {
        return dietType;
    }
    
    public void setDietType(String dietType) {
        this.dietType = dietType;
    }
    
    public Integer getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }
    
    public void setDailyCalorieTarget(Integer dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
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
    
    public List<String> getPreferredCuisines() {
        return preferredCuisines;
    }
    
    public void setPreferredCuisines(List<String> preferredCuisines) {
        this.preferredCuisines = preferredCuisines;
    }
    
    public String getCookingSkillLevel() {
        return cookingSkillLevel;
    }
    
    public void setCookingSkillLevel(String cookingSkillLevel) {
        this.cookingSkillLevel = cookingSkillLevel;
    }
    
    public Boolean getMealReminders() {
        return mealReminders;
    }
    
    public void setMealReminders(Boolean mealReminders) {
        this.mealReminders = mealReminders;
    }
    
    public LocalTime getNotificationTime() {
        return notificationTime;
    }
    
    public void setNotificationTime(LocalTime notificationTime) {
        this.notificationTime = notificationTime;
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
}