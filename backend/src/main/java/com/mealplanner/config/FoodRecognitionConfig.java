package com.mealplanner.config;

import com.mealplanner.service.FoodRecognitionService;
import com.mealplanner.service.NutritionixFoodRecognitionService;
import com.mealplanner.service.GoogleVisionFoodRecognitionService;
import com.mealplanner.service.ClarifaiFoodRecognitionService;
import com.mealplanner.service.EnhancedFoodRecognitionService;
import com.mealplanner.service.Gpt4oVisionFoodRecognitionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class FoodRecognitionConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FoodRecognitionConfig.class);
    
    @Value("${nutritionix.app.id:}")
    private String nutritionixAppId;
    
    @Value("${nutritionix.app.key:}")
    private String nutritionixAppKey;
    
    @Value("${google.vision.api.key:}")
    private String googleVisionApiKey;
    
    @Value("${clarifai.api.key:YOUR_CLARIFAI_API_KEY}")
    private String clarifaiApiKey;
    
    @Bean
    @Primary
    @ConditionalOnProperty(name = "food.recognition.api", havingValue = "gpt4o")
    public FoodRecognitionService gpt4oVisionFoodRecognitionService() {
        logger.info("Creating GPT-4o Vision food recognition service");
        return new Gpt4oVisionFoodRecognitionService();
    }
    
    @Bean
    @ConditionalOnProperty(name = "food.recognition.api", havingValue = "enhanced", matchIfMissing = true)
    public FoodRecognitionService enhancedFoodRecognitionService() {
        logger.info("Creating Enhanced food recognition service with Indian food database");
        return new EnhancedFoodRecognitionService(clarifaiApiKey, nutritionixAppId, nutritionixAppKey);
    }
    
    @Bean
    @ConditionalOnProperty(name = "food.recognition.api", havingValue = "clarifai")
    public FoodRecognitionService clarifaiFoodRecognitionService() {
        logger.info("Creating Clarifai food recognition service");
        return new ClarifaiFoodRecognitionService(clarifaiApiKey, nutritionixAppId, nutritionixAppKey);
    }
    
    @Bean
    @ConditionalOnProperty(name = "food.recognition.api", havingValue = "nutritionix")
    public FoodRecognitionService nutritionixFoodRecognitionService() {
        logger.info("Creating Nutritionix food recognition service");
        return new NutritionixFoodRecognitionService(nutritionixAppId, nutritionixAppKey);
    }
    
    @Bean
    @ConditionalOnProperty(name = "food.recognition.api", havingValue = "google-vision")
    public FoodRecognitionService googleVisionFoodRecognitionService() {
        logger.info("Creating Google Vision food recognition service");
        return new GoogleVisionFoodRecognitionService(googleVisionApiKey);
    }
} 