package com.mealplanner.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_items")
public class FoodItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @NotBlank
    @Column(nullable = false)
    private String category; // 'protein', 'carbs', 'fats', 'vegetables', 'fruits'
    
    @NotNull
    @Column(name = "calories_per_100g", nullable = false)
    private Integer caloriesPer100g;
    
    @Column(name = "protein_g", precision = 5, scale = 2)
    private BigDecimal proteinG;
    
    @Column(name = "carbs_g", precision = 5, scale = 2)
    private BigDecimal carbsG;
    
    @Column(name = "fat_g", precision = 5, scale = 2)
    private BigDecimal fatG;
    
    @Column(name = "fiber_g", precision = 5, scale = 2)
    private BigDecimal fiberG;
    
    @Column(name = "sugar_g", precision = 5, scale = 2)
    private BigDecimal sugarG;
    
    @Column(name = "sodium_mg")
    private Integer sodiumMg;
    
    @Column(name = "is_vegetarian")
    private Boolean isVegetarian = true;
    
    @Column(name = "is_vegan")
    private Boolean isVegan = true;
    
    @Column(name = "is_gluten_free")
    private Boolean isGlutenFree = true;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public FoodItem() {}
    
    public FoodItem(String name, String category, Integer caloriesPer100g) {
        this.name = name;
        this.category = category;
        this.caloriesPer100g = caloriesPer100g;
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
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Integer getCaloriesPer100g() {
        return caloriesPer100g;
    }
    
    public void setCaloriesPer100g(Integer caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
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
    
    public BigDecimal getFiberG() {
        return fiberG;
    }
    
    public void setFiberG(BigDecimal fiberG) {
        this.fiberG = fiberG;
    }
    
    public BigDecimal getSugarG() {
        return sugarG;
    }
    
    public void setSugarG(BigDecimal sugarG) {
        this.sugarG = sugarG;
    }
    
    public Integer getSodiumMg() {
        return sodiumMg;
    }
    
    public void setSodiumMg(Integer sodiumMg) {
        this.sodiumMg = sodiumMg;
    }
    
    public Boolean getIsVegetarian() {
        return isVegetarian;
    }
    
    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }
    
    public Boolean getIsVegan() {
        return isVegan;
    }
    
    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }
    
    public Boolean getIsGlutenFree() {
        return isGlutenFree;
    }
    
    public void setIsGlutenFree(Boolean isGlutenFree) {
        this.isGlutenFree = isGlutenFree;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public boolean isProtein() {
        return "protein".equalsIgnoreCase(category);
    }
    
    public boolean isCarb() {
        return "carbs".equalsIgnoreCase(category);
    }
    
    public boolean isFat() {
        return "fats".equalsIgnoreCase(category);
    }
    
    public boolean isVegetable() {
        return "vegetables".equalsIgnoreCase(category);
    }
    
    public boolean isFruit() {
        return "fruits".equalsIgnoreCase(category);
    }
    
    public boolean isSuitableForDiet(String diet) {
        switch (diet.toLowerCase()) {
            case "vegetarian":
                return isVegetarian;
            case "vegan":
                return isVegan;
            case "gluten_free":
                return isGlutenFree;
            default:
                return true;
        }
    }
    
    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", caloriesPer100g=" + caloriesPer100g +
                '}';
    }
} 