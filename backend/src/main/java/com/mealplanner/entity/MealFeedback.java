package com.mealplanner.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_feedback")
public class MealFeedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_time", nullable = false)
    private MealTime mealTime;
    
    @Column(name = "liked")
    private Boolean liked;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_with_recipe_id")
    private Recipe replacedWithRecipe;
    
    @Column(name = "feedback_notes", columnDefinition = "TEXT")
    private String feedbackNotes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum MealTime {
        BREAKFAST, LUNCH, DINNER
    }
    
    // Default constructor
    public MealFeedback() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with required fields
    public MealFeedback(User user, LocalDate date, Recipe recipe, MealTime mealTime) {
        this();
        this.user = user;
        this.date = date;
        this.recipe = recipe;
        this.mealTime = mealTime;
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
    
    public Recipe getRecipe() {
        return recipe;
    }
    
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
    public MealTime getMealTime() {
        return mealTime;
    }
    
    public void setMealTime(MealTime mealTime) {
        this.mealTime = mealTime;
    }
    
    public Boolean getLiked() {
        return liked;
    }
    
    public void setLiked(Boolean liked) {
        this.liked = liked;
    }
    
    public Recipe getReplacedWithRecipe() {
        return replacedWithRecipe;
    }
    
    public void setReplacedWithRecipe(Recipe replacedWithRecipe) {
        this.replacedWithRecipe = replacedWithRecipe;
    }
    
    public String getFeedbackNotes() {
        return feedbackNotes;
    }
    
    public void setFeedbackNotes(String feedbackNotes) {
        this.feedbackNotes = feedbackNotes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 