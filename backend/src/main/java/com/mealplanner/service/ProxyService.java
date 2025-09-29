package com.mealplanner.service;

import com.mealplanner.entity.User;
import com.mealplanner.repository.ApiUsageLogRepository;
import com.mealplanner.entity.ApiUsageLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ProxyService {
    
    @Autowired
    private ApiUsageLogRepository apiUsageLogRepository;
    
    private final RestTemplate restTemplate;
    
    public ProxyService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Securely call external APIs with logging and rate limiting
     */
    public ResponseEntity<String> makeSecureApiCall(
            String apiName,
            String endpoint,
            User user,
            Map<String, Object> requestData,
            HttpMethod method) {
        
        long startTime = System.currentTimeMillis();
        ApiUsageLog log = new ApiUsageLog();
        log.setApiName(apiName);
        log.setEndpoint(endpoint);
        log.setUser(user);
               log.setRequestData(requestData.toString());
        log.setCreatedAt(LocalDateTime.now());
        
        try {
            // Check rate limits before making the call
            if (!isWithinRateLimit(apiName, user)) {
                log.setResponseStatus(429);
                log.setResponseTimeMs((int) (System.currentTimeMillis() - startTime));
                apiUsageLogRepository.save(log);
                
                return ResponseEntity.status(429)
                    .body("{\"error\": \"Rate limit exceeded for " + apiName + "\"}");
            }
            
            // Make the actual API call
            ResponseEntity<String> response = makeApiCall(apiName, endpoint, requestData, method);
            
            // Log successful response
            log.setResponseStatus(response.getStatusCode().value());
            log.setResponseTimeMs((int) (System.currentTimeMillis() - startTime));
            apiUsageLogRepository.save(log);
            
            return response;
            
        } catch (Exception e) {
            // Log error
            log.setResponseStatus(500);
            log.setResponseTimeMs((int) (System.currentTimeMillis() - startTime));
            apiUsageLogRepository.save(log);
            
            return ResponseEntity.status(500)
                .body("{\"error\": \"External API call failed: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Check if user is within rate limits for the API
     */
    private boolean isWithinRateLimit(String apiName, User user) {
        // Get usage count for the last hour
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long usageCount = apiUsageLogRepository.countByApiNameAndUserAndCreatedAtAfter(
            apiName, user, oneHourAgo);
        
        // Define rate limits per API
        int rateLimit = getRateLimitForApi(apiName);
        
        return usageCount < rateLimit;
    }
    
    /**
     * Get rate limit for specific API
     */
    private int getRateLimitForApi(String apiName) {
        return switch (apiName.toLowerCase()) {
            case "edamam" -> 100; // 100 calls per hour
            case "spoonacular" -> 50; // 50 calls per hour
            case "openai" -> 30; // 30 calls per hour
            default -> 10; // Default limit
        };
    }
    
    /**
     * Make the actual API call based on the API name
     */
    private ResponseEntity<String> makeApiCall(
            String apiName,
            String endpoint,
            Map<String, Object> requestData,
            HttpMethod method) {
        
        HttpHeaders headers = createHeadersForApi(apiName);
        
        switch (apiName.toLowerCase()) {
            case "edamam":
                return callEdamamApi(endpoint, requestData, headers, method);
            case "spoonacular":
                return callSpoonacularApi(endpoint, requestData, headers, method);
            case "openai":
                return callOpenAIApi(endpoint, requestData, headers, method);
            default:
                throw new IllegalArgumentException("Unsupported API: " + apiName);
        }
    }
    
    /**
     * Create headers specific to each API
     */
    private HttpHeaders createHeadersForApi(String apiName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        switch (apiName.toLowerCase()) {
            case "edamam":
                // Edamam uses app_id and app_key as query parameters
                break;
            case "spoonacular":
                headers.set("X-RapidAPI-Key", getApiKey("spoonacular"));
                headers.set("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
                break;
            case "openai":
                headers.set("Authorization", "Bearer " + getApiKey("openai"));
                break;
        }
        
        return headers;
    }
    
    /**
     * Call Edamam API
     */
    private ResponseEntity<String> callEdamamApi(
            String endpoint,
            Map<String, Object> requestData,
            HttpHeaders headers,
            HttpMethod method) {
        
        // Add Edamam credentials to request data
        requestData.put("app_id", getApiKey("edamam_app_id"));
        requestData.put("app_key", getApiKey("edamam_app_key"));
        
        String url = "https://api.edamam.com/api/recipes/v2" + endpoint;
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
        return restTemplate.exchange(url, method, entity, String.class);
    }
    
    /**
     * Call Spoonacular API
     */
    private ResponseEntity<String> callSpoonacularApi(
            String endpoint,
            Map<String, Object> requestData,
            HttpHeaders headers,
            HttpMethod method) {
        
        String url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com" + endpoint;
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
        return restTemplate.exchange(url, method, entity, String.class);
    }
    
    /**
     * Call OpenAI API
     */
    private ResponseEntity<String> callOpenAIApi(
            String endpoint,
            Map<String, Object> requestData,
            HttpHeaders headers,
            HttpMethod method) {
        
        String url = "https://api.openai.com/v1" + endpoint;
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
        return restTemplate.exchange(url, method, entity, String.class);
    }
    
    /**
     * Get API key from configuration (in real implementation, use @Value)
     */
    private String getApiKey(String keyName) {
        // In real implementation, these would come from application.properties
        return switch (keyName) {
            case "edamam_app_id" -> System.getProperty("edamam.app.id", "your-edamam-app-id");
            case "edamam_app_key" -> System.getProperty("edamam.app.key", "your-edamam-app-key");
            case "spoonacular" -> System.getProperty("spoonacular.api.key", "your-spoonacular-key");
            case "openai" -> System.getProperty("openai.api.key", "your-openai-key");
            default -> "default-key";
        };
    }
}
