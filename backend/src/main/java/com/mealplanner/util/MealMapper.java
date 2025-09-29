package com.mealplanner.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealplanner.dto.EdamamRecipeResponseDto;
import com.mealplanner.dto.MealDto;
import com.mealplanner.dto.MealPlanResponseDto;
import com.mealplanner.entity.Recipe;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MealMapper {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Convert Recipe entity to MealDto
     */
    public MealDto toMealDto(Recipe recipe) {
        if (recipe == null) return null;

        MealDto dto = new MealDto();
        dto.setId(recipe.getId());
        dto.setExternalId(recipe.getExternalId());
        dto.setSourceApi(recipe.getSource());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setSourceUrl(recipe.getExternalUrl());
        dto.setMealType(recipe.getCategory());
        dto.setCuisineType(recipe.getCuisineType());
        dto.setDietType(recipe.getDietType());
        dto.setDifficultyLevel(recipe.getDifficultyLevel());
        dto.setPrepTimeMinutes(recipe.getPrepTime());
        dto.setCookTimeMinutes(recipe.getCookTime());
        dto.setServings(recipe.getServings());
        dto.setCalories(recipe.getCalories());
        dto.setProteinGrams(recipe.getProtein());
        dto.setFatGrams(recipe.getFat());
        dto.setCarbsGrams(recipe.getCarbs());
        dto.setFiberGrams(recipe.getFiber());
        dto.setSugarGrams(recipe.getSugar());
        dto.setSodiumMilliGrams(recipe.getSodium());

        // Set list fields directly from entity
        dto.setIngredients(recipe.getIngredients());
        dto.setInstructions(recipe.getInstructions());
        
        // Set empty lists for fields not available in current Recipe entity
        dto.setTags(Collections.emptyList());
        dto.setDietLabels(Collections.emptyList());
        dto.setHealthLabels(Collections.emptyList());
        dto.setCuisineTypes(Collections.emptyList());
        dto.setMealTypes(Collections.emptyList());
        dto.setDishTypes(Collections.emptyList());

        return dto;
    }

    /**
     * Convert MealDto to Recipe entity
     */
    public Recipe toRecipeEntity(MealDto dto) {
        if (dto == null) return null;

        Recipe recipe = new Recipe();
        recipe.setId(dto.getId());
        recipe.setExternalId(dto.getExternalId());
        recipe.setSource(dto.getSourceApi());
        recipe.setName(dto.getName());
        recipe.setDescription(dto.getDescription());
        recipe.setImageUrl(dto.getImageUrl());
        recipe.setExternalUrl(dto.getSourceUrl());
        recipe.setCategory(dto.getMealType());
        recipe.setCuisineType(dto.getCuisineType());
        recipe.setDietType(dto.getDietType());
        recipe.setDifficultyLevel(dto.getDifficultyLevel());
        recipe.setPrepTime(dto.getPrepTimeMinutes());
        recipe.setCookTime(dto.getCookTimeMinutes());
        recipe.setServings(dto.getServings());
        recipe.setCalories(dto.getCalories());
        recipe.setProtein(dto.getProteinGrams());
        recipe.setFat(dto.getFatGrams());
        recipe.setCarbs(dto.getCarbsGrams());
        recipe.setFiber(dto.getFiberGrams());
        recipe.setSugar(dto.getSugarGrams());
        recipe.setSodium(dto.getSodiumMilliGrams());

        // Set list fields directly
        recipe.setIngredients(dto.getIngredients());
        recipe.setInstructions(dto.getInstructions());

        return recipe;
    }

    /**
     * Convert to MealPlanResponseDto
     */
    public MealPlanResponseDto toMealPlanResponseDto(LocalDate date, List<com.mealplanner.entity.DailyMealPlan> dailyMealPlans) {
        List<MealDto> meals = dailyMealPlans.stream()
            .map(plan -> {
                // Convert DailyMealPlan to MealDto
                MealDto mealDto = new MealDto();
                mealDto.setId(plan.getId());
                mealDto.setMealType(plan.getMealType());
                mealDto.setName("Meal for " + plan.getMealType());
                // Add other fields as needed
                return mealDto;
            })
            .collect(java.util.stream.Collectors.toList());
        
        return new MealPlanResponseDto(date, meals);
    }

    /**
     * Convert Edamam Recipe to MealDto
     */
    public MealDto toMealDto(EdamamRecipeResponseDto.Recipe edamamRecipe) {
        if (edamamRecipe == null) return null;

        MealDto dto = new MealDto();
        dto.setExternalId(edamamRecipe.getUri());
        dto.setSourceApi("EDAMAM");
        dto.setName(edamamRecipe.getLabel());
        dto.setImageUrl(edamamRecipe.getImage());
        dto.setSourceUrl(edamamRecipe.getUrl());
        dto.setServings(edamamRecipe.getYield());
        dto.setCalories(edamamRecipe.getCalories() != null ? edamamRecipe.getCalories().intValue() : null);
        dto.setCookTimeMinutes(edamamRecipe.getTotalTime());

        // Set meal type from Edamam data
        if (edamamRecipe.getMealType() != null && !edamamRecipe.getMealType().isEmpty()) {
            dto.setMealType(edamamRecipe.getMealType().get(0));
        }

        // Set cuisine type from Edamam data
        if (edamamRecipe.getCuisineType() != null && !edamamRecipe.getCuisineType().isEmpty()) {
            dto.setCuisineType(edamamRecipe.getCuisineType().get(0));
        }

        // Set ingredients
        dto.setIngredients(edamamRecipe.getIngredientLines());

        // Set diet and health labels
        dto.setDietLabels(edamamRecipe.getDietLabels());
        dto.setHealthLabels(edamamRecipe.getHealthLabels());

        // Set cuisine types, meal types, and dish types
        dto.setCuisineTypes(edamamRecipe.getCuisineType());
        dto.setMealTypes(edamamRecipe.getMealType());
        dto.setDishTypes(edamamRecipe.getDishType());

        // Convert total nutrients to JSON string
        if (edamamRecipe.getTotalNutrients() != null) {
            try {
                dto.setNutritionInfo(objectMapper.writeValueAsString(edamamRecipe.getTotalNutrients()));
            } catch (JsonProcessingException e) {
                System.err.println("Error converting nutrients to JSON: " + e.getMessage());
            }
        }

        // Convert instructions to JSON string
        if (edamamRecipe.getInstructions() != null) {
            try {
                dto.setInstructions(objectMapper.readValue(
                    objectMapper.writeValueAsString(edamamRecipe.getInstructions()), List.class));
            } catch (JsonProcessingException e) {
                System.err.println("Error converting instructions to JSON: " + e.getMessage());
            }
        }

        return dto;
    }
}