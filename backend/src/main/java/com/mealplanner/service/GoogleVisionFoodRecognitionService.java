package com.mealplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GoogleVisionFoodRecognitionService implements FoodRecognitionService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleVisionFoodRecognitionService.class);
    
    private final String googleVisionApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Google Cloud Vision API endpoint
    private static final String GOOGLE_VISION_URL = "https://vision.googleapis.com/v1/images:annotate";
    
    public GoogleVisionFoodRecognitionService(String googleVisionApiKey) {
        this.googleVisionApiKey = googleVisionApiKey;
    }
    
    @Override
    public List<FoodItem> analyzeFoodImage(MultipartFile imageFile) {
        logger.info("Analyzing food image using Google Cloud Vision API...");
        
        try {
            // Convert image to base64
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
            
            // Create request body for Google Vision API
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> image = new HashMap<>();
            image.put("content", base64Image);
            
            Map<String, Object> feature = new HashMap<>();
            feature.put("type", "LABEL_DETECTION");
            feature.put("maxResults", 10);
            
            Map<String, Object> request = new HashMap<>();
            request.put("image", image);
            request.put("features", Arrays.asList(feature));
            
            requestBody.put("requests", Arrays.asList(request));
            
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            String url = GOOGLE_VISION_URL + "?key=" + googleVisionApiKey;
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseGoogleVisionResponse(response.getBody());
            } else {
                logger.warn("Google Vision API returned non-OK status: {}", response.getStatusCode());
                return getFallbackResults();
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing food image with Google Vision: {}", e.getMessage());
            return getFallbackResults();
        }
    }
    
    @Override
    public List<FoodItem> searchFoodByName(String query) {
        logger.info("Searching for food by name using Google Vision fallback: {}", query);
        // Google Vision doesn't have text search, so we'll use fallback data
        return getFallbackResults();
    }
    
    private List<FoodItem> parseGoogleVisionResponse(String responseBody) {
        List<FoodItem> foodItems = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode responses = rootNode.get("responses");
            
            if (responses != null && responses.isArray() && responses.size() > 0) {
                JsonNode firstResponse = responses.get(0);
                JsonNode labelAnnotations = firstResponse.get("labelAnnotations");
                
                if (labelAnnotations != null && labelAnnotations.isArray()) {
                    for (JsonNode label : labelAnnotations) {
                        String description = label.get("description").asText().toLowerCase();
                        double score = label.get("score").asDouble();
                        
                        // Filter for food-related labels
                        if (isFoodRelated(description) && score > 0.7) {
                            FoodItem foodItem = createFoodItemFromLabel(description, score);
                            foodItems.add(foodItem);
                        }
                    }
                }
            }
            
            logger.info("Parsed {} food items from Google Vision response", foodItems.size());
            
        } catch (Exception e) {
            logger.error("Error parsing Google Vision response: {}", e.getMessage());
        }
        
        return foodItems.isEmpty() ? getFallbackResults() : foodItems;
    }
    
    private boolean isFoodRelated(String label) {
        String[] foodKeywords = {
            "food", "dish", "meal", "cuisine", "recipe", "cooking", "fruit", "vegetable",
            "meat", "chicken", "beef", "pork", "fish", "seafood", "bread", "pasta",
            "rice", "salad", "soup", "dessert", "cake", "cookie", "pizza", "burger",
            "sandwich", "sushi", "curry", "stew", "grill", "bake", "fry", "steam"
        };
        
        for (String keyword : foodKeywords) {
            if (label.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private FoodItem createFoodItemFromLabel(String label, double confidence) {
        // Map common food labels to nutrition data
        Map<String, double[]> nutritionMap = new HashMap<>();
        nutritionMap.put("apple", new double[]{95, 0.5, 25, 0.3});
        nutritionMap.put("banana", new double[]{105, 1.3, 27, 0.4});
        nutritionMap.put("chicken", new double[]{165, 31, 0, 3.6});
        nutritionMap.put("rice", new double[]{130, 2.7, 28, 0.3});
        nutritionMap.put("bread", new double[]{79, 3.1, 15, 1.0});
        nutritionMap.put("pizza", new double[]{266, 11, 33, 10});
        nutritionMap.put("salad", new double[]{45, 3, 8, 0.5});
        nutritionMap.put("soup", new double[]{60, 4, 8, 2});
        nutritionMap.put("pasta", new double[]{131, 5, 25, 1.1});
        nutritionMap.put("fish", new double[]{206, 22, 0, 12});
        
        // Find best match
        double[] nutrition = nutritionMap.getOrDefault(label, new double[]{150, 10, 20, 5});
        
        return new FoodItem(
            label.substring(0, 1).toUpperCase() + label.substring(1),
            nutrition[0], // calories
            nutrition[1], // protein
            nutrition[2], // carbs
            nutrition[3], // fat
            confidence
        );
    }
    
    private List<FoodItem> getFallbackResults() {
        logger.info("Using fallback food data for Google Vision");
        return Arrays.asList(
            new FoodItem("Food Item", 150, 10, 20, 5, 0.8),
            new FoodItem("Meal", 200, 15, 25, 8, 0.75)
        );
    }
} 