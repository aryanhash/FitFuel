package com.mealplanner.controller;

import com.mealplanner.service.FoodRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/food")
@CrossOrigin(origins = "*")
public class FoodRecognitionController {

    private static final Logger logger = LoggerFactory.getLogger(FoodRecognitionController.class);

    @Autowired
    private FoodRecognitionService foodRecognitionService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Food Recognition API is working!");
    }

    @PostMapping("/analyze")
    public ResponseEntity<List<FoodRecognitionService.FoodItem>> analyzeFoodImage(
            @RequestParam("image") MultipartFile imageFile) {
        
        logger.info("Received food image analysis request");
        
        try {
            List<FoodRecognitionService.FoodItem> results = foodRecognitionService.analyzeFoodImage(imageFile);
            logger.info("Food analysis completed successfully");
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error analyzing food image: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodRecognitionService.FoodItem>> searchFoodByName(
            @RequestParam("query") String query) {
        
        logger.info("Received food search request for query: {}", query);
        
        try {
            List<FoodRecognitionService.FoodItem> results = foodRecognitionService.searchFoodByName(query);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error searching for food: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
} 