package com.mealplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

public class Gpt4oVisionFoodRecognitionService implements FoodRecognitionService {

    private static final Logger logger = LoggerFactory.getLogger(Gpt4oVisionFoodRecognitionService.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o";

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String openAiApiKey;

    public Gpt4oVisionFoodRecognitionService(String apiKey) {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.openAiApiKey = apiKey;

        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            logger.warn("GPT4O_API_KEY not set! Food recognition will use fallback data.");
        } else {
            logger.info("GPT4O_API_KEY successfully loaded (length: {})", openAiApiKey.length());
        }
    }

    @Override
    public List<FoodItem> analyzeFoodImage(MultipartFile imageFile) {
        logger.info("Analyzing food image using GPT-4o Vision...");

        try {
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String prompt = "You are a nutrition expert specializing in Indian cuisine. Analyze the food in this image and identify the specific Indian dish. " +
                    "If it's an Indian food, be very specific (e.g., 'Aloo Paratha', 'Masala Dosa', 'Butter Chicken', 'Dal Tadka'). " +
                    "If it's not Indian food, identify it accurately. " +
                    "Provide a JSON object with these exact fields: " +
                    "{\"name\": \"exact food name\", \"estimated_calories\": number, \"protein_g\": number, \"carbs_g\": number, \"fat_g\": number, \"description\": \"brief description\"}. " +
                    "Respond ONLY with the JSON object, no other text.";

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", MODEL);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.1);

            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode message = messages.addObject();
            message.put("role", "user");

            ArrayNode content = message.putArray("content");

            ObjectNode textContent = content.addObject();
            textContent.put("type", "text");
            textContent.put("text", prompt);

            ObjectNode imageContent = content.addObject();
            imageContent.put("type", "image_url");
            ObjectNode imageUrl = imageContent.putObject("image_url");
            imageUrl.put("url", "data:image/jpeg;base64," + base64Image);

            Request request = new Request.Builder()
                    .url(OPENAI_API_URL)
                    .addHeader("Authorization", "Bearer " + openAiApiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    logger.error("OpenAI API error: {} - {}", response.code(), errorBody);
                    return getFallbackResults();
                }

                String responseBody = response.body().string();
                logger.debug("OpenAI response: {}", responseBody);

                JsonNode root = objectMapper.readTree(responseBody);
                String contentText = root.path("choices").get(0).path("message").path("content").asText();

                int start = contentText.indexOf("{");
                int end = contentText.lastIndexOf("}");
                if (start == -1 || end == -1) {
                    logger.error("No JSON object found in GPT response: {}", contentText);
                    return getFallbackResults();
                }

                String json = contentText.substring(start, end + 1);
                logger.info("Extracted JSON from GPT response: {}", json);

                JsonNode foodNode = objectMapper.readTree(json);

                FoodItem item = new FoodItem(
                        foodNode.path("name").asText(),
                        foodNode.path("estimated_calories").asInt(),
                        foodNode.path("protein_g").asDouble(),
                        foodNode.path("carbs_g").asDouble(),
                        foodNode.path("fat_g").asDouble(),
                        0.95
                );

                logger.info("Successfully analyzed food: {} ({} calories)", item.getName(), item.getCalories());
                return Collections.singletonList(item);

            }
        } catch (IOException e) {
            logger.error("Error calling OpenAI API", e);
            return getFallbackResults();
        } catch (Exception e) {
            logger.error("Error processing GPT-4o Vision response", e);
            return getFallbackResults();
        }
    }

    @Override
    public List<FoodItem> searchFoodByName(String query) {
        logger.info("Searching for food by name: {}", query);
        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("aloo") && lowerQuery.contains("paratha")) {
            return Collections.singletonList(new FoodItem("Aloo Paratha", 250, 6.0, 35, 8.0, 0.95));
        } else if (lowerQuery.contains("dosa")) {
            return Collections.singletonList(new FoodItem("Masala Dosa", 180, 4.5, 28, 5.0, 0.95));
        } else if (lowerQuery.contains("biryani")) {
            return Collections.singletonList(new FoodItem("Biryani", 350, 12.0, 45, 12.0, 0.95));
        } else if (lowerQuery.contains("curry")) {
            return Collections.singletonList(new FoodItem("Curry", 200, 10.0, 15, 10.0, 0.95));
        }

        return getFallbackResults();
    }

    private List<FoodItem> getFallbackResults() {
        logger.info("Using fallback food data");
        return Arrays.asList(
                new FoodItem("Aloo Paratha", 250, 6.0, 35, 8.0, 0.9),
                new FoodItem("Masala Dosa", 180, 4.5, 28, 5.0, 0.9),
                new FoodItem("Butter Chicken", 350, 25.0, 8.0, 22.0, 0.9),
                new FoodItem("Dal Tadka", 150, 8.0, 22, 4.0, 0.9),
                new FoodItem("Biryani", 400, 15.0, 50, 15.0, 0.9)
        );
    }
}
