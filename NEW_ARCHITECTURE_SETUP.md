# New Architecture Setup Guide

## Overview
Your meal planner application has been completely restructured according to the new design flow. Here's what's been implemented:

## 🏗️ **New Architecture**

### **1. Enhanced Database Schema**
- **Users**: Basic user information
- **User Preferences**: Diet type, calorie targets, allergies, dislikes, cooking skill level
- **Recipes**: Cached from external APIs with full nutritional data
- **User Meal Plans**: Personal meal schedules with ratings and notes
- **User Favorites**: Saved favorite recipes
- **User Meal History**: Consumption tracking for AI recommendations
- **AI Conversations**: Chat history for context-aware responses
- **API Usage Logs**: Rate limiting and usage tracking

### **2. Service Layer Architecture**

#### **Enhanced Meal Service**
- Fetches meals from local database first
- Falls back to external APIs (Edamam, Spoonacular)
- Caches external recipes locally
- Filters by user preferences (allergies, dislikes, calorie targets)
- Supports advanced search and filtering

#### **Enhanced User Service**
- Manages user preferences and dietary constraints
- Handles favorites and meal history
- Provides dietary insights for AI recommendations
- Tracks meal ratings and feedback

#### **AI Service**
- Generates personalized meal plans using GPT
- Provides context-aware chatbot responses
- Analyzes eating patterns and provides insights
- Uses user history and preferences for recommendations

#### **Proxy Service**
- Securely calls external APIs (hides API keys)
- Implements rate limiting per user
- Logs all API usage for monitoring
- Handles API failures gracefully

### **3. New API Endpoints**

#### **Enhanced Meal Plan Controller** (`/api/v2/meal-plan/`)
- `GET /personalized/{userId}` - Get personalized meals
- `POST /ai-generate/{userId}` - AI-generated meal plans
- `GET /search/{userId}` - Advanced meal search
- `GET /recommendations/{userId}` - Personalized recommendations
- `POST /add-meal/{userId}` - Add meal to plan
- `GET /favorites/{userId}` - Get user favorites
- `POST /rate/{userId}` - Rate meals
- `GET /insights/{userId}` - Dietary insights
- `GET /analysis/{userId}` - Eating pattern analysis

#### **AI Chatbot Controller** (`/api/v2/chatbot/`)
- `POST /chat/{userId}` - Handle chat messages
- `POST /start-session/{userId}` - Start new chat session
- `GET /history/{userId}/{sessionId}` - Get chat history
- `GET /quick-suggestions/{userId}` - Get quick suggestions

#### **User Preferences Controller** (`/api/v2/user/`)
- `GET /preferences/{userId}` - Get user preferences
- `PUT /preferences/{userId}` - Update preferences
- `POST /create` - Create new user
- `GET /profile/{userId}` - Get complete user profile

## 🚀 **Setup Instructions**

### **1. Database Setup**
```bash
# Run the new schema
psql -U your_username -d your_database -f database/updated_schema.sql
```

### **2. API Keys Configuration**
Update `backend/src/main/resources/application.properties`:
```properties
# Edamam Recipe API
edamam.app.id=your-edamam-app-id
edamam.app.key=your-edamam-app-key

# Spoonacular API (optional)
spoonacular.api.key=your-spoonacular-key

# OpenAI API
openai.api.key=your-openai-key
```

### **3. Test the New Architecture**

#### **Create a User**
```bash
curl -X POST http://localhost:8081/api/v2/user/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "passwordHash": "hashedpassword"
  }'
```

#### **Set User Preferences**
```bash
curl -X PUT http://localhost:8081/api/v2/user/preferences/1 \
  -H "Content-Type: application/json" \
  -d '{
    "dietType": "VEG",
    "dailyCalorieTarget": 1800,
    "allergies": ["nuts", "dairy"],
    "preferredCuisines": ["Italian", "Mediterranean"],
    "cookingSkillLevel": "INTERMEDIATE"
  }'
```

#### **Get Personalized Meals**
```bash
curl "http://localhost:8081/api/v2/meal-plan/personalized/1?date=2025-01-15&mealType=BREAKFAST"
```

#### **Start AI Chat**
```bash
curl -X POST http://localhost:8081/api/v2/chatbot/start-session/1
```

#### **Chat with AI**
```bash
curl -X POST http://localhost:8081/api/v2/chatbot/chat/1 \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What should I eat for breakfast?",
    "sessionId": "your-session-id"
  }'
```

## 🔄 **Migration from Old System**

### **Data Migration**
1. **Users**: Existing users will work with new preferences system
2. **Recipes**: Old sample data can be migrated to new Recipe entity
3. **Meal Plans**: Old daily meal plans can be converted to user-specific plans

### **API Migration**
- Old endpoints (`/api/meal-plan/day`) still work for backward compatibility
- New endpoints (`/api/v2/meal-plan/personalized`) provide enhanced functionality
- Gradually migrate frontend to use new endpoints

## 🎯 **Key Benefits**

### **1. Personalization**
- User-specific meal recommendations
- Dietary constraint handling
- Preference-based filtering
- AI-powered suggestions

### **2. Scalability**
- External API integration for unlimited recipes
- Caching system for performance
- Rate limiting for API protection
- Modular service architecture

### **3. Intelligence**
- AI chatbot for nutrition advice
- Eating pattern analysis
- Personalized recommendations
- Context-aware responses

### **4. User Experience**
- Favorites and ratings system
- Meal history tracking
- Advanced search and filtering
- Real-time AI assistance

## 📊 **Monitoring and Analytics**

### **API Usage Tracking**
- Monitor external API usage
- Track user engagement
- Identify popular recipes
- Analyze eating patterns

### **Performance Metrics**
- Response times for each service
- Cache hit rates
- API success/failure rates
- User satisfaction scores

## 🔧 **Development Workflow**

### **Adding New Features**
1. **New External API**: Add to ProxyService
2. **New Meal Source**: Extend EnhancedMealService
3. **New AI Feature**: Extend AIService
4. **New User Feature**: Extend EnhancedUserService

### **Testing**
- Unit tests for each service
- Integration tests for API endpoints
- End-to-end tests for user workflows
- Performance tests for external API calls

## 🚀 **Next Steps**

1. **Set up API keys** for external services
2. **Run database migration** to new schema
3. **Test new endpoints** with sample data
4. **Update frontend** to use new APIs
5. **Monitor performance** and usage
6. **Add more external APIs** as needed

Your meal planner is now a sophisticated, AI-powered application with real-time recipe data, personalized recommendations, and intelligent chatbot assistance! 🎉
