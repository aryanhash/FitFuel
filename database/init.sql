-- Smart Meal Planner Database Schema
-- Initialize database with tables and sample data

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    height_cm INTEGER,
    weight_kg DECIMAL(5,2),
    activity_level VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User profiles for meal planning preferences
CREATE TABLE IF NOT EXISTS user_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    body_goal VARCHAR(50) NOT NULL, -- 'fat_loss', 'muscle_gain', 'detox', 'maintenance'
    food_preference VARCHAR(50) NOT NULL, -- 'vegetarian', 'vegan', 'eggs', 'non_vegetarian'
    fasting_window_start TIME,
    fasting_window_end TIME,
    workout_days TEXT[], -- Array of days: ['monday', 'wednesday', 'friday']
    daily_calorie_target INTEGER,
    protein_target_g INTEGER,
    carbs_target_g INTEGER,
    fat_target_g INTEGER,
    allergies TEXT[],
    dislikes TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Food items database
CREATE TABLE IF NOT EXISTS food_items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL, -- 'protein', 'carbs', 'fats', 'vegetables', 'fruits'
    calories_per_100g INTEGER NOT NULL,
    protein_g DECIMAL(5,2),
    carbs_g DECIMAL(5,2),
    fat_g DECIMAL(5,2),
    fiber_g DECIMAL(5,2),
    sugar_g DECIMAL(5,2),
    sodium_mg INTEGER,
    is_vegetarian BOOLEAN DEFAULT true,
    is_vegan BOOLEAN DEFAULT true,
    is_gluten_free BOOLEAN DEFAULT true,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recipes table
CREATE TABLE IF NOT EXISTS recipes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    prep_time_minutes INTEGER,
    cook_time_minutes INTEGER,
    servings INTEGER,
    difficulty_level VARCHAR(20), -- 'easy', 'medium', 'hard'
    cuisine_type VARCHAR(100),
    meal_type VARCHAR(50), -- 'breakfast', 'lunch', 'dinner', 'snack'
    calories_per_serving INTEGER,
    protein_g DECIMAL(5,2),
    carbs_g DECIMAL(5,2),
    fat_g DECIMAL(5,2),
    instructions TEXT,
    image_url VARCHAR(500),
    video_url VARCHAR(500),
    is_vegetarian BOOLEAN DEFAULT true,
    is_vegan BOOLEAN DEFAULT true,
    tags TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recipe ingredients (many-to-many relationship)
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    id SERIAL PRIMARY KEY,
    recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
    food_item_id INTEGER REFERENCES food_items(id) ON DELETE CASCADE,
    quantity DECIMAL(8,2) NOT NULL,
    unit VARCHAR(50) NOT NULL, -- 'g', 'kg', 'ml', 'l', 'pieces', 'cups'
    notes VARCHAR(255)
);

-- Meal plans
CREATE TABLE IF NOT EXISTS meal_plans (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_calories INTEGER,
    total_protein_g DECIMAL(6,2),
    total_carbs_g DECIMAL(6,2),
    total_fat_g DECIMAL(6,2),
    status VARCHAR(20) DEFAULT 'active', -- 'active', 'completed', 'archived'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Meal plan days
CREATE TABLE IF NOT EXISTS meal_plan_days (
    id SERIAL PRIMARY KEY,
    meal_plan_id INTEGER REFERENCES meal_plans(id) ON DELETE CASCADE,
    day_of_week VARCHAR(20) NOT NULL, -- 'monday', 'tuesday', etc.
    date DATE NOT NULL,
    total_calories INTEGER,
    total_protein_g DECIMAL(6,2),
    total_carbs_g DECIMAL(6,2),
    total_fat_g DECIMAL(6,2)
);

-- Meal plan meals
CREATE TABLE IF NOT EXISTS meal_plan_meals (
    id SERIAL PRIMARY KEY,
    meal_plan_day_id INTEGER REFERENCES meal_plan_days(id) ON DELETE CASCADE,
    meal_type VARCHAR(50) NOT NULL, -- 'breakfast', 'lunch', 'dinner', 'snack'
    recipe_id INTEGER REFERENCES recipes(id),
    custom_meal_name VARCHAR(255),
    custom_meal_description TEXT,
    calories INTEGER,
    protein_g DECIMAL(5,2),
    carbs_g DECIMAL(5,2),
    fat_g DECIMAL(5,2),
    meal_time TIME,
    notes TEXT
);

-- Grocery lists
CREATE TABLE IF NOT EXISTS grocery_lists (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    meal_plan_id INTEGER REFERENCES meal_plans(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Grocery list items
CREATE TABLE IF NOT EXISTS grocery_list_items (
    id SERIAL PRIMARY KEY,
    grocery_list_id INTEGER REFERENCES grocery_lists(id) ON DELETE CASCADE,
    food_item_id INTEGER REFERENCES food_items(id),
    custom_item_name VARCHAR(255),
    quantity DECIMAL(8,2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    category VARCHAR(100), -- 'produce', 'dairy', 'meat', 'pantry', etc.
    is_checked BOOLEAN DEFAULT false,
    notes TEXT
);

-- AI chat conversations
CREATE TABLE IF NOT EXISTS chat_conversations (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI chat messages
CREATE TABLE IF NOT EXISTS chat_messages (
    id SERIAL PRIMARY KEY,
    conversation_id INTEGER REFERENCES chat_conversations(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    is_user_message BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User progress tracking
CREATE TABLE IF NOT EXISTS user_progress (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    weight_kg DECIMAL(5,2),
    body_fat_percentage DECIMAL(4,2),
    muscle_mass_kg DECIMAL(5,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_food_items_category ON food_items(category);
CREATE INDEX idx_recipes_meal_type ON recipes(meal_type);
CREATE INDEX idx_meal_plans_user_id ON meal_plans(user_id);
CREATE INDEX idx_meal_plans_dates ON meal_plans(start_date, end_date);
CREATE INDEX idx_grocery_lists_user_id ON grocery_lists(user_id);
CREATE INDEX idx_chat_conversations_user_id ON chat_conversations(user_id);

-- Insert sample food items
INSERT INTO food_items (name, category, calories_per_100g, protein_g, carbs_g, fat_g, fiber_g, sugar_g, sodium_mg, is_vegetarian, is_vegan, is_gluten_free) VALUES
('Chicken Breast', 'protein', 165, 31.0, 0.0, 3.6, 0.0, 0.0, 74, false, false, true),
('Salmon', 'protein', 208, 25.0, 0.0, 12.0, 0.0, 0.0, 59, false, false, true),
('Eggs', 'protein', 155, 12.6, 1.1, 10.6, 0.0, 1.1, 124, true, false, true),
('Greek Yogurt', 'protein', 59, 10.0, 3.6, 0.4, 0.0, 3.2, 36, true, false, true),
('Quinoa', 'carbs', 120, 4.4, 21.3, 1.9, 2.8, 0.9, 7, true, true, true),
('Brown Rice', 'carbs', 111, 2.6, 23.0, 0.9, 1.8, 0.4, 5, true, true, true),
('Sweet Potato', 'carbs', 86, 1.6, 20.1, 0.1, 3.0, 4.2, 55, true, true, true),
('Broccoli', 'vegetables', 34, 2.8, 7.0, 0.4, 2.6, 1.5, 33, true, true, true),
('Spinach', 'vegetables', 23, 2.9, 3.6, 0.4, 2.2, 0.4, 79, true, true, true),
('Avocado', 'fats', 160, 2.0, 8.5, 14.7, 6.7, 0.7, 7, true, true, true),
('Almonds', 'fats', 579, 21.2, 21.7, 49.9, 12.5, 4.4, 1, true, true, true),
('Banana', 'fruits', 89, 1.1, 22.8, 0.3, 2.6, 12.2, 1, true, true, true),
('Blueberries', 'fruits', 57, 0.7, 14.5, 0.3, 2.4, 10.0, 1, true, true, true);

-- Insert sample recipes
INSERT INTO recipes (name, description, prep_time_minutes, cook_time_minutes, servings, difficulty_level, cuisine_type, meal_type, calories_per_serving, protein_g, carbs_g, fat_g, instructions, is_vegetarian, is_vegan, tags) VALUES
('Protein Smoothie Bowl', 'A delicious and nutritious smoothie bowl packed with protein and antioxidants', 10, 0, 1, 'easy', 'healthy', 'breakfast', 320, 25.0, 35.0, 12.0, '1. Blend frozen berries, protein powder, and almond milk\n2. Pour into a bowl\n3. Top with granola, fresh fruits, and nuts', true, true, ARRAY['protein', 'breakfast', 'smoothie']),
('Grilled Chicken Salad', 'Fresh salad with grilled chicken breast and mixed greens', 15, 20, 2, 'easy', 'mediterranean', 'lunch', 280, 35.0, 8.0, 12.0, '1. Grill chicken breast until cooked through\n2. Mix salad greens, vegetables, and dressing\n3. Top with sliced chicken', false, false, ARRAY['protein', 'salad', 'lunch']),
('Quinoa Buddha Bowl', 'Nutritious bowl with quinoa, roasted vegetables, and tahini dressing', 20, 25, 2, 'medium', 'healthy', 'dinner', 420, 15.0, 45.0, 18.0, '1. Cook quinoa according to package instructions\n2. Roast vegetables in the oven\n3. Assemble bowl with quinoa, vegetables, and dressing', true, true, ARRAY['vegetarian', 'bowl', 'dinner']);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply trigger to tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_profiles_updated_at BEFORE UPDATE ON user_profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column(); 