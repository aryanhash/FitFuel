package com.mealplanner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

@Service
public class YouTubeService {

    private static final Logger logger = LoggerFactory.getLogger(YouTubeService.class);
    private final Random random = new Random();

    @Value("${youtube.api.key}")
    private String apiKey;

    public String searchRecipeVideo(String query) {
        try {
            // Extract the main ingredient or dish type from the query
            String searchTerm = extractSearchTerm(query);
            
            // Try different search strategies for better results
            String embedUrl = trySearchStrategy(searchTerm, "recipe");
            if (embedUrl.isEmpty()) {
                embedUrl = trySearchStrategy(searchTerm, "cooking");
            }
            if (embedUrl.isEmpty()) {
                embedUrl = trySearchStrategy(searchTerm, "how to make");
            }
            
            return embedUrl;
        } catch (Exception e) {
            logger.error("Error searching YouTube for query: {}", query, e);
        }
        return "";
    }

    private String trySearchStrategy(String searchTerm, String suffix) {
        try {
            String fullQuery = searchTerm + " " + suffix;
            
            String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/search")
                    .queryParam("part", "snippet")
                    .queryParam("q", fullQuery)
                    .queryParam("type", "video")
                    .queryParam("maxResults", "5") // Get more results for variety
                    .queryParam("key", apiKey)
                    .build()
                    .toUriString();

            logger.info("YouTube API URL: {}", url);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            logger.info("YouTube API Response: {}", response);

            if (response != null) {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray items = jsonResponse.getJSONArray("items");

                if (items.length() > 0) {
                    // Randomly select from top 3 results for variety
                    int randomIndex = Math.min(random.nextInt(3), items.length() - 1);
                    JSONObject selectedItem = items.getJSONObject(randomIndex);
                    JSONObject id = selectedItem.getJSONObject("id");
                    String videoId = id.getString("videoId");
                    String embedUrl = "https://www.youtube.com/embed/" + videoId;
                    
                    logger.info("Found video ID: {}, Embed URL: {}", videoId, embedUrl);
                    return embedUrl;
                } else {
                    logger.warn("No videos found for query: {}", fullQuery);
                    return "";
                }
            }
        } catch (Exception e) {
            logger.error("Error in search strategy for: {}", searchTerm + " " + suffix, e);
        }
        return "";
    }

    private String extractSearchTerm(String query) {
        // Map specific dish names to more searchable terms
        String lowerQuery = query.toLowerCase();
        
        // Breakfast items
        if (lowerQuery.contains("poha")) return "poha";
        if (lowerQuery.contains("upma")) return "upma";
        if (lowerQuery.contains("daliya")) return "daliya";
        if (lowerQuery.contains("idli")) return "idli sambar";
        if (lowerQuery.contains("paratha")) return "aloo paratha";
        if (lowerQuery.contains("dosa")) return "masala dosa";
        
        // Main dishes
        if (lowerQuery.contains("chicken curry")) return "chicken curry";
        if (lowerQuery.contains("fish curry")) return "fish curry";
        if (lowerQuery.contains("mutton curry")) return "mutton curry";
        if (lowerQuery.contains("butter chicken")) return "butter chicken";
        if (lowerQuery.contains("chicken keema")) return "chicken keema";
        if (lowerQuery.contains("fish fry")) return "fish fry";
        
        // Vegetarian dishes
        if (lowerQuery.contains("paneer")) return "paneer tikka";
        if (lowerQuery.contains("biryani")) return "vegetable biryani";
        if (lowerQuery.contains("rajma")) return "rajma chawal";
        if (lowerQuery.contains("chana masala")) return "chana masala";
        if (lowerQuery.contains("dal khichdi")) return "dal khichdi";
        if (lowerQuery.contains("dal")) return "dal tadka";
        if (lowerQuery.contains("khichdi")) return "khichdi";
        if (lowerQuery.contains("kadhi")) return "kadhi chawal";
        if (lowerQuery.contains("mixed vegetable")) return "mixed vegetable curry";
        
        // Generic fallbacks
        if (lowerQuery.contains("curry")) return "curry";
        if (lowerQuery.contains("vegetable")) return "vegetable curry";
        
        // If no specific match, use the original query
        return query;
    }
} 