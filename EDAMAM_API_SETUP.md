# Edamam Recipe API Setup

## Overview
We've integrated the Edamam Recipe API to provide real meal data instead of hardcoded sample data. Edamam offers a free tier with 5,000 requests per month.

## Setup Instructions

### 1. Get Edamam API Credentials
1. Go to [Edamam Developer Portal](https://developer.edamam.com/)
2. Sign up for a free account
3. Create a new application
4. Get your **Application ID** and **Application Key**

### 2. Configure Your Application
Update `backend/src/main/resources/application.properties`:

```properties
# Edamam Recipe API Configuration
edamam.app.id=YOUR_ACTUAL_APP_ID_HERE
edamam.app.key=YOUR_ACTUAL_APP_KEY_HERE
```

### 3. Test the Integration
Once configured, you can test these new endpoints:

```bash
# Search for breakfast recipes
curl "http://localhost:8081/api/meal-plan/search-recipes?mealType=BREAKFAST&dietType=vegetarian&maxResults=5"

# Get a random lunch recipe
curl "http://localhost:8081/api/meal-plan/random-recipe?mealType=LUNCH&dietType=vegan"

# Search for dinner recipes
curl "http://localhost:8081/api/meal-plan/search-recipes?mealType=DINNER&maxResults=10"
```

## API Features

### Supported Meal Types
- BREAKFAST
- LUNCH  
- DINNER
- SNACK

### Supported Diet Types
- vegetarian
- vegan
- keto-friendly
- paleo
- low-carb
- low-fat

### Response Format
The API returns structured meal data including:
- Recipe name and description
- Nutritional information (calories, protein, carbs, fat, etc.)
- Cooking time and servings
- Ingredients list
- Instructions
- Recipe image
- Source information

## Benefits of Using Real API Data

1. **Fresh Content**: Always up-to-date recipes
2. **Nutritional Accuracy**: Professional nutritional analysis
3. **Variety**: Access to thousands of recipes
4. **Images**: High-quality recipe photos
5. **Detailed Instructions**: Step-by-step cooking instructions
6. **Dietary Filters**: Support for various dietary preferences

## Fallback Strategy
If the Edamam API is unavailable, the system will:
1. First try to use cached data from your local database
2. Fall back to the original sample data if needed
3. Return appropriate error messages

## Rate Limits
- Free tier: 5,000 requests per month
- Paid tiers available for higher usage
- Consider caching popular recipes locally

## Alternative APIs
If you prefer different data sources, you can also integrate:
- **Spoonacular API**: More features, paid service
- **Food.com API**: Recipe data
- **USDA Food Database**: Nutritional data
- **Open Food Facts**: Open source food database
