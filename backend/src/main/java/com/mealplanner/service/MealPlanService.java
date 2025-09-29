package com.mealplanner.service;

import com.mealplanner.entity.DailyMealPlan;
import com.mealplanner.entity.Meal;
import com.mealplanner.entity.User;
import com.mealplanner.entity.UserPreferences;
import com.mealplanner.repository.DailyMealPlanRepository;
import com.mealplanner.repository.MealRepository;
import com.mealplanner.repository.UserPreferencesRepository;
import com.mealplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealPlanService {
    
    @Autowired
    private DailyMealPlanRepository dailyMealPlanRepository;
    
    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    /**
     * Generate a complete meal plan for a specific year
     */
    public void generateMealPlanForYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        // Delete existing meal plans for this year
        dailyMealPlanRepository.deleteByCreatedForYear(year);
        
        // Generate meal plans for each day
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            generateMealPlanForDay(currentDate, year);
            currentDate = currentDate.plusDays(1);
        }
    }
    
    /**
     * Generate meal plan for a specific day
     */
    public void generateMealPlanForDay(LocalDate date, int year) {
        String[] mealTypes = {"BREAKFAST", "LUNCH", "DINNER", "SNACK"};
        
        for (String mealType : mealTypes) {
            // Check if meal plan already exists for this date and meal type
            if (!dailyMealPlanRepository.existsByMealDateAndMealTypeAndCreatedForYear(date, mealType, year)) {
                // Get a random meal for this type
                Optional<Meal> randomMeal = mealRepository.findRandomByMealType(mealType);
                
                if (randomMeal.isPresent()) {
                    DailyMealPlan mealPlan = new DailyMealPlan(date, mealType, randomMeal.get(), year);
                    dailyMealPlanRepository.save(mealPlan);
                }
            }
        }
    }
    
    /**
     * Generate personalized meal plan for a user for a specific year
     */
    public void generatePersonalizedMealPlanForYear(User user, int year) {
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElse(null);
        
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        // Delete existing meal plans for this year
        dailyMealPlanRepository.deleteByCreatedForYear(year);
        
        // Generate meal plans for each day
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            generatePersonalizedMealPlanForDay(currentDate, year, preferences);
            currentDate = currentDate.plusDays(1);
        }
    }
    
    /**
     * Generate personalized meal plan for a specific day
     */
    public void generatePersonalizedMealPlanForDay(LocalDate date, int year, UserPreferences preferences) {
        String[] mealTypes = {"BREAKFAST", "LUNCH", "DINNER", "SNACK"};
        
        for (String mealType : mealTypes) {
            if (!dailyMealPlanRepository.existsByMealDateAndMealTypeAndCreatedForYear(date, mealType, year)) {
                Meal selectedMeal = selectMealForUser(mealType, preferences);
                
                if (selectedMeal != null) {
                    DailyMealPlan mealPlan = new DailyMealPlan(date, mealType, selectedMeal, year);
                    dailyMealPlanRepository.save(mealPlan);
                }
            }
        }
    }
    
    /**
     * Select a meal based on user preferences
     */
    private Meal selectMealForUser(String mealType, UserPreferences preferences) {
        if (preferences == null) {
            return mealRepository.findRandomByMealType(mealType).orElse(null);
        }
        
        String diet = preferences.getDietType();
        Integer maxCalories = preferences.getDailyCalorieTarget() != null ? 
            preferences.getDailyCalorieTarget() / 4 : 500; // Divide daily target by 4 meals
        
        List<Meal> suitableMeals = mealRepository.findSuitableMeals(mealType, maxCalories, diet);
        
        if (suitableMeals.isEmpty()) {
            // Fallback to random meal
            return mealRepository.findRandomByMealType(mealType).orElse(null);
        }
        
        // Filter out meals with allergies or dislikes
        suitableMeals = suitableMeals.stream()
                .filter(meal -> !hasAllergyOrDislike(meal, preferences))
                .collect(Collectors.toList());
        
        if (suitableMeals.isEmpty()) {
            return mealRepository.findRandomByMealType(mealType).orElse(null);
        }
        
        // Randomly select from suitable meals
        Random random = new Random();
        return suitableMeals.get(random.nextInt(suitableMeals.size()));
    }
    
    /**
     * Check if meal has any allergies or dislikes
     */
    private boolean hasAllergyOrDislike(Meal meal, UserPreferences preferences) {
        if (preferences.getAllergies() != null) {
            for (String allergy : preferences.getAllergies()) {
                if (meal.getName().toLowerCase().contains(allergy.toLowerCase()) ||
                    (meal.getDescription() != null && meal.getDescription().toLowerCase().contains(allergy.toLowerCase()))) {
                    return true;
                }
            }
        }
        
        if (preferences.getDislikes() != null) {
            for (String dislike : preferences.getDislikes()) {
                if (meal.getName().toLowerCase().contains(dislike.toLowerCase()) ||
                    (meal.getDescription() != null && meal.getDescription().toLowerCase().contains(dislike.toLowerCase()))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get meal plan for a specific date
     */
    public List<DailyMealPlan> getMealPlanForDate(LocalDate date) {
        int currentYear = Year.now().getValue();
        return dailyMealPlanRepository.findByMealDateAndCreatedForYear(date, currentYear);
    }
    
    /**
     * Get meal plan for a specific date and year
     */
    public List<DailyMealPlan> getMealPlanForDateAndYear(LocalDate date, int year) {
        return dailyMealPlanRepository.findByMealDateAndCreatedForYear(date, year);
    }
    
    /**
     * Get meal plan for a date range
     */
    public List<DailyMealPlan> getMealPlanForDateRange(LocalDate startDate, LocalDate endDate) {
        int currentYear = Year.now().getValue();
        return dailyMealPlanRepository.findByMealDateBetweenAndCreatedForYear(startDate, endDate, currentYear);
    }
    
    /**
     * Get meal plan for a date range and year
     */
    public List<DailyMealPlan> getMealPlanForDateRangeAndYear(LocalDate startDate, LocalDate endDate, int year) {
        return dailyMealPlanRepository.findByMealDateBetweenAndCreatedForYear(startDate, endDate, year);
    }
    
    /**
     * Get meal plan for a specific month
     */
    public List<DailyMealPlan> getMealPlanForMonth(int month, int year) {
        return dailyMealPlanRepository.findByMonthAndYear(month, year);
    }
    
    /**
     * Get meal plan for current month
     */
    public List<DailyMealPlan> getMealPlanForCurrentMonth() {
        LocalDate now = LocalDate.now();
        return getMealPlanForMonth(now.getMonthValue(), now.getYear());
    }
    
    /**
     * Update meal for a specific date and meal type
     */
    public DailyMealPlan updateMealForDate(LocalDate date, String mealType, Long mealId) {
        int currentYear = Year.now().getValue();
        
        List<DailyMealPlan> existingPlans = dailyMealPlanRepository
                .findByMealDateAndMealTypeAndCreatedForYear(date, mealType, currentYear);
        
        if (!existingPlans.isEmpty()) {
            DailyMealPlan plan = existingPlans.get(0);
            Meal newMeal = mealRepository.findById(mealId).orElse(null);
            if (newMeal != null) {
                plan.setMeal(newMeal);
                return dailyMealPlanRepository.save(plan);
            }
        }
        
        return null;
    }
    
    /**
     * Get meal plan statistics for a year
     */
    public Map<String, Object> getMealPlanStats(int year) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalMealPlans = dailyMealPlanRepository.countByCreatedForYear(year);
        stats.put("totalMealPlans", totalMealPlans);
        
        // Count by meal type
        Map<String, Long> mealTypeCounts = new HashMap<>();
        mealTypeCounts.put("BREAKFAST", dailyMealPlanRepository.countByMealTypeAndCreatedForYear("BREAKFAST", year));
        mealTypeCounts.put("LUNCH", dailyMealPlanRepository.countByMealTypeAndCreatedForYear("LUNCH", year));
        mealTypeCounts.put("DINNER", dailyMealPlanRepository.countByMealTypeAndCreatedForYear("DINNER", year));
        mealTypeCounts.put("SNACK", dailyMealPlanRepository.countByMealTypeAndCreatedForYear("SNACK", year));
        stats.put("mealTypeCounts", mealTypeCounts);
        
        // Date range
        Optional<LocalDate> earliestDate = dailyMealPlanRepository.findEarliestMealDateByYear(year);
        Optional<LocalDate> latestDate = dailyMealPlanRepository.findLatestMealDateByYear(year);
        stats.put("earliestDate", earliestDate.orElse(null));
        stats.put("latestDate", latestDate.orElse(null));
        
        return stats;
    }
    
    /**
     * Check if meal plan exists for a year
     */
    public boolean hasMealPlanForYear(int year) {
        return dailyMealPlanRepository.countByCreatedForYear(year) > 0;
    }
    
    /**
     * Get unassigned meal plans (where meal is null)
     */
    public List<DailyMealPlan> getUnassignedMealPlans(int year) {
        return dailyMealPlanRepository.findByMealIsNullAndCreatedForYear(year);
    }
} 