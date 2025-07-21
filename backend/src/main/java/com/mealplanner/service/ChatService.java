package com.mealplanner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final Random random = new Random();

    // Predefined responses for common nutrition questions
    private final Map<String, String[]> responseMap = new HashMap<>();

    public ChatService() {
        initializeResponses();
    }

    private void initializeResponses() {
        // Weight management
        responseMap.put("weight", new String[]{
            "To manage your weight effectively, focus on creating a sustainable calorie deficit through a combination of healthy eating and regular exercise. Aim for a balanced diet rich in whole foods, lean proteins, vegetables, and healthy fats. Remember, gradual changes are more sustainable than drastic diets.",
            "Weight management is about balance. Focus on portion control, regular physical activity, and choosing nutrient-dense foods. Track your progress but don't obsess over the scale - how you feel is just as important!",
            "Sustainable weight management involves lifestyle changes, not quick fixes. Eat mindfully, stay hydrated, get adequate sleep, and find physical activities you enjoy. Small, consistent changes lead to lasting results."
        });

        // Protein and muscle building
        responseMap.put("protein", new String[]{
            "Protein is essential for muscle building and repair. Good sources include lean meats, fish, eggs, legumes, and dairy. Aim for 0.8-1.2g of protein per kg of body weight daily, or 1.6-2.2g if you're actively building muscle.",
            "Excellent protein sources include chicken breast, fish, eggs, Greek yogurt, cottage cheese, beans, lentils, and quinoa. Try to include protein in every meal to support muscle maintenance and recovery.",
            "For muscle building, spread your protein intake throughout the day. Include 20-30g of protein in each meal. Don't forget that timing matters - consuming protein within 2 hours after exercise can help with muscle recovery."
        });

        // Vitamins and supplements
        responseMap.put("vitamin", new String[]{
            "While supplements can be helpful, it's best to get most nutrients from whole foods. Focus on a colorful diet with plenty of fruits and vegetables. Consider consulting a healthcare provider before starting any supplements.",
            "Aim to eat a rainbow of fruits and vegetables to get a variety of vitamins and minerals naturally. Dark leafy greens, citrus fruits, berries, and colorful vegetables are excellent sources of essential nutrients.",
            "Before taking supplements, try to identify any gaps in your diet. Common deficiencies include Vitamin D, B12, and iron. Always consult with a healthcare provider for personalized supplement recommendations."
        });

        // Meal planning
        responseMap.put("meal", new String[]{
            "A balanced meal should include protein, complex carbohydrates, healthy fats, and plenty of vegetables. Try to eat regular meals and include snacks if needed. Planning ahead can help you make healthier choices throughout the day.",
            "Meal planning saves time and helps you make healthier choices. Start by planning 3-4 meals per week, prep ingredients in advance, and keep healthy snacks readily available. Remember to include variety to ensure you get all necessary nutrients.",
            "When meal planning, think about the plate method: half vegetables, quarter lean protein, quarter whole grains. This simple approach helps create balanced, nutritious meals without overthinking."
        });

        // Calories and nutrition
        responseMap.put("calorie", new String[]{
            "Calories are important, but quality matters more than quantity. Focus on nutrient-dense foods that provide vitamins, minerals, and fiber. Track your intake initially to understand your needs, then focus on building healthy habits.",
            "Your calorie needs depend on your age, activity level, and goals. Use online calculators as a starting point, but listen to your body's hunger and fullness cues. Quality of food is just as important as quantity.",
            "Instead of obsessing over calories, focus on eating whole, unprocessed foods. These naturally help regulate your appetite and provide the nutrients your body needs to function optimally."
        });

        // Healthy eating habits
        responseMap.put("healthy", new String[]{
            "Healthy eating is about balance and consistency. Include a variety of whole foods, stay hydrated, eat mindfully, and don't deprive yourself. Remember, it's about progress, not perfection.",
            "Build healthy eating habits gradually. Start with small changes like adding more vegetables to your meals, drinking more water, or cooking at home more often. Small steps lead to big changes over time.",
            "Healthy eating doesn't mean giving up your favorite foods. It's about making better choices most of the time and enjoying treats in moderation. Focus on adding nutritious foods rather than just removing 'bad' ones."
        });

        // Exercise and nutrition
        responseMap.put("exercise", new String[]{
            "Nutrition and exercise work together for optimal health. Fuel your workouts with carbohydrates before exercise and protein after. Stay hydrated and listen to your body's needs.",
            "Your nutrition needs change based on your activity level. Active individuals may need more calories, protein, and carbohydrates. Time your meals around your workouts for better performance and recovery.",
            "Whether you're doing cardio or strength training, proper nutrition supports your performance and recovery. Don't forget that rest and recovery are just as important as exercise and nutrition."
        });

        // General nutrition advice
        responseMap.put("general", new String[]{
            "Thank you for your question! I'm here to help with nutrition advice. For personalized recommendations, consider consulting with a registered dietitian or nutritionist. Remember that individual needs vary, and what works for one person may not work for another.",
            "Great question! Nutrition is personal, and what works for one person might not work for another. Focus on whole foods, stay hydrated, and listen to your body. When in doubt, consult with a healthcare professional.",
            "I'm happy to help with nutrition questions! Remember that while I can provide general advice, individual needs vary. For specific health concerns or personalized plans, consider working with a registered dietitian."
        });
    }

    public String getNutritionistResponse(String userMessage) {
        try {
            String lowerMessage = userMessage.toLowerCase();
            String response = getContextualResponse(lowerMessage);
            
            // Add some personality and encouragement
            response = addEncouragement(response);
            
            return response;
        } catch (Exception e) {
            logger.error("Error generating nutritionist response: {}", e.getMessage());
            return "I'm here to help with your nutrition questions! Could you please rephrase your question or ask about a specific nutrition topic?";
        }
    }

    private String getContextualResponse(String message) {
        // Check for specific keywords and return appropriate responses
        if (message.contains("weight") || message.contains("lose") || message.contains("gain") || message.contains("diet")) {
            return getRandomResponse("weight");
        } else if (message.contains("protein") || message.contains("muscle") || message.contains("build")) {
            return getRandomResponse("protein");
        } else if (message.contains("vitamin") || message.contains("supplement") || message.contains("nutrient")) {
            return getRandomResponse("vitamin");
        } else if (message.contains("meal") || message.contains("plan") || message.contains("cook") || message.contains("recipe")) {
            return getRandomResponse("meal");
        } else if (message.contains("calorie") || message.contains("energy") || message.contains("burn")) {
            return getRandomResponse("calorie");
        } else if (message.contains("healthy") || message.contains("good") || message.contains("bad") || message.contains("food")) {
            return getRandomResponse("healthy");
        } else if (message.contains("exercise") || message.contains("workout") || message.contains("gym") || message.contains("training")) {
            return getRandomResponse("exercise");
        } else {
            return getRandomResponse("general");
        }
    }

    private String getRandomResponse(String category) {
        String[] responses = responseMap.get(category);
        if (responses != null && responses.length > 0) {
            return responses[random.nextInt(responses.length)];
        }
        return getRandomResponse("general");
    }

    private String addEncouragement(String response) {
        String[] encouragements = {
            " Keep up the great work on your health journey! 💪",
            " You're making excellent choices for your health! 🌟",
            " Remember, every healthy choice counts! 🎯",
            " You've got this! Small steps lead to big changes! ✨",
            " Your commitment to health is inspiring! 🌱"
        };
        
        return response + encouragements[random.nextInt(encouragements.length)];
    }
} 