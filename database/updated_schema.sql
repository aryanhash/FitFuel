-- Updated Database Schema for New Design Flow
-- This script creates all necessary tables for the enhanced meal planner

-- Users table (enhanced with more preferences)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User preferences table (enhanced)
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Diet preferences
    diet_type VARCHAR(20) DEFAULT 'MIXED' CHECK (diet_type IN ('VEG', 'NON_VEG', 'VEGAN', 'KETO', 'PALEO', 'MIXED')),
    daily_calorie_target INTEGER DEFAULT 2000,
    
    -- Allergens and dislikes
    allergens TEXT[], -- Array of allergens
    dislikes TEXT[],  -- Array of disliked foods
    
    -- Meal preferences
    preferred_cuisines TEXT[], -- Array of preferred cuisines
    cooking_skill_level VARCHAR(20) DEFAULT 'INTERMEDIATE' CHECK (cooking_skill_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    
    -- Notifications
    meal_reminders BOOLEAN DEFAULT true,
    notification_time TIME DEFAULT '19:00:00',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id)
);

-- Recipes table (cached from external APIs)
CREATE TABLE IF NOT EXISTS recipes (
    id BIGSERIAL PRIMARY KEY,
    
    -- Basic recipe info
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL CHECK (category IN ('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK')),
    cuisine_type VARCHAR(50),
    diet_type VARCHAR(20) DEFAULT 'MIXED' CHECK (diet_type IN ('VEG', 'NON_VEG', 'VEGAN', 'KETO', 'PALEO', 'MIXED')),
    
    -- Cooking details
    prep_time INTEGER, -- in minutes
    cook_time INTEGER, -- in minutes
    total_time INTEGER, -- in minutes
    servings INTEGER DEFAULT 1,
    difficulty_level VARCHAR(20) DEFAULT 'MEDIUM' CHECK (difficulty_level IN ('EASY', 'MEDIUM', 'HARD')),
    
    -- Ingredients and instructions
    ingredients TEXT[], -- Array of ingredients
    instructions TEXT[], -- Array of cooking steps
    
    -- Nutritional information
    calories INTEGER,
    protein DECIMAL(5,2),
    carbs DECIMAL(5,2),
    fat DECIMAL(5,2),
    fiber DECIMAL(5,2),
    sugar DECIMAL(5,2),
    sodium DECIMAL(5,2),
    
    -- Media
    image_url VARCHAR(500),
    
    -- External API tracking
    source VARCHAR(50) DEFAULT 'local', -- 'edamam', 'spoonacular', 'local'
    external_id VARCHAR(255), -- ID from external API
    external_url VARCHAR(500), -- Link to original recipe
    
    -- Caching metadata
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User meal plans table (simplified)
CREATE TABLE IF NOT EXISTS user_meal_plans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    meal_type VARCHAR(20) NOT NULL CHECK (meal_type IN ('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK')),
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    
    -- User interactions
    is_favorite BOOLEAN DEFAULT false,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, date, meal_type)
);

-- User favorites table
CREATE TABLE IF NOT EXISTS user_favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, recipe_id)
);

-- User meal history table (for AI recommendations)
CREATE TABLE IF NOT EXISTS user_meal_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    date_consumed DATE NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI conversation history table
CREATE TABLE IF NOT EXISTS ai_conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id VARCHAR(255) NOT NULL,
    message_type VARCHAR(20) NOT NULL CHECK (message_type IN ('USER', 'AI')),
    content TEXT NOT NULL,
    context JSONB, -- Store any relevant context
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API usage tracking table
CREATE TABLE IF NOT EXISTS api_usage_logs (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(50) NOT NULL, -- 'edamam', 'spoonacular', 'openai'
    endpoint VARCHAR(255),
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    request_data JSONB,
    response_status INTEGER,
    response_time_ms INTEGER,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON user_preferences(user_id);
CREATE INDEX IF NOT EXISTS idx_recipes_category ON recipes(category);
CREATE INDEX IF NOT EXISTS idx_recipes_diet_type ON recipes(diet_type);
CREATE INDEX IF NOT EXISTS idx_recipes_source ON recipes(source);
CREATE INDEX IF NOT EXISTS idx_user_meal_plans_user_date ON user_meal_plans(user_id, date);
CREATE INDEX IF NOT EXISTS idx_user_favorites_user_id ON user_favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_user_meal_history_user_id ON user_meal_history(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_conversations_user_session ON ai_conversations(user_id, session_id);
CREATE INDEX IF NOT EXISTS idx_api_usage_logs_api_name ON api_usage_logs(api_name);
CREATE INDEX IF NOT EXISTS idx_api_usage_logs_created_at ON api_usage_logs(created_at);

-- Functions for updating timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers for updating timestamps
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_preferences_updated_at BEFORE UPDATE ON user_preferences FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_recipes_updated_at BEFORE UPDATE ON recipes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_meal_plans_updated_at BEFORE UPDATE ON user_meal_plans FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
