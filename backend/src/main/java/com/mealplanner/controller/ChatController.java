package com.mealplanner.controller;

import com.mealplanner.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
            }

            String response = chatService.getNutritionistResponse(message);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to process message: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "Chat service is running"));
    }
} 