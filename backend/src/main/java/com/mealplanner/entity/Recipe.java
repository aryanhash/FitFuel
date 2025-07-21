package com.mealplanner.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RecipeType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MealCategory category;
    
    @Column(name = "ingredients", columnDefinition = "TEXT", nullable = false)
    private String ingredients;
    
    @Column(name = "instructions", columnDefinition = "TEXT", nullable = false)
    private String instructions;
    
    @Column(name = "calories", nullable = false)
    private Integer calories;
    
    @Column(name = "protein", precision = 5, scale = 2)
    private BigDecimal protein;
    
    @Column(name = "carbs", precision = 5, scale = 2)
    private BigDecimal carbs;
    
    @Column(name = "fat", precision = 5, scale = 2)
    private BigDecimal fat;
    
    @Column(name = "fiber", precision = 5, scale = 2)
    private BigDecimal fiber;
    
    @ElementCollection
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "tag")
    private List<String> tags;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "prep_time")
    private Integer prepTime;
    
    @Column(name = "cook_time")
    private Integer cookTime;
    
    @Column(name = "servings")
    private Integer servings;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private Difficulty difficulty;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum RecipeType {
        VEG, NON_VEG
    }
    
    public enum MealCategory {
        BREAKFAST, LUNCH, DINNER, SNACK
    }
    
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    
    // Default constructor
    public Recipe() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.servings = 1;
        this.difficulty = Difficulty.MEDIUM;
    }
    
    // Constructor with required fields
    public Recipe(String name, RecipeType type, MealCategory category, String ingredients, 
                  String instructions, Integer calories) {
        this();
        this.name = name;
        this.type = type;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.calories = calories;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public RecipeType getType() {
        return type;
    }
    
    public void setType(RecipeType type) {
        this.type = type;
    }
    
    public MealCategory getCategory() {
        return category;
    }
    
    public void setCategory(MealCategory category) {
        this.category = category;
    }
    
    public String getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public Integer getCalories() {
        return calories;
    }
    
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
    
    public BigDecimal getProtein() {
        return protein;
    }
    
    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }
    
    public BigDecimal getCarbs() {
        return carbs;
    }
    
    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }
    
    public BigDecimal getFat() {
        return fat;
    }
    
    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }
    
    public BigDecimal getFiber() {
        return fiber;
    }
    
    public void setFiber(BigDecimal fiber) {
        this.fiber = fiber;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Integer getPrepTime() {
        return prepTime;
    }
    
    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }
    
    public Integer getCookTime() {
        return cookTime;
    }
    
    public void setCookTime(Integer cookTime) {
        this.cookTime = cookTime;
    }
    
    public Integer getServings() {
        return servings;
    }
    
    public void setServings(Integer servings) {
        this.servings = servings;
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
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