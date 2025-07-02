# Food Recognition API Setup Guide

This guide will help you set up real food recognition APIs for your meal planner application. You have two options:

## Option 1: Nutritionix API (Recommended - Primary)

**Free Tier:** 1,000 requests/month
**Best for:** Food database with nutrition info

### Setup Steps:

1. **Get Free API Keys:**
   - Go to: https://www.nutritionix.com/business/api
   - Sign up for a free account
   - Get your App ID and App Key

2. **Update Configuration:**
   ```properties
   # In backend/src/main/resources/application.properties
   food.recognition.api=nutritionix
   nutritionix.app.id=your_actual_app_id_here
   nutritionix.app.key=your_actual_app_key_here
   ```

3. **Test the API:**
   ```bash
   curl -X GET http://localhost:8081/api/food/test
   ```

## Option 2: Google Cloud Vision API (Backup)

**Free Tier:** 1,000 requests/month
**Best for:** General image recognition

### Setup Steps:

1. **Get API Key:**
   - Go to: https://console.cloud.google.com/
   - Create a new project or select existing
   - Enable the Cloud Vision API
   - Go to APIs & Services > Credentials
   - Create an API Key

2. **Update Configuration:**
   ```properties
   # In backend/src/main/resources/application.properties
   food.recognition.api=google-vision
   google.vision.api.key=your_actual_google_api_key_here
   ```

## Switching Between APIs

To switch from Nutritionix to Google Vision when you exceed limits:

1. **Update the configuration:**
   ```properties
   # Change this line in application.properties
   food.recognition.api=google-vision
   ```

2. **Restart your backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

## API Comparison

| Feature | Nutritionix | Google Vision |
|---------|-------------|---------------|
| **Food Database** | ✅ Large food database | ❌ General labels |
| **Nutrition Data** | ✅ Detailed nutrition | ❌ Basic mapping |
| **Accuracy** | ✅ High for food | ✅ High for images |
| **Free Tier** | 1,000 requests/month | 1,000 requests/month |
| **Setup Difficulty** | Easy | Medium |

## Testing Your Setup

1. **Start your backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Test the API:**
   ```bash
   # Test endpoint
   curl -X GET http://localhost:8081/api/food/test
   
   # Search for food
   curl -X GET "http://localhost:8081/api/food/search?query=apple"
   ```

3. **Test with frontend:**
   - Go to your food scanner page
   - Upload an image or take a photo
   - Check the console logs for API responses

## Troubleshooting

### Common Issues:

1. **"API key not found" error:**
   - Make sure you've added your API keys to `application.properties`
   - Restart the backend after adding keys

2. **"Rate limit exceeded" error:**
   - Switch to the other API by changing `food.recognition.api`
   - Or wait until next month for free tier reset

3. **"No food items found" error:**
   - Try with a clearer food image
   - Check if the image contains recognizable food items

### Debug Logs:

Check the backend logs for detailed information:
```bash
cd backend
mvn spring-boot:run
```

Look for logs like:
- "Using Nutritionix API for food recognition"
- "Analyzing food image using Nutritionix API..."
- "Food analysis completed. Found X items."

## Cost Management

- **Nutritionix:** Free for 1,000 requests/month
- **Google Vision:** Free for 1,000 requests/month
- **Total:** 2,000 free requests/month combined

When you approach limits, switch APIs to continue using the service.

## Next Steps

1. Set up your preferred API first (Nutritionix recommended)
2. Test with sample food images
3. Monitor your usage in the API provider's dashboard
4. Switch to backup API when needed

Your food scanner will now provide real, accurate food recognition instead of mock data! 