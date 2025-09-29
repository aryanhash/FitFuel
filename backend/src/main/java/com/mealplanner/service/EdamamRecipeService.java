package com.mealplanner.service;

import com.mealplanner.dto.EdamamRecipeResponseDto;
import com.mealplanner.dto.MealDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EdamamRecipeService {
    
    @Value("${edamam.app.id}")
    private String appId;
    
    @Value("${edamam.app.key}")
    private String appKey;
    
    private final RestTemplate restTemplate;
    private final String BASE_URL = "https://api.edamam.com/api/recipes/v2";
    
    public EdamamRecipeService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Search for recipes by meal type and diet preference
     */
    public List<MealDto> searchRecipes(String mealType, String dietType, int maxResults) {
        try {
            String url = buildSearchUrl(mealType, dietType, maxResults);
            
            EdamamRecipeResponseDto response = restTemplate.getForObject(url, EdamamRecipeResponseDto.class);
            
            if (response != null && response.getHits() != null) {
                return convertToMealDtos(response.getHits());
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error fetching recipes from Edamam: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get a random recipe for a specific meal type
     */
    public MealDto getRandomRecipe(String mealType, String dietType) {
        List<MealDto> recipes = searchRecipes(mealType, dietType, 20);
        
        if (!recipes.isEmpty()) {
            Random random = new Random();
            return recipes.get(random.nextInt(recipes.size()));
        }
        
        return null;
    }
    
    /**
     * Build the search URL for Edamam API
     */
    private String buildSearchUrl(String mealType, String dietType, int maxResults) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("type", "public")
                .queryParam("app_id", appId)
                .queryParam("app_key", appKey)
                .queryParam("random", "true")
                .queryParam("to", maxResults);
        
        // Add meal type filter
        if (mealType != null && !mealType.isEmpty()) {
            String mealTypeFilter = getMealTypeFilter(mealType);
            if (mealTypeFilter != null) {
                builder.queryParam("mealType", mealTypeFilter);
            }
        }
        
        // Add diet type filter
        if (dietType != null && !dietType.isEmpty()) {
            String dietFilter = getDietFilter(dietType);
            if (dietFilter != null) {
                builder.queryParam("health", dietFilter);
            }
        }
        
        return builder.toUriString();
    }
    
    /**
     * Convert Edamam meal type to API filter
     */
    private String getMealTypeFilter(String mealType) {
        return switch (mealType.toUpperCase()) {
            case "BREAKFAST" -> "Breakfast";
            case "LUNCH" -> "Lunch";
            case "DINNER" -> "Dinner";
            case "SNACK" -> "Snack";
            default -> null;
        };
    }
    
    /**
     * Convert diet type to Edamam health filter
     */
    private String getDietFilter(String dietType) {
        return switch (dietType.toUpperCase()) {
            case "VEG" -> "vegetarian";
            case "VEGAN" -> "vegan";
            case "KETO" -> "keto-friendly";
            case "PALEO" -> "paleo";
            case "LOW-CARB" -> "low-carb";
            case "LOW-FAT" -> "low-fat";
            case "NON_VEG" -> null; // No specific filter for non-veg
            default -> null;
        };
    }
    
    /**
     * Convert Edamam recipes to our MealDto format
     */
    private List<MealDto> convertToMealDtos(List<EdamamRecipeResponseDto.Hit> hits) {
        List<MealDto> meals = new ArrayList<>();
        
        for (EdamamRecipeResponseDto.Hit hit : hits) {
            EdamamRecipeResponseDto.Recipe recipe = hit.getRecipe();
            MealDto meal = new MealDto();
            
            // Basic info
            meal.setName(recipe.getLabel());
            meal.setDescription(recipe.getSource());
            meal.setImageUrl(recipe.getImage());
            meal.setSourceApi("edamam");
            meal.setExternalId(extractIdFromUri(recipe.getUri()));
            
            // Meal type
            if (recipe.getMealType() != null && !recipe.getMealType().isEmpty()) {
                meal.setMealType(recipe.getMealType().get(0).toUpperCase());
            } else {
                meal.setMealType("GENERAL");
            }
            
            // Nutrition info
            if (recipe.getCalories() != null) {
                meal.setCalories(recipe.getCalories().intValue());
            }
            
            // Note: Nutrition parsing is complex due to dynamic JSON structure
            // For now, we'll rely on the basic calories field
            // TODO: Implement proper nutrition parsing from totalNutrients object
            
            // Cooking time
            if (recipe.getTotalTime() != null) {
                meal.setCookTimeMinutes(recipe.getTotalTime().intValue());
            }
            
            // Servings
            if (recipe.getYield() != null) {
                meal.setServings(recipe.getYield().intValue());
            }
            
            // Ingredients
            if (recipe.getIngredientLines() != null) {
                meal.setIngredients(recipe.getIngredientLines());
            }
            
            // Diet type
            if (recipe.getDietLabels() != null && !recipe.getDietLabels().isEmpty()) {
                meal.setDietType(recipe.getDietLabels().get(0).toUpperCase());
            } else {
                meal.setDietType("MIXED");
            }
            
            // Cuisine type
            if (recipe.getCuisineType() != null && !recipe.getCuisineType().isEmpty()) {
                meal.setCuisineType(recipe.getCuisineType().get(0));
            }
            
            meals.add(meal);
        }
        
        return meals;
    }
    
    /**
     * Extract ID from Edamam URI
     */
    private String extractIdFromUri(String uri) {
        if (uri != null && uri.contains("recipe_")) {
            return uri.substring(uri.lastIndexOf("_") + 1);
        }
        return uri;
    }
}
