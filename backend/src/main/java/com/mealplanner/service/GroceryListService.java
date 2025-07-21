package com.mealplanner.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

@Service
public class GroceryListService {

    private static final Logger logger = LoggerFactory.getLogger(GroceryListService.class);

    public Map<String, Object> getMonthlyGroceryList(String username, int year, int month) {
        logger.info("Getting monthly grocery list for user: {}, year: {}, month: {}", username, year, month);
        
        // Only return data for years 2025 onwards
        if (year < 2025) {
            logger.info("No grocery list data available for year {}", year);
            return Map.of(
                "groceryList", List.of(),
                "message", "Grocery list data is available from 2025 onwards with seasonal variations"
            );
        }

        // Get seasonal grocery list based on month
        List<Map<String, Object>> groceryList = getSeasonalGroceryList(month, year);
        
        logger.info("Generated seasonal grocery list with {} categories for {}/{}", groceryList.size(), month, year);
        
        return Map.of(
            "groceryList", groceryList,
            "message", "Seasonal grocery list for " + getMonthName(month) + " " + year + " with yearly variations",
            "totalItems", groceryList.stream()
                .mapToInt(cat -> ((List<?>) cat.get("items")).size())
                .sum()
        );
    }
    
    private List<Map<String, Object>> getSeasonalGroceryList(int month, int year) {
        String season = getSeason(month);
        int yearOffset = year - 2025;
        
        // Base grocery items that are available year-round
        List<Map<String, Object>> baseItems = Arrays.asList(
            Map.of(
                "category", "Protein",
                "items", Arrays.asList(
                    Map.of("name", "Chicken Breast", "quantity", 2.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                    Map.of("name", "Salmon Fillets", "quantity", 1.5 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                    Map.of("name", "Lean Beef", "quantity", 1.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                    Map.of("name", "Eggs", "quantity", 24.0 + yearOffset * 2, "unit", "pieces", "imageUrl", null),
                    Map.of("name", "Tofu", "quantity", 500.0 + yearOffset * 25, "unit", "g", "imageUrl", null)
                )
            ),
            Map.of(
                "category", "Dairy",
                "items", Arrays.asList(
                    Map.of("name", "Greek Yogurt", "quantity", 2.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                    Map.of("name", "Milk", "quantity", 3.0 + yearOffset * 0.2, "unit", "L", "imageUrl", null),
                    Map.of("name", "Cheese", "quantity", 500.0 + yearOffset * 25, "unit", "g", "imageUrl", null),
                    Map.of("name", "Butter", "quantity", 250.0 + yearOffset * 10, "unit", "g", "imageUrl", null)
                )
            ),
            Map.of(
                "category", "Grains",
                "items", Arrays.asList(
                    Map.of("name", "Oatmeal", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                    Map.of("name", "Brown Rice", "quantity", 2.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                    Map.of("name", "Quinoa", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                    Map.of("name", "Whole Wheat Bread", "quantity", 2.0 + yearOffset * 0.1, "unit", "loaves", "imageUrl", null),
                    Map.of("name", "Whole Wheat Pasta", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null)
                )
            ),
            Map.of(
                "category", "Nuts & Seeds",
                "items", Arrays.asList(
                    Map.of("name", "Almonds", "quantity", 500.0 + yearOffset * 25, "unit", "g", "imageUrl", null),
                    Map.of("name", "Walnuts", "quantity", 300.0 + yearOffset * 15, "unit", "g", "imageUrl", null),
                    Map.of("name", "Chia Seeds", "quantity", 200.0 + yearOffset * 10, "unit", "g", "imageUrl", null),
                    Map.of("name", "Flax Seeds", "quantity", 200.0 + yearOffset * 10, "unit", "g", "imageUrl", null)
                )
            ),
            Map.of(
                "category", "Spices & Seasonings",
                "items", Arrays.asList(
                    Map.of("name", "Olive Oil", "quantity", 500.0 + yearOffset * 25, "unit", "ml", "imageUrl", null),
                    Map.of("name", "Salt", "quantity", 200.0 + yearOffset * 10, "unit", "g", "imageUrl", null),
                    Map.of("name", "Black Pepper", "quantity", 100.0 + yearOffset * 5, "unit", "g", "imageUrl", null),
                    Map.of("name", "Garlic", "quantity", 200.0 + yearOffset * 10, "unit", "g", "imageUrl", null),
                    Map.of("name", "Ginger", "quantity", 100.0 + yearOffset * 5, "unit", "g", "imageUrl", null),
                    Map.of("name", "Turmeric", "quantity", 50.0 + yearOffset * 2, "unit", "g", "imageUrl", null)
                )
            ),
            Map.of(
                "category", "Snacks",
                "items", Arrays.asList(
                    Map.of("name", "Protein Bars", "quantity", 20.0 + yearOffset, "unit", "pieces", "imageUrl", null),
                    Map.of("name", "Hummus", "quantity", 500.0 + yearOffset * 25, "unit", "g", "imageUrl", null),
                    Map.of("name", "Protein Powder", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null)
                )
            )
        );
        
        // Add seasonal items based on the season
        List<Map<String, Object>> seasonalItems = getSeasonalItems(season, yearOffset);
        baseItems.addAll(seasonalItems);
        
        return baseItems;
    }
    
    private List<Map<String, Object>> getSeasonalItems(String season, int yearOffset) {
        switch (season) {
            case "winter":
                return Arrays.asList(
                    Map.of(
                        "category", "Winter Vegetables",
                        "items", Arrays.asList(
                            Map.of("name", "Butternut Squash", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Sweet Potatoes", "quantity", 2.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Carrots", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Parsnips", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Brussels Sprouts", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null)
                        )
                    ),
                    Map.of(
                        "category", "Winter Fruits",
                        "items", Arrays.asList(
                            Map.of("name", "Oranges", "quantity", 2.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Apples", "quantity", 2.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Pears", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null)
                        )
                    )
                );
                
            case "spring":
                return Arrays.asList(
                    Map.of(
                        "category", "Spring Vegetables",
                        "items", Arrays.asList(
                            Map.of("name", "Asparagus", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Peas", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Spinach", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Arugula", "quantity", 500.0 + yearOffset * 25, "unit", "g", "imageUrl", null),
                            Map.of("name", "Radishes", "quantity", 500.0 + yearOffset * 25, "unit", "g", "imageUrl", null)
                        )
                    ),
                    Map.of(
                        "category", "Spring Fruits",
                        "items", Arrays.asList(
                            Map.of("name", "Strawberries", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Rhubarb", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Cherries", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null)
                        )
                    )
                );
                
            case "summer":
                return Arrays.asList(
                    Map.of(
                        "category", "Summer Vegetables",
                        "items", Arrays.asList(
                            Map.of("name", "Tomatoes", "quantity", 2.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Bell Peppers", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Cucumber", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Zucchini", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Corn", "quantity", 6.0 + yearOffset * 0.5, "unit", "ears", "imageUrl", null)
                        )
                    ),
                    Map.of(
                        "category", "Summer Fruits",
                        "items", Arrays.asList(
                            Map.of("name", "Watermelon", "quantity", 1.0 + yearOffset * 0.05, "unit", "pieces", "imageUrl", null),
                            Map.of("name", "Berries", "quantity", 2.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Peaches", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Plums", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null)
                        )
                    )
                );
                
            case "fall":
                return Arrays.asList(
                    Map.of(
                        "category", "Fall Vegetables",
                        "items", Arrays.asList(
                            Map.of("name", "Pumpkin", "quantity", 2.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Butternut Squash", "quantity", 1.5 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Brussels Sprouts", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Kale", "quantity", 1.0 + yearOffset * 0.05, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Mushrooms", "quantity", 500.0 + yearOffset * 25, "unit", "g", "imageUrl", null)
                        )
                    ),
                    Map.of(
                        "category", "Fall Fruits",
                        "items", Arrays.asList(
                            Map.of("name", "Apples", "quantity", 3.0 + yearOffset * 0.15, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Pears", "quantity", 2.0 + yearOffset * 0.1, "unit", "kg", "imageUrl", null),
                            Map.of("name", "Cranberries", "quantity", 500.0 + yearOffset * 25, "unit", "g", "imageUrl", null),
                            Map.of("name", "Pomegranate", "quantity", 3.0 + yearOffset * 0.2, "unit", "pieces", "imageUrl", null)
                        )
                    )
                );
                
            default:
                return new ArrayList<>();
        }
    }
    
    /**
     * Get grocery list for a specific date range
     */
    public Map<String, Object> getGroceryListForDateRange(String username, LocalDate startDate, LocalDate endDate) {
        logger.info("Getting grocery list for user: {}, date range: {} to {}", username, startDate, endDate);
        
        // Only return data for years 2025 onwards
        if (startDate.getYear() < 2025 || endDate.getYear() < 2025) {
            return Map.of(
                "groceryList", List.of(),
                "message", "Grocery list data is available from 2025 onwards with seasonal variations"
            );
        }
        
        // For date ranges, we'll use the start month's seasonal list
        return getMonthlyGroceryList(username, startDate.getYear(), startDate.getMonthValue());
    }
    
    /**
     * Get current month grocery list
     */
    public Map<String, Object> getCurrentMonthGroceryList(String username) {
        LocalDate now = LocalDate.now();
        return getMonthlyGroceryList(username, now.getYear(), now.getMonthValue());
    }
    
    /**
     * Get season based on month
     */
    private String getSeason(int month) {
        if (month == 12 || month == 1 || month == 2) return "winter";
        if (month == 3 || month == 4 || month == 5) return "spring";
        if (month == 6 || month == 7 || month == 8) return "summer";
        return "fall"; // 9, 10, 11
    }
    
    /**
     * Get month name
     */
    private String getMonthName(int month) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month - 1];
    }
} 