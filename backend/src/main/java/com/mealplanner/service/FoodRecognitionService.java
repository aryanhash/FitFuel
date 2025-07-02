package com.mealplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public interface FoodRecognitionService {
    
    List<FoodItem> analyzeFoodImage(MultipartFile imageFile);
    List<FoodItem> searchFoodByName(String query);
    
    class FoodItem {
        private String name;
        private double calories;
        private double protein;
        private double carbs;
        private double fat;
        private double confidence;
        
        public FoodItem(String name, double calories, double protein, double carbs, double fat, double confidence) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.confidence = confidence;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public double getCalories() { return calories; }
        public void setCalories(double calories) { this.calories = calories; }
        
        public double getProtein() { return protein; }
        public void setProtein(double protein) { this.protein = protein; }
        
        public double getCarbs() { return carbs; }
        public void setCarbs(double carbs) { this.carbs = carbs; }
        
        public double getFat() { return fat; }
        public void setFat(double fat) { this.fat = fat; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
} 