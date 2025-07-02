package com.mealplanner.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class EnvConfig {

    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);

    @PostConstruct
    public void loadEnv() {
        logger.info("Loading environment variables from .env file...");
        
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".") // Look in current directory
                    .ignoreIfMissing()
                    .load();
            
            logger.info("Dotenv loaded successfully. Found {} entries", dotenv.entries().size());
            
            // Load environment variables into System properties
            int loadedCount = 0;
            for (var entry : dotenv.entries()) {
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                    logger.debug("Loaded environment variable: {} = {}", entry.getKey(), 
                        entry.getKey().contains("KEY") || entry.getKey().contains("SECRET") || entry.getKey().contains("PASSWORD") 
                        ? "***HIDDEN***" : entry.getValue());
                    loadedCount++;
                }
            }
            
            logger.info("Loaded {} environment variables into system properties", loadedCount);
            
            // Log specific API keys to verify they're loaded
            logger.info("GPT4O_API_KEY loaded: {}", 
                System.getProperty("GPT4O_API_KEY") != null ? "YES" : "NO");
            logger.info("CLARIFAI_API_KEY loaded: {}", 
                System.getProperty("CLARIFAI_API_KEY") != null ? "YES" : "NO");
            logger.info("NUTRITIONIX_APP_ID loaded: {}", 
                System.getProperty("NUTRITIONIX_APP_ID") != null ? "YES" : "NO");
                
        } catch (Exception e) {
            logger.error("Error loading .env file", e);
        }
    }
} 