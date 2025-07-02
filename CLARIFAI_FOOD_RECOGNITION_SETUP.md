# Clarifai Food Model Integration Setup

This guide explains how to set up and use the Clarifai Food Model API for food image recognition in your meal planner app.

## Overview

The Clarifai Food Model integration works as follows:
1. **Image Recognition**: Clarifai's Food Model analyzes uploaded food images and identifies food items
2. **Nutrition Lookup**: The detected food names are then used to search Nutritionix for nutrition data
3. **Combined Results**: The app returns both the detected food and its nutritional information

## Setup Steps

### 1. Get Clarifai API Key

1. Go to [Clarifai Portal](https://portal.clarifai.com/)
2. Create a free account or sign in
3. Navigate to "Security" → "API Keys"
4. Create a new API key or copy an existing one
5. Note: Free tier includes 5,000 API calls per month

### 2. Update Configuration

1. Open `backend/src/main/resources/application.properties`
2. Replace `YOUR_CLARIFAI_API_KEY` with your actual Clarifai API key:

```properties
clarifai.api.key=your_actual_clarifai_api_key_here
```

### 3. Verify API Configuration

The app is configured to use Clarifai as the primary food recognition service:

```properties
food.recognition.api=clarifai
```

## How It Works

### Image Analysis Flow

1. **Upload Image**: User uploads a food photo through the frontend
2. **Base64 Conversion**: Image is converted to base64 format
3. **Clarifai API Call**: Image is sent to Clarifai's Food Model endpoint
4. **Food Detection**: Clarifai returns detected food concepts with confidence scores
5. **Nutrition Lookup**: Each detected food name is searched in Nutritionix
6. **Results**: Combined food recognition and nutrition data is returned

### API Endpoints

- **Clarifai Food Model**: `https://api.clarifai.com/v2/models/food-item-recognition/outputs`
- **Nutritionix Search**: `https://trackapi.nutritionix.com/v2/search/instant`

### Request Format

The Clarifai API request follows this structure:

```json
{
  "user_app_id": {
    "user_id": "clarifai",
    "app_id": "main"
  },
  "inputs": [
    {
      "data": {
        "image": {
          "base64": "base64_encoded_image_data"
        }
      }
    }
  ]
}
```

### Response Format

```json
{
  "status": {
    "code": 10000,
    "description": "Ok"
  },
  "outputs": [
    {
      "data": {
        "concepts": [
          {
            "name": "apple",
            "value": 0.95
          },
          {
            "name": "fruit",
            "value": 0.87
          }
        ]
      }
    }
  ]
}
```

## Features

### Food Recognition
- Recognizes thousands of food items using Clarifai's Food Model
- Provides confidence scores for each detection
- Filters results to only include high-confidence matches (>50%)

### Nutrition Data
- Automatic nutrition lookup for detected foods using Nutritionix
- Includes calories, protein, carbs, and fat
- Falls back to common nutrition data if API lookup fails

### Error Handling
- Graceful fallback to sample data if APIs are unavailable
- Comprehensive logging for debugging
- User-friendly error messages
- Proper HTTP status code handling

## Testing

### Test with Sample Images
1. Start the backend server
2. Use the frontend food scanner
3. Upload food images (apples, bananas, pizza, etc.)
4. Check the console logs for API responses

### Expected Results
- Clear food identification (e.g., "apple", "banana", "pizza")
- Accurate nutrition data from Nutritionix
- Reasonable confidence scores

## Troubleshooting

### Common Issues

1. **"No food items detected"**
   - Check if the image is clear and contains recognizable food
   - Verify the Clarifai API key is correct
   - Check API usage limits

2. **"Clarifai API error"**
   - Verify Clarifai API key is correct
   - Check API usage limits (5,000 requests/month free tier)
   - Ensure image format is supported (JPEG, PNG)
   - Check the response body in logs for detailed error messages

3. **"Nutritionix API error"**
   - Verify Nutritionix API keys are valid
   - Check API usage limits (1,000 requests/month free tier)

### Debug Logs

Enable debug logging in `application.properties`:

```properties
logging.level.com.mealplanner.service.ClarifaiFoodRecognitionService=DEBUG
```

### API Response Codes

- **10000**: Success
- **10002**: API key invalid
- **10010**: Rate limit exceeded
- **10020**: Invalid request format

## API Limits

- **Clarifai**: 5,000 requests/month (free tier)
- **Nutritionix**: 1,000 requests/month (free tier)

## Cost Considerations

- **Clarifai**: Free tier is sufficient for development and small-scale use
- **Nutritionix**: Free tier is sufficient for development and small-scale use
- **Production**: Consider paid tiers for higher usage

## Alternative APIs

If you need to switch APIs, update `application.properties`:

```properties
# Use Nutritionix only (no image recognition)
food.recognition.api=nutritionix

# Use Google Vision (requires Google Cloud setup)
food.recognition.api=google-vision
```

## Implementation Details

### Service Architecture
- `ClarifaiFoodRecognitionService`: Main service implementing the FoodRecognitionService interface
- Uses OkHttp for HTTP requests to Clarifai API
- Uses RestTemplate for Nutritionix API calls
- Implements proper error handling and fallback mechanisms

### Configuration
- Spring Boot configuration with conditional beans
- Environment-based API key management
- Easy switching between different food recognition services

## Next Steps

1. Test with various food images
2. Monitor API usage and costs
3. Consider implementing caching for frequently detected foods
4. Add support for multiple food items in single images
5. Implement portion size estimation
6. Add support for Clarifai's gRPC client for better performance (optional) 