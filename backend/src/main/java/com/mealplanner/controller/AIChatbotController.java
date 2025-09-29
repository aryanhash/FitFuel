package com.mealplanner.controller;

import com.mealplanner.entity.User;
import com.mealplanner.service.AIService;
import com.mealplanner.service.EnhancedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/chatbot")
@CrossOrigin(origins = "http://localhost:3000")
public class AIChatbotController {
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private EnhancedUserService userService;
    
    /**
     * Handle chatbot conversation
     */
    @PostMapping("/chat/{userId}")
    public ResponseEntity<?> handleChatMessage(
            @PathVariable Long userId,
            @RequestBody ChatRequest request) {
        
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }
            
            String response = aiService.handleChatbotQuery(user, request.getMessage(), sessionId);
            
            Map<String, Object> chatResponse = new HashMap<>();
            chatResponse.put("message", response);
            chatResponse.put("sessionId", sessionId);
            chatResponse.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(chatResponse);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Chat failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Start new chat session
     */
    @PostMapping("/start-session/{userId}")
    public ResponseEntity<?> startChatSession(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String sessionId = UUID.randomUUID().toString();
            
            // Send welcome message
            String welcomeMessage = "Hello! I'm your personal nutrition and meal planning assistant. " +
                    "I can help you with meal suggestions, dietary advice, recipe recommendations, and more. " +
                    "What would you like to know today?";
            
            aiService.handleChatbotQuery(user, "START_SESSION", sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("welcomeMessage", welcomeMessage);
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to start session: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get chat history for a session
     */
    @GetMapping("/history/{userId}/{sessionId}")
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long userId,
            @PathVariable String sessionId) {
        
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // This would fetch from AiConversationRepository
            // For now, return empty history
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("sessionId", sessionId);
            response.put("history", new java.util.ArrayList<>());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get chat history: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get quick suggestions for common queries
     */
    @GetMapping("/quick-suggestions/{userId}")
    public ResponseEntity<?> getQuickSuggestions(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> suggestions = new HashMap<>();
            suggestions.put("commonQuestions", new String[]{
                "What should I eat for breakfast?",
                "Give me healthy lunch ideas",
                "Suggest a low-calorie dinner",
                "What are some vegetarian options?",
                "Help me plan my weekly meals",
                "What are good sources of protein?",
                "How can I reduce my calorie intake?",
                "Suggest meals for weight loss"
            });
            
            suggestions.put("mealTypes", new String[]{
                "BREAKFAST", "LUNCH", "DINNER", "SNACK"
            });
            
            suggestions.put("dietTypes", new String[]{
                "VEG", "NON_VEG", "VEGAN", "KETO", "PALEO", "MIXED"
            });
            
            return ResponseEntity.ok(suggestions);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get suggestions: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Request class for chat messages
     */
    public static class ChatRequest {
        private String message;
        private String sessionId;
        
        // Getters and Setters
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }
}
