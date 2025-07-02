package com.mealplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.Base64;

public class EnhancedFoodRecognitionService implements FoodRecognitionService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedFoodRecognitionService.class);
    
    private final String clarifaiApiKey;
    private final String nutritionixAppId;
    private final String nutritionixAppKey;
    
    private final OkHttpClient httpClient = new OkHttpClient();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Clarifai API configuration
    private static final String CLARIFAI_API_URL = "https://api.clarifai.com/v2/models/food-item-recognition/outputs";
    private static final String USER_ID = "clarifai";
    private static final String APP_ID = "main";
    
    // Nutritionix API endpoints
    private static final String NUTRITIONIX_SEARCH_URL = "https://trackapi.nutritionix.com/v2/search/instant";
    
    // Indian food database with nutrition data
    private static final Map<String, FoodItem> INDIAN_FOOD_DATABASE = createIndianFoodDatabase();
    
    public EnhancedFoodRecognitionService(String clarifaiApiKey, String nutritionixAppId, String nutritionixAppKey) {
        this.clarifaiApiKey = clarifaiApiKey;
        this.nutritionixAppId = nutritionixAppId;
        this.nutritionixAppKey = nutritionixAppKey;
    }
    
    @Override
    public List<FoodItem> analyzeFoodImage(MultipartFile imageFile) {
        logger.info("Analyzing food image using enhanced recognition service...");
        
        try {
            // Convert image to base64
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // First, try Clarifai for image recognition
            List<String> detectedFoods = callClarifaiFoodModel(base64Image);
            
            List<FoodItem> foodItems = new ArrayList<>();
            
            if (!detectedFoods.isEmpty()) {
                // Process detected foods
                for (String foodName : detectedFoods) {
                    FoodItem item = findFoodItem(foodName);
                    if (item != null) {
                        foodItems.add(item);
                    }
                }
            }
            
            // If no foods detected or only generic foods, suggest common Indian foods
            if (foodItems.isEmpty() || isGenericFood(foodItems)) {
                logger.info("No specific foods detected, suggesting common Indian foods");
                foodItems.addAll(getCommonIndianFoods());
            }
            
            logger.info("Food analysis completed. Found {} items.", foodItems.size());
            return foodItems;
            
        } catch (Exception e) {
            logger.error("Error analyzing food image: {}", e.getMessage());
            return getCommonIndianFoods();
        }
    }
    
    @Override
    public List<FoodItem> searchFoodByName(String query) {
        logger.info("Searching for food: {}", query);
        
        // First, check our Indian food database
        List<FoodItem> indianFoods = searchIndianFoodDatabase(query);
        if (!indianFoods.isEmpty()) {
            logger.info("Found {} Indian foods for query: {}", indianFoods.size(), query);
            return indianFoods;
        }
        
        // Then try Nutritionix for other foods
        try {
            return searchNutritionix(query);
        } catch (Exception e) {
            logger.error("Error searching Nutritionix: {}", e.getMessage());
            return indianFoods.isEmpty() ? getCommonIndianFoods() : indianFoods;
        }
    }
    
    private List<FoodItem> searchIndianFoodDatabase(String query) {
        List<FoodItem> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (Map.Entry<String, FoodItem> entry : INDIAN_FOOD_DATABASE.entrySet()) {
            String foodName = entry.getKey().toLowerCase();
            if (foodName.contains(lowerQuery) || lowerQuery.contains(foodName)) {
                results.add(entry.getValue());
            }
        }
        
        return results;
    }
    
    private List<FoodItem> searchNutritionix(String query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("x-app-id", nutritionixAppId);
            headers.set("x-app-key", nutritionixAppKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            requestBody.put("detailed", true);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                NUTRITIONIX_SEARCH_URL,
                HttpMethod.POST,
                request,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseNutritionixResponse(response.getBody());
            }
            
        } catch (Exception e) {
            logger.error("Error searching Nutritionix: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    private List<String> callClarifaiFoodModel(String base64Image) {
        List<String> detectedFoods = new ArrayList<>();
        
        try {
            String requestBody = String.format(
                "{\"user_app_id\":{\"user_id\":\"%s\",\"app_id\":\"%s\"},\"inputs\":[{\"data\":{\"image\":{\"base64\":\"%s\"}}}]}",
                USER_ID, APP_ID, base64Image
            );
            
            RequestBody body = RequestBody.create(
                okhttp3.MediaType.parse("application/json"),
                requestBody
            );
            
            Request request = new Request.Builder()
                .url(CLARIFAI_API_URL)
                .post(body)
                .addHeader("Authorization", "Key " + clarifaiApiKey)
                .addHeader("Content-Type", "application/json")
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    detectedFoods = parseClarifaiResponse(responseBody);
                } else {
                    logger.error("Clarifai API error: {} - {}", response.code(), response.message());
                }
            }
            
        } catch (IOException e) {
            logger.error("Error calling Clarifai API: {}", e.getMessage());
        }
        
        return detectedFoods;
    }
    
    private List<String> parseClarifaiResponse(String responseBody) {
        List<String> detectedFoods = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            JsonNode status = rootNode.get("status");
            if (status != null && status.has("code")) {
                int code = status.get("code").asInt();
                if (code != 10000) {
                    logger.error("Clarifai API returned error code: {}", code);
                    return detectedFoods;
                }
            }
            
            JsonNode outputs = rootNode.get("outputs");
            
            if (outputs != null && outputs.isArray() && outputs.size() > 0) {
                JsonNode output = outputs.get(0);
                JsonNode data = output.get("data");
                
                if (data != null && data.has("concepts")) {
                    JsonNode concepts = data.get("concepts");
                    
                    for (JsonNode concept : concepts) {
                        String name = concept.get("name").asText();
                        double value = concept.get("value").asDouble();
                        
                        if (value > 0.5) {
                            detectedFoods.add(name);
                            logger.info("Detected food: {} (confidence: {})", name, value);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing Clarifai response: {}", e.getMessage());
        }
        
        return detectedFoods;
    }
    
    private FoodItem findFoodItem(String foodName) {
        // First check Indian food database
        FoodItem indianFood = INDIAN_FOOD_DATABASE.get(foodName.toLowerCase());
        if (indianFood != null) {
            return indianFood;
        }
        
        // Then try Nutritionix
        List<FoodItem> nutritionixResults = searchNutritionix(foodName);
        if (!nutritionixResults.isEmpty()) {
            return nutritionixResults.get(0);
        }
        
        return null;
    }
    
    private boolean isGenericFood(List<FoodItem> foods) {
        Set<String> genericTerms = Set.of("food", "meal", "dish", "plate", "bowl", "container");
        return foods.stream().anyMatch(food -> 
            genericTerms.contains(food.getName().toLowerCase())
        );
    }
    
    private List<FoodItem> getCommonIndianFoods() {
        return Arrays.asList(
            new FoodItem("Dosa", 120, 3.5, 20, 2.5, 0.9),
            new FoodItem("Idli", 80, 3.0, 15, 0.5, 0.9),
            new FoodItem("Paratha", 180, 4.0, 25, 7.0, 0.9),
            new FoodItem("Chapati", 70, 2.5, 12, 1.0, 0.9),
            new FoodItem("Rice", 130, 2.7, 28, 0.3, 0.9),
            new FoodItem("Dal", 100, 6.0, 18, 0.5, 0.9),
            new FoodItem("Curry", 150, 8.0, 12, 8.0, 0.9),
            new FoodItem("Biryani", 350, 12.0, 45, 12.0, 0.9),
            new FoodItem("Samosa", 250, 5.0, 30, 12.0, 0.9),
            new FoodItem("Pakora", 200, 4.0, 25, 8.0, 0.9)
        );
    }
    
    private List<FoodItem> parseNutritionixResponse(String responseBody) {
        List<FoodItem> foodItems = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode brandedNode = rootNode.get("branded");
            JsonNode commonNode = rootNode.get("common");
            
            if (brandedNode != null && brandedNode.isArray()) {
                for (JsonNode item : brandedNode) {
                    FoodItem foodItem = parseFoodItem(item, true);
                    if (foodItem != null) {
                        foodItems.add(foodItem);
                    }
                }
            }
            
            if (commonNode != null && commonNode.isArray()) {
                for (JsonNode item : commonNode) {
                    FoodItem foodItem = parseFoodItem(item, false);
                    if (foodItem != null) {
                        foodItems.add(foodItem);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing Nutritionix response: {}", e.getMessage());
        }
        
        return foodItems;
    }
    
    private FoodItem parseFoodItem(JsonNode item, boolean isBranded) {
        try {
            String name = item.get("food_name").asText();
            String brandName = isBranded ? item.get("brand_name").asText() : "";
            String fullName = isBranded ? brandName + " " + name : name;
            
            double calories = 0;
            double protein = 0;
            double carbs = 0;
            double fat = 0;
            
            if (item.has("full_nutrients")) {
                JsonNode nutrients = item.get("full_nutrients");
                for (JsonNode nutrient : nutrients) {
                    int attrId = nutrient.get("attr_id").asInt();
                    double value = nutrient.get("value").asDouble();
                    
                    switch (attrId) {
                        case 208: calories = value; break;
                        case 203: protein = value; break;
                        case 205: carbs = value; break;
                        case 204: fat = value; break;
                    }
                }
            }
            
            return new FoodItem(fullName, calories, protein, carbs, fat, 0.95);
            
        } catch (Exception e) {
            logger.error("Error parsing food item: {}", e.getMessage());
            return null;
        }
    }
    
    private static Map<String, FoodItem> createIndianFoodDatabase() {
        Map<String, FoodItem> database = new HashMap<>();
        
        // South Indian Foods
        database.put("dosa", new FoodItem("Dosa", 120, 3.5, 20, 2.5, 0.95));
        database.put("idli", new FoodItem("Idli", 80, 3.0, 15, 0.5, 0.95));
        database.put("vada", new FoodItem("Vada", 150, 4.0, 18, 6.0, 0.95));
        database.put("upma", new FoodItem("Upma", 180, 4.5, 25, 6.0, 0.95));
        database.put("pongal", new FoodItem("Pongal", 200, 6.0, 30, 5.0, 0.95));
        database.put("sambar", new FoodItem("Sambar", 100, 5.0, 15, 3.0, 0.95));
        database.put("rasam", new FoodItem("Rasam", 80, 3.0, 12, 2.0, 0.95));
        
        // North Indian Foods
        database.put("paratha", new FoodItem("Paratha", 180, 4.0, 25, 7.0, 0.95));
        database.put("chapati", new FoodItem("Chapati", 70, 2.5, 12, 1.0, 0.95));
        database.put("roti", new FoodItem("Roti", 70, 2.5, 12, 1.0, 0.95));
        database.put("naan", new FoodItem("Naan", 150, 4.0, 25, 3.0, 0.95));
        database.put("biryani", new FoodItem("Biryani", 350, 12.0, 45, 12.0, 0.95));
        database.put("pulao", new FoodItem("Pulao", 250, 6.0, 40, 8.0, 0.95));
        database.put("dal", new FoodItem("Dal", 100, 6.0, 18, 0.5, 0.95));
        database.put("curry", new FoodItem("Curry", 150, 8.0, 12, 8.0, 0.95));
        
        // Snacks
        database.put("samosa", new FoodItem("Samosa", 250, 5.0, 30, 12.0, 0.95));
        database.put("pakora", new FoodItem("Pakora", 200, 4.0, 25, 8.0, 0.95));
        database.put("vada pav", new FoodItem("Vada Pav", 300, 8.0, 35, 12.0, 0.95));
        database.put("pav bhaji", new FoodItem("Pav Bhaji", 280, 6.0, 30, 10.0, 0.95));
        database.put("bhel puri", new FoodItem("Bhel Puri", 180, 4.0, 25, 6.0, 0.95));
        database.put("pani puri", new FoodItem("Pani Puri", 120, 3.0, 18, 4.0, 0.95));
        
        // Sweets
        database.put("gulab jamun", new FoodItem("Gulab Jamun", 200, 2.0, 35, 5.0, 0.95));
        database.put("rasgulla", new FoodItem("Rasgulla", 180, 2.5, 32, 4.0, 0.95));
        database.put("jalebi", new FoodItem("Jalebi", 250, 2.0, 45, 6.0, 0.95));
        database.put("laddu", new FoodItem("Laddu", 220, 3.0, 38, 5.0, 0.95));
        
        // Drinks
        database.put("lassi", new FoodItem("Lassi", 150, 4.0, 20, 5.0, 0.95));
        database.put("chai", new FoodItem("Chai", 80, 2.0, 12, 2.0, 0.95));
        database.put("masala chai", new FoodItem("Masala Chai", 90, 2.0, 13, 2.5, 0.95));
        
        return database;
    }
} 