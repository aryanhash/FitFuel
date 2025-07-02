package com.mealplanner.controller;

import com.mealplanner.service.YouTubeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/youtube")
public class YouTubeController {

    private final YouTubeService youTubeService;

    public YouTubeController(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    @GetMapping("/search")
    public String searchRecipeVideo(@RequestParam String query) {
        return youTubeService.searchRecipeVideo(query);
    }
    
    @GetMapping("/test")
    public String testYouTubeAPI() {
        // Test with a simple query to see what the API returns
        return youTubeService.searchRecipeVideo("smoothie");
    }
} 