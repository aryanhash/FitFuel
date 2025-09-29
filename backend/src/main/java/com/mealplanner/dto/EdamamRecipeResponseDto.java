package com.mealplanner.dto;

import java.util.List;

public class EdamamRecipeResponseDto {
    private List<Hit> hits;

    // Constructors
    public EdamamRecipeResponseDto() {}

    // Getters and Setters
    public List<Hit> getHits() { return hits; }
    public void setHits(List<Hit> hits) { this.hits = hits; }

    public static class Hit {
        private Recipe recipe;

        // Constructors
        public Hit() {}

        // Getters and Setters
        public Recipe getRecipe() { return recipe; }
        public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    }

    public static class Recipe {
        private String uri;
        private String label;
        private String image;
        private String source;
        private String url;
        private Integer yield;
        private List<String> dietLabels;
        private List<String> healthLabels;
        private List<String> ingredientLines;
        private Double calories;
        private List<String> cuisineType;
        private List<String> mealType;
        private List<String> dishType;
        private Object totalNutrients;
        private Integer totalTime;
        private List<Object> instructions;

        // Constructors
        public Recipe() {}

        // Getters and Setters
        public String getUri() { return uri; }
        public void setUri(String uri) { this.uri = uri; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public Integer getYield() { return yield; }
        public void setYield(Integer yield) { this.yield = yield; }
        public List<String> getDietLabels() { return dietLabels; }
        public void setDietLabels(List<String> dietLabels) { this.dietLabels = dietLabels; }
        public List<String> getHealthLabels() { return healthLabels; }
        public void setHealthLabels(List<String> healthLabels) { this.healthLabels = healthLabels; }
        public List<String> getIngredientLines() { return ingredientLines; }
        public void setIngredientLines(List<String> ingredientLines) { this.ingredientLines = ingredientLines; }
        public Double getCalories() { return calories; }
        public void setCalories(Double calories) { this.calories = calories; }
        public List<String> getCuisineType() { return cuisineType; }
        public void setCuisineType(List<String> cuisineType) { this.cuisineType = cuisineType; }
        public List<String> getMealType() { return mealType; }
        public void setMealType(List<String> mealType) { this.mealType = mealType; }
        public List<String> getDishType() { return dishType; }
        public void setDishType(List<String> dishType) { this.dishType = dishType; }
        public Object getTotalNutrients() { return totalNutrients; }
        public void setTotalNutrients(Object totalNutrients) { this.totalNutrients = totalNutrients; }
        public Integer getTotalTime() { return totalTime; }
        public void setTotalTime(Integer totalTime) { this.totalTime = totalTime; }
        public List<Object> getInstructions() { return instructions; }
        public void setInstructions(List<Object> instructions) { this.instructions = instructions; }
    }
}