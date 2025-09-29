package com.mealplanner.service;

import com.mealplanner.dto.MealDto;
import com.mealplanner.dto.UserPreferencesDto;
import com.mealplanner.entity.User;
import com.mealplanner.entity.AiConversation;
import com.mealplanner.entity.UserPreferences;
import com.mealplanner.repository.AiConversationRepository;
import com.mealplanner.repository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AIService {
    
    @Autowired
    private ProxyService proxyService;
    
    @Autowired
    private AiConversationRepository aiConversationRepository;
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    @Autowired
    private EnhancedMealService mealService;
    
    @Autowired
    private EnhancedUserService userService;
    
    /**
     * Generate personalized meal plan for user
     */
    public List<MealDto> generatePersonalizedMealPlan(User user, LocalDate date, String mealType) {
        try {
            // Get user preferences and history for context
            UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElse(new UserPreferences());
            
            Map<String, Object> userInsights = userService.getUserDietaryInsights(user.getId());
            
            // Create prompt for AI
            String prompt = buildMealGenerationPrompt(preferences, userInsights, mealType, date);
            
            // Call OpenAI API through proxy service
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model", "gpt-3.5-turbo");
            requestData.put("messages", Arrays.asList(
                Map.of("role", "system", "content", "You are a nutritionist AI that helps users plan healthy meals."),
                Map.of("role", "user", "content", prompt)
            ));
            requestData.put("max_tokens", 500);
            requestData.put("temperature", 0.7);
            
            // This would make the actual API call through ProxyService
            // For now, we'll use a fallback approach
            return generateFallbackMealPlan(user, mealType, preferences);
            
        } catch (Exception e) {
            System.err.println("AI meal generation failed: " + e.getMessage());
            return generateFallbackMealPlan(user, mealType, null);
        }
    }
    
    /**
     * Handle AI chatbot conversation
     */
    public String handleChatbotQuery(User user, String message, String sessionId) {
        try {
            // Log user message
            logConversation(user, sessionId, "USER", message);
            
            // Get conversation context
            List<AiConversation> conversationHistory = aiConversationRepository
                .findByUserAndSessionIdOrderByCreatedAtAsc(user, sessionId);
            
            // Get user preferences for context
            UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElse(new UserPreferences());
            
            // Build context-aware prompt
            String prompt = buildChatbotPrompt(message, conversationHistory, preferences);
            
            // Call OpenAI API through proxy service
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model", "gpt-3.5-turbo");
            requestData.put("messages", Arrays.asList(
                Map.of("role", "system", "content", "You are a helpful nutrition and meal planning assistant."),
                Map.of("role", "user", "content", prompt)
            ));
            requestData.put("max_tokens", 300);
            requestData.put("temperature", 0.7);
            
            // This would make the actual API call through ProxyService
            // For now, we'll use a fallback response
            String response = generateFallbackChatbotResponse(message, preferences);
            
            // Log AI response
            logConversation(user, sessionId, "AI", response);
            
            return response;
            
        } catch (Exception e) {
            System.err.println("AI chatbot failed: " + e.getMessage());
            return "I'm sorry, I'm having trouble processing your request right now. Please try again later.";
        }
    }
    
    /**
     * Get meal recommendations based on user history and preferences
     */
    public List<MealDto> getPersonalizedRecommendations(User user, String mealType, int limit) {
        try {
            // Get user insights
            Map<String, Object> userInsights = userService.getUserDietaryInsights(user.getId());
            
            // Get user's meal history for analysis
            List<MealDto> recentMeals = getRecentUserMeals(user, 30); // Last 30 days
            
            // Create recommendation prompt
            String prompt = buildRecommendationPrompt(userInsights, recentMeals, mealType, limit);
            
            // Call OpenAI API through proxy service
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model", "gpt-3.5-turbo");
            requestData.put("messages", Arrays.asList(
                Map.of("role", "system", "content", "You are a meal recommendation AI that suggests personalized recipes."),
                Map.of("role", "user", "content", prompt)
            ));
            requestData.put("max_tokens", 400);
            requestData.put("temperature", 0.8);
            
            // This would make the actual API call through ProxyService
            // For now, we'll use a fallback approach
            return generateFallbackRecommendations(user, mealType, limit);
            
        } catch (Exception e) {
            System.err.println("AI recommendations failed: " + e.getMessage());
            return generateFallbackRecommendations(user, mealType, limit);
        }
    }
    
    /**
     * Analyze user's eating patterns and provide insights
     */
    public Map<String, Object> analyzeEatingPatterns(User user) {
        try {
            // Get user's meal history
            List<MealDto> recentMeals = getRecentUserMeals(user, 30);
            Map<String, Object> userInsights = userService.getUserDietaryInsights(user.getId());
            
            // Create analysis prompt
            String prompt = buildAnalysisPrompt(recentMeals, userInsights);
            
            // Call OpenAI API through proxy service
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model", "gpt-3.5-turbo");
            requestData.put("messages", Arrays.asList(
                Map.of("role", "system", "content", "You are a nutritionist AI that analyzes eating patterns and provides insights."),
                Map.of("role", "user", "content", prompt)
            ));
            requestData.put("max_tokens", 600);
            requestData.put("temperature", 0.5);
            
            // This would make the actual API call through ProxyService
            // For now, we'll use a fallback analysis
            return generateFallbackAnalysis(userInsights, recentMeals);
            
        } catch (Exception e) {
            System.err.println("AI analysis failed: " + e.getMessage());
            return Map.of("error", "Analysis temporarily unavailable");
        }
    }
    
    /**
     * Build meal generation prompt for AI
     */
    private String buildMealGenerationPrompt(UserPreferences preferences, Map<String, Object> userInsights, 
                                           String mealType, LocalDate date) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a personalized meal recommendation for ").append(mealType.toLowerCase()).append(".\n\n");
        
        prompt.append("User Profile:\n");
        prompt.append("- Diet Type: ").append(preferences.getDietType()).append("\n");
        prompt.append("- Daily Calorie Target: ").append(preferences.getDailyCalorieTarget()).append("\n");
        prompt.append("- Cooking Skill: ").append(preferences.getCookingSkillLevel()).append("\n");
        
        if (preferences.getPreferredCuisines() != null && !preferences.getPreferredCuisines().isEmpty()) {
            prompt.append("- Preferred Cuisines: ").append(String.join(", ", preferences.getPreferredCuisines())).append("\n");
        }
        
        if (preferences.getAllergies() != null && !preferences.getAllergies().isEmpty()) {
            prompt.append("- Allergies: ").append(String.join(", ", preferences.getAllergies())).append("\n");
        }
        
        if (preferences.getDislikes() != null && !preferences.getDislikes().isEmpty()) {
            prompt.append("- Dislikes: ").append(String.join(", ", preferences.getDislikes())).append("\n");
        }
        
        prompt.append("\nPlease suggest a healthy, balanced meal that fits these preferences.");
        
        return prompt.toString();
    }
    
    /**
     * Build chatbot prompt with context
     */
    private String buildChatbotPrompt(String message, List<AiConversation> history, UserPreferences preferences) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("User Preferences:\n");
        prompt.append("- Diet: ").append(preferences.getDietType()).append("\n");
        prompt.append("- Calorie Target: ").append(preferences.getDailyCalorieTarget()).append("\n");
        
        if (history.size() > 0) {
            prompt.append("\nRecent Conversation:\n");
            for (AiConversation conv : history.subList(Math.max(0, history.size() - 5), history.size())) {
                prompt.append(conv.getMessageType()).append(": ").append(conv.getContent()).append("\n");
            }
        }
        
        prompt.append("\nCurrent Question: ").append(message);
        prompt.append("\n\nProvide a helpful, personalized response about nutrition and meal planning.");
        
        return prompt.toString();
    }
    
    /**
     * Build recommendation prompt
     */
    private String buildRecommendationPrompt(Map<String, Object> userInsights, List<MealDto> recentMeals, 
                                           String mealType, int limit) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Recommend ").append(limit).append(" personalized meals for ").append(mealType.toLowerCase()).append(".\n\n");
        
        prompt.append("User Insights:\n");
        prompt.append("- Diet Type: ").append(userInsights.get("dietType")).append("\n");
        prompt.append("- Preferred Cuisines: ").append(userInsights.get("preferredCuisines")).append("\n");
        prompt.append("- Average Rating: ").append(userInsights.get("averageRating")).append("\n");
        
        if (recentMeals.size() > 0) {
            prompt.append("\nRecent Meals (avoid repetition):\n");
            recentMeals.stream().limit(10).forEach(meal -> 
                prompt.append("- ").append(meal.getName()).append("\n"));
        }
        
        prompt.append("\nSuggest diverse, healthy options that the user will enjoy.");
        
        return prompt.toString();
    }
    
    /**
     * Build analysis prompt
     */
    private String buildAnalysisPrompt(List<MealDto> recentMeals, Map<String, Object> userInsights) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the user's eating patterns and provide insights.\n\n");
        
        prompt.append("Recent Meals (last 30 days):\n");
        recentMeals.forEach(meal -> 
            prompt.append("- ").append(meal.getName()).append(" (").append(meal.getMealType()).append(")\n"));
        
        prompt.append("\nUser Profile:\n");
        prompt.append("- Diet Type: ").append(userInsights.get("dietType")).append("\n");
        prompt.append("- Calorie Target: ").append(userInsights.get("calorieTarget")).append("\n");
        prompt.append("- Average Rating: ").append(userInsights.get("averageRating")).append("\n");
        
        prompt.append("\nProvide insights about eating patterns, nutritional balance, and suggestions for improvement.");
        
        return prompt.toString();
    }
    
    /**
     * Log conversation for context
     */
    private void logConversation(User user, String sessionId, String messageType, String content) {
        AiConversation conversation = new AiConversation();
        conversation.setUser(user);
        conversation.setSessionId(sessionId);
        conversation.setMessageType(messageType);
        conversation.setContent(content);
        conversation.setCreatedAt(LocalDateTime.now());
        
        aiConversationRepository.save(conversation);
    }
    
    /**
     * Get recent user meals for context
     */
    private List<MealDto> getRecentUserMeals(User user, int days) {
        // This would fetch from the meal history
        // For now, return empty list
        return new ArrayList<>();
    }
    
    /**
     * Fallback meal generation when AI is unavailable
     */
    private List<MealDto> generateFallbackMealPlan(User user, String mealType, UserPreferences preferences) {
        return mealService.getMealsForUser(user, LocalDate.now(), mealType);
    }
    
    /**
     * Fallback chatbot response
     */
    private String generateFallbackChatbotResponse(String message, UserPreferences preferences) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("calorie") || lowerMessage.contains("nutrition")) {
            return "Based on your preferences, you're targeting " + preferences.getDailyCalorieTarget() + 
                   " calories per day. I recommend focusing on balanced meals with lean proteins, whole grains, and plenty of vegetables.";
        } else if (lowerMessage.contains("recipe") || lowerMessage.contains("cook")) {
            return "I'd be happy to help with recipes! Given your " + preferences.getDietType().toLowerCase() + 
                   " diet and " + preferences.getCookingSkillLevel().toLowerCase() + " cooking level, I can suggest some great options.";
        } else {
            return "I'm here to help with your meal planning and nutrition questions! Feel free to ask about recipes, dietary advice, or meal planning strategies.";
        }
    }
    
    /**
     * Fallback recommendations
     */
    private List<MealDto> generateFallbackRecommendations(User user, String mealType, int limit) {
        List<MealDto> meals = mealService.getMealsForUser(user, LocalDate.now(), mealType);
        return meals.stream().limit(limit).collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Fallback analysis
     */
    private Map<String, Object> generateFallbackAnalysis(Map<String, Object> userInsights, List<MealDto> recentMeals) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("dietType", userInsights.get("dietType"));
        analysis.put("totalMealsAnalyzed", recentMeals.size());
        analysis.put("averageRating", userInsights.get("averageRating"));
        analysis.put("insights", "Keep up the great work with your meal planning! Consider adding more variety to your diet.");
        analysis.put("suggestions", Arrays.asList(
            "Try new cuisines to add variety",
            "Include more seasonal vegetables",
            "Consider meal prep for consistency"
        ));
        
        return analysis;
    }
}
