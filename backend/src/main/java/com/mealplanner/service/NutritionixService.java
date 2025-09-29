package com.mealplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealplanner.dto.MealDto;
import com.mealplanner.entity.Recipe;
import com.mealplanner.repository.RecipeRepository;
import com.mealplanner.repository.ApiUsageLogRepository;
import com.mealplanner.entity.ApiUsageLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class NutritionixService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private ApiUsageLogRepository apiUsageLogRepository;
    
    @Value("${nutritionix.app.id}")
    private String appId;
    
    @Value("${nutritionix.app.key}")
    private String appKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public NutritionixService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Search for recipes using Nutritionix API
     */
    public List<MealDto> searchRecipes(String query, String mealType, Integer maxCalories, String diet) {
        try {
            // Log API usage
            ApiUsageLog log = new ApiUsageLog();
            log.setApiName("NUTRITIONIX");
            log.setEndpoint("/v2/search/instant");
            Map<String, Object> requestDataMap = new HashMap<>();
            requestDataMap.put("query", query);
            requestDataMap.put("mealType", mealType);
            requestDataMap.put("maxCalories", maxCalories);
            requestDataMap.put("diet", diet);
            // Convert Map to JSON string for JSONB column
            String requestDataJson = objectMapper.writeValueAsString(requestDataMap);
            log.setRequestData(requestDataJson);
            log.setCreatedAt(LocalDateTime.now());
            
            long startTime = System.currentTimeMillis();
            
            // Build request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-app-id", appId);
            headers.set("x-app-key", appKey);
            headers.set("x-remote-user-id", "0"); // Required for free tier
            
            // Build request body - only query is needed for instant search
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            String url = "https://trackapi.nutritionix.com/v2/search/instant";
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class);
            
            // Log response
            log.setResponseStatus(response.getStatusCode().value());
            log.setResponseTimeMs((int) (System.currentTimeMillis() - startTime));
            apiUsageLogRepository.save(log);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return parseNutritionixResponse(response.getBody(), mealType);
            } else {
                System.err.println("Nutritionix API error: " + response.getStatusCode());
                return Collections.emptyList();
            }
            
        } catch (Exception e) {
            System.err.println("Error calling Nutritionix API: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Get detailed nutrition information for a food item
     */
    public MealDto getDetailedNutrition(String foodName, Integer quantity, String unit) {
        try {
            // Log API usage
            ApiUsageLog log = new ApiUsageLog();
            log.setApiName("NUTRITIONIX");
            log.setEndpoint("/v2/natural/nutrients");
            Map<String, Object> requestDataMap = new HashMap<>();
            requestDataMap.put("foodName", foodName);
            requestDataMap.put("quantity", quantity);
            requestDataMap.put("unit", unit);
            // Convert Map to JSON string for JSONB column
            String requestDataJson = objectMapper.writeValueAsString(requestDataMap);
            log.setRequestData(requestDataJson);
            log.setCreatedAt(LocalDateTime.now());
            
            long startTime = System.currentTimeMillis();
            
            // Build request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-app-id", appId);
            headers.set("x-app-key", appKey);
            headers.set("x-remote-user-id", "0");
            
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", quantity + " " + unit + " " + foodName);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            String url = "https://trackapi.nutritionix.com/v2/natural/nutrients";
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class);
            
            // Log response
            log.setResponseStatus(response.getStatusCode().value());
            log.setResponseTimeMs((int) (System.currentTimeMillis() - startTime));
            apiUsageLogRepository.save(log);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return parseNutritionixNutrientsResponse(response.getBody());
            } else {
                System.err.println("Nutritionix API error: " + response.getStatusCode());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Error calling Nutritionix API: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Parse Nutritionix search response
     */
    private List<MealDto> parseNutritionixResponse(String responseBody, String mealType) {
        List<MealDto> meals = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode common = root.get("common");
            JsonNode branded = root.get("branded");
            
            // Process common foods
            if (common != null && common.isArray()) {
                for (JsonNode item : common) {
                    MealDto meal = parseCommonFoodItem(item, mealType);
                    if (meal != null) {
                        meals.add(meal);
                    }
                }
            }
            
            // Process branded foods
            if (branded != null && branded.isArray()) {
                for (JsonNode item : branded) {
                    MealDto meal = parseBrandedFoodItem(item, mealType);
                    if (meal != null) {
                        meals.add(meal);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing Nutritionix response: " + e.getMessage());
            e.printStackTrace();
        }
        
        return meals;
    }
    
    /**
     * Parse common food item
     */
    private MealDto parseCommonFoodItem(JsonNode item, String mealType) {
        try {
            MealDto meal = new MealDto();
            
            meal.setExternalId(item.get("tag_id").asText());
            meal.setSourceApi("NUTRITIONIX");
            meal.setName(item.get("food_name").asText());
            meal.setMealType(mealType);
            
            // Get image URL if available
            if (item.has("photo") && item.get("photo").has("thumb")) {
                meal.setImageUrl(item.get("photo").get("thumb").asText());
            }
            
            return meal;
        } catch (Exception e) {
            System.err.println("Error parsing common food item: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse branded food item
     */
    private MealDto parseBrandedFoodItem(JsonNode item, String mealType) {
        try {
            MealDto meal = new MealDto();
            
            meal.setExternalId(item.get("nix_item_id").asText());
            meal.setSourceApi("NUTRITIONIX");
            meal.setName(item.get("food_name").asText());
            meal.setMealType(mealType);
            
            // Get image URL if available
            if (item.has("photo") && item.get("photo").has("thumb")) {
                meal.setImageUrl(item.get("photo").get("thumb").asText());
            }
            
            // Get calories if available
            if (item.has("nf_calories")) {
                meal.setCalories(item.get("nf_calories").asInt());
            }
            
            return meal;
        } catch (Exception e) {
            System.err.println("Error parsing branded food item: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse Nutritionix nutrients response
     */
    private MealDto parseNutritionixNutrientsResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode foods = root.get("foods");
            
            if (foods != null && foods.isArray() && foods.size() > 0) {
                JsonNode food = foods.get(0);
                MealDto meal = new MealDto();
                
                meal.setExternalId(food.get("nix_item_id").asText());
                meal.setSourceApi("NUTRITIONIX");
                meal.setName(food.get("food_name").asText());
                
                // Set nutrition information
                if (food.has("nf_calories")) {
                    meal.setCalories(food.get("nf_calories").asInt());
                }
                if (food.has("nf_protein")) {
                    meal.setProteinGrams(food.get("nf_protein").asDouble());
                }
                if (food.has("nf_total_fat")) {
                    meal.setFatGrams(food.get("nf_total_fat").asDouble());
                }
                if (food.has("nf_total_carbohydrate")) {
                    meal.setCarbsGrams(food.get("nf_total_carbohydrate").asDouble());
                }
                if (food.has("nf_dietary_fiber")) {
                    meal.setFiberGrams(food.get("nf_dietary_fiber").asDouble());
                }
                if (food.has("nf_sugars")) {
                    meal.setSugarGrams(food.get("nf_sugars").asDouble());
                }
                if (food.has("nf_sodium")) {
                    meal.setSodiumMilliGrams(food.get("nf_sodium").asDouble());
                }
                
                return meal;
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing Nutritionix nutrients response: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cache a meal from Nutritionix to local database
     */
    public Recipe cacheMealFromNutritionix(MealDto mealDto) {
        try {
            // Check if already cached
            Optional<Recipe> existing = recipeRepository.findByExternalIdAndSource(
                mealDto.getExternalId(), "NUTRITIONIX");
            
            if (existing.isPresent()) {
                return existing.get();
            }
            
            // Create new recipe entity
            Recipe recipe = new Recipe();
            recipe.setExternalId(mealDto.getExternalId());
            recipe.setSource("NUTRITIONIX");
            recipe.setName(mealDto.getName());
            recipe.setDescription(mealDto.getDescription());
            recipe.setImageUrl(mealDto.getImageUrl());
            recipe.setCategory(mealDto.getMealType());
            recipe.setCalories(mealDto.getCalories());
            recipe.setProtein(mealDto.getProteinGrams());
            recipe.setFat(mealDto.getFatGrams());
            recipe.setCarbs(mealDto.getCarbsGrams());
            recipe.setFiber(mealDto.getFiberGrams());
            recipe.setSugar(mealDto.getSugarGrams());
            recipe.setSodium(mealDto.getSodiumMilliGrams());
            
            return recipeRepository.save(recipe);
            
        } catch (Exception e) {
            System.err.println("Error caching Nutritionix meal: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
