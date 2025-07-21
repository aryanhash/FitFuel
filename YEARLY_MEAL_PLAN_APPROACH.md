# Yearly Meal Plan Approach with Seasonal Variations

## Overview

This document outlines the comprehensive approach implemented to display different meals for all months of the year with yearly variations and seasonal considerations.

## Key Features Implemented

### 1. **All-Year Support**
- ✅ Support for all 12 months of the year
- ✅ Support for all years from 2025 onwards
- ✅ Automatic yearly variations and improvements

### 2. **Seasonal Meal System**
- ✅ **Winter** (Dec, Jan, Feb): Warm meals, root vegetables, citrus fruits
- ✅ **Spring** (Mar, Apr, May): Fresh vegetables, berries, light meals
- ✅ **Summer** (Jun, Jul, Aug): Cold meals, grilled vegetables, summer fruits
- ✅ **Fall** (Sep, Oct, Nov): Pumpkin, apples, hearty meals

### 3. **Yearly Variations**
- ✅ **Calorie Adjustments**: Gradual reduction over years (healthier trend)
- ✅ **Protein Increases**: Slight protein boost each year
- ✅ **Recipe Updates**: "Updated for [Year]" descriptions
- ✅ **Quantity Variations**: Grocery quantities adjust yearly

### 4. **Meal Rotation System**
- ✅ **Deterministic Selection**: Same date always gets same meal (for consistency)
- ✅ **Yearly Variation**: Different years get different meals for same date
- ✅ **Seasonal Integration**: 20% chance of seasonal meals replacing base meals

## Technical Implementation

### Backend Changes

#### 1. **Enhanced MealPlanService**
```java
// Base meals available year-round
private final Map<String, List<Map<String, Object>>> baseMeals = new HashMap<>();

// Seasonal meals for each season
private final Map<String, List<Map<String, Object>>> seasonalMeals = new HashMap<>();
```

#### 2. **Yearly Modification Logic**
```java
private void applyYearlyModifications(Map<String, Object> meal, int year, int month) {
    // Reduce calories by 2 per year (healthier trend)
    int calorieAdjustment = yearOffset * -2;
    
    // Increase protein by 0.5g per year
    double proteinAdjustment = yearOffset * 0.5;
    
    // Add seasonal variations (20% chance)
    if (Math.abs(hash) % 5 == 0) {
        // Use seasonal meal instead
    }
}
```

#### 3. **Seasonal Grocery Lists**
```java
private List<Map<String, Object>> getSeasonalItems(String season, int yearOffset) {
    switch (season) {
        case "winter": // Root vegetables, citrus
        case "spring": // Fresh greens, berries
        case "summer": // Tomatoes, summer fruits
        case "fall":   // Pumpkin, apples
    }
}
```

### Frontend Changes

#### 1. **Dynamic Calendar**
- ✅ Shows current year/month by default
- ✅ Allows navigation to any year from 2025 onwards
- ✅ Visual indicators for available data

#### 2. **Enhanced Meal Display**
- ✅ Shows nutritional information (calories, protein, carbs, fat)
- ✅ Displays seasonal meal indicators
- ✅ Shows yearly update messages

## Data Structure

### Base Meals (Year-Round)
- **Breakfast**: 8 different meals (oatmeal, yogurt, eggs, etc.)
- **Lunch**: 8 different meals (salads, wraps, soups, etc.)
- **Dinner**: 8 different meals (stir-fries, grilled items, etc.)
- **Snacks**: 8 different snacks (protein bars, nuts, etc.)

### Seasonal Meals
- **Winter**: 3 seasonal meals (warm oatmeal, squash soup, root vegetables)
- **Spring**: 3 seasonal meals (asparagus, pea risotto, strawberry salad)
- **Summer**: 3 seasonal meals (fruit smoothie, grilled vegetables, watermelon soup)
- **Fall**: 3 seasonal meals (pumpkin oatmeal, apple cider chicken, Brussels sprouts)

### Grocery Lists
- **Base Items**: Protein, dairy, grains, nuts, spices, snacks
- **Seasonal Items**: Vegetables and fruits specific to each season
- **Yearly Adjustments**: Quantities increase slightly each year

## API Endpoints

### Meal Planning
- `GET /api/meal-plan/day?date=YYYY-MM-DD` - Get meals for specific date
- `GET /api/meal-plan/month/{month}/{year}` - Get meals for specific month/year
- `GET /api/meal-plan/current-month` - Get current month meals
- `GET /api/meal-plan/stats/{year}` - Get yearly statistics
- `GET /api/meal-plan/seasonal/{month}` - Get seasonal meals for month

### Grocery Lists
- `GET /api/grocery-list/monthly?year=YYYY&month=M` - Get monthly grocery list
- `GET /api/grocery-list/current-month` - Get current month grocery list

## Yearly Evolution Examples

### 2025 (Base Year)
- Calories: 350 (base)
- Protein: 25g (base)
- Description: "Oatmeal with protein powder, berries, and nuts"

### 2026 (Year 2)
- Calories: 348 (-2)
- Protein: 25.5g (+0.5)
- Description: "Oatmeal with protein powder, berries, and nuts (Updated for 2026)"

### 2027 (Year 3)
- Calories: 346 (-4)
- Protein: 26g (+1.0)
- Description: "Oatmeal with protein powder, berries, and nuts (Updated for 2027)"

## Benefits of This Approach

### 1. **Consistency**
- Same date always shows same meal (deterministic)
- Users can plan ahead reliably

### 2. **Variety**
- Different years show different meals
- Seasonal variations add natural diversity
- 32 base meals + 12 seasonal meals = 44 total meal options

### 3. **Health Trends**
- Gradual calorie reduction promotes healthier eating
- Protein increases support fitness goals
- Yearly updates show continuous improvement

### 4. **Seasonal Awareness**
- Meals match seasonal availability
- Grocery lists reflect seasonal produce
- Natural eating patterns

### 5. **Scalability**
- Easy to add new meals each year
- Simple to modify seasonal variations
- Extensible for new features

## Future Enhancements

### 1. **User Preferences**
- Allow users to set dietary restrictions
- Customize calorie targets
- Select preferred cuisines

### 2. **Recipe Details**
- Add cooking instructions
- Include ingredient lists
- Provide cooking time estimates

### 3. **Advanced Seasonal Logic**
- Location-based seasonal adjustments
- Climate-specific meal recommendations
- Holiday-specific meal variations

### 4. **Machine Learning Integration**
- Learn from user preferences
- Optimize meal selections
- Predict user satisfaction

## Usage Examples

### Viewing Meals for Different Years
```javascript
// 2025 meals
fetch('/api/meal-plan/day?date=2025-07-15')

// 2026 meals (different due to yearly variations)
fetch('/api/meal-plan/day?date=2026-07-15')

// 2027 meals (further variations)
fetch('/api/meal-plan/day?date=2027-07-15')
```

### Seasonal Grocery Lists
```javascript
// Winter grocery list (Dec 2025)
fetch('/api/grocery-list/monthly?year=2025&month=12')

// Spring grocery list (Mar 2026)
fetch('/api/grocery-list/monthly?year=2026&month=3')
```

This approach provides a comprehensive, scalable, and user-friendly meal planning system that evolves naturally over time while maintaining consistency and variety. 