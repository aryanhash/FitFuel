-- Initialize Meal Planner Database
-- This script creates all necessary tables for the multi-user meal planner

-- Create database if not exists (this will be handled by Docker)
-- CREATE DATABASE meal_planner;

-- Connect to the database
-- \c meal_planner;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    diet_preference VARCHAR(20) DEFAULT 'Mixed' CHECK (diet_preference IN ('Veg', 'Non-Veg', 'Mixed')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recipes table (shared across all users)
CREATE TABLE IF NOT EXISTS recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('Veg', 'Non-Veg')),
    category VARCHAR(50) NOT NULL CHECK (category IN ('Breakfast', 'Lunch', 'Dinner', 'Snack')),
    ingredients TEXT NOT NULL,
    instructions TEXT NOT NULL,
    calories INTEGER NOT NULL,
    protein DECIMAL(5,2),
    carbs DECIMAL(5,2),
    fat DECIMAL(5,2),
    fiber DECIMAL(5,2),
    tags TEXT[], -- Array of tags like ['high-protein', 'spicy', 'gluten-free']
    image_url VARCHAR(500),
    prep_time INTEGER, -- in minutes
    cook_time INTEGER, -- in minutes
    servings INTEGER DEFAULT 1,
    difficulty VARCHAR(20) DEFAULT 'Medium' CHECK (difficulty IN ('Easy', 'Medium', 'Hard')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User meal plans table
CREATE TABLE IF NOT EXISTS user_meal_plans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    meal_time VARCHAR(20) NOT NULL CHECK (meal_time IN ('Breakfast', 'Lunch', 'Dinner')),
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    year INTEGER NOT NULL,
    is_eaten BOOLEAN DEFAULT FALSE,
    is_skipped BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, date, meal_time, year)
);

-- Meal feedback table
CREATE TABLE IF NOT EXISTS meal_feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    meal_time VARCHAR(20) NOT NULL CHECK (meal_time IN ('Breakfast', 'Lunch', 'Dinner')),
    liked BOOLEAN,
    replaced_with_recipe_id BIGINT REFERENCES recipes(id),
    feedback_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User preferences table (for additional preferences beyond diet)
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    preference_key VARCHAR(100) NOT NULL,
    preference_value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, preference_key)
);

-- Recipe versions table (for tracking recipe modifications)
CREATE TABLE IF NOT EXISTS recipe_versions (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    ingredients TEXT NOT NULL,
    instructions TEXT NOT NULL,
    calories INTEGER NOT NULL,
    protein DECIMAL(5,2),
    carbs DECIMAL(5,2),
    fat DECIMAL(5,2),
    fiber DECIMAL(5,2),
    tags TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(recipe_id, version_number)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_recipes_type ON recipes(type);
CREATE INDEX IF NOT EXISTS idx_recipes_category ON recipes(category);
CREATE INDEX IF NOT EXISTS idx_recipes_tags ON recipes USING GIN(tags);
CREATE INDEX IF NOT EXISTS idx_user_meal_plans_user_date ON user_meal_plans(user_id, date);
CREATE INDEX IF NOT EXISTS idx_user_meal_plans_user_year ON user_meal_plans(user_id, year);
CREATE INDEX IF NOT EXISTS idx_meal_feedback_user_date ON meal_feedback(user_id, date);
CREATE INDEX IF NOT EXISTS idx_user_preferences_user_key ON user_preferences(user_id, preference_key);

-- Insert sample recipes for Veg diet
INSERT INTO recipes (name, type, category, ingredients, instructions, calories, protein, carbs, fat, fiber, tags, prep_time, cook_time, servings, difficulty) VALUES
-- Breakfast Recipes (Veg)
('Oatmeal with Berries', 'Veg', 'Breakfast', '["1 cup rolled oats", "1 cup almond milk", "1/2 cup mixed berries", "1 tbsp honey", "1 tbsp chia seeds"]', '["Cook oats with almond milk", "Top with berries and honey", "Sprinkle chia seeds"]', 320, 12.5, 52.3, 8.2, 8.5, ARRAY['high-fiber', 'quick', 'healthy'], 5, 10, 1, 'Easy'),
('Avocado Toast', 'Veg', 'Breakfast', '["2 slices whole grain bread", "1 ripe avocado", "1 tbsp olive oil", "Salt and pepper to taste", "Red pepper flakes"]', '["Toast bread", "Mash avocado", "Spread on toast", "Season with salt, pepper, and red pepper flakes"]', 280, 8.2, 28.5, 16.8, 7.2, ARRAY['quick', 'healthy', 'high-fiber'], 3, 5, 1, 'Easy'),
('Greek Yogurt Parfait', 'Veg', 'Breakfast', '["1 cup Greek yogurt", "1/4 cup granola", "1/2 cup mixed berries", "1 tbsp honey"]', '["Layer yogurt, granola, and berries", "Drizzle with honey"]', 250, 18.5, 32.1, 6.8, 4.2, ARRAY['high-protein', 'quick', 'healthy'], 5, 0, 1, 'Easy'),
('Spinach Smoothie Bowl', 'Veg', 'Breakfast', '["2 cups spinach", "1 frozen banana", "1/2 cup almond milk", "1 tbsp protein powder", "Toppings: granola, berries, nuts"]', '["Blend spinach, banana, milk, and protein powder", "Pour into bowl", "Add toppings"]', 220, 15.8, 28.4, 5.6, 6.8, ARRAY['high-protein', 'healthy', 'green'], 8, 0, 1, 'Easy'),

-- Lunch Recipes (Veg)
('Quinoa Buddha Bowl', 'Veg', 'Lunch', '["1 cup cooked quinoa", "1 cup chickpeas", "1 cup roasted vegetables", "2 tbsp tahini dressing", "1/4 cup avocado"]', '["Cook quinoa", "Roast vegetables", "Assemble bowl with all ingredients", "Drizzle with tahini dressing"]', 420, 18.5, 52.3, 16.8, 12.5, ARRAY['high-protein', 'healthy', 'balanced'], 15, 25, 1, 'Medium'),
('Mediterranean Wrap', 'Veg', 'Lunch', '["1 whole grain tortilla", "1/2 cup hummus", "1 cup mixed vegetables", "1/4 cup feta cheese", "1 tbsp olive oil"]', '["Spread hummus on tortilla", "Add vegetables and feta", "Roll tightly", "Cut diagonally"]', 380, 14.2, 42.1, 18.5, 8.9, ARRAY['quick', 'portable', 'mediterranean'], 10, 5, 1, 'Easy'),
('Lentil Soup', 'Veg', 'Lunch', '["1 cup red lentils", "1 onion", "2 carrots", "2 celery stalks", "4 cups vegetable broth", "Spices: cumin, turmeric, salt"]', '["Sauté vegetables", "Add lentils and broth", "Simmer until lentils are tender", "Season with spices"]', 320, 18.5, 52.3, 2.1, 16.8, ARRAY['high-protein', 'soup', 'comfort'], 10, 30, 2, 'Easy'),
('Caprese Salad', 'Veg', 'Lunch', '["2 large tomatoes", "1 ball fresh mozzarella", "Fresh basil leaves", "2 tbsp balsamic glaze", "1 tbsp olive oil"]', '["Slice tomatoes and mozzarella", "Arrange with basil", "Drizzle with balsamic and olive oil"]', 280, 12.5, 8.2, 18.5, 3.2, ARRAY['fresh', 'italian', 'quick'], 8, 0, 1, 'Easy'),

-- Dinner Recipes (Veg)
('Vegetable Stir Fry', 'Veg', 'Dinner', '["2 cups mixed vegetables", "1 cup tofu", "2 tbsp soy sauce", "1 tbsp sesame oil", "1 cup brown rice"]', '["Cook rice", "Stir fry vegetables and tofu", "Add soy sauce and sesame oil", "Serve over rice"]', 380, 16.8, 52.3, 12.5, 8.9, ARRAY['asian', 'quick', 'healthy'], 15, 20, 1, 'Medium'),
('Mushroom Risotto', 'Veg', 'Dinner', '["1 cup arborio rice", "2 cups mushrooms", "4 cups vegetable broth", "1/2 cup parmesan cheese", "1 tbsp butter"]', '["Sauté mushrooms", "Add rice and broth gradually", "Stir until creamy", "Add parmesan and butter"]', 420, 14.2, 68.5, 12.8, 4.2, ARRAY['italian', 'creamy', 'comfort'], 10, 30, 2, 'Medium'),
('Stuffed Bell Peppers', 'Veg', 'Dinner', '["4 bell peppers", "1 cup quinoa", "1 cup black beans", "1 cup corn", "1 cup tomato sauce"]', '["Cook quinoa", "Mix with beans and corn", "Stuff peppers", "Bake with tomato sauce"]', 350, 16.8, 52.3, 8.5, 12.5, ARRAY['mexican', 'healthy', 'balanced'], 20, 35, 4, 'Medium'),
('Pasta Primavera', 'Veg', 'Dinner', '["8 oz whole grain pasta", "2 cups mixed vegetables", "2 tbsp olive oil", "1/4 cup parmesan cheese", "Fresh herbs"]', '["Cook pasta", "Sauté vegetables", "Combine with pasta", "Add cheese and herbs"]', 380, 14.2, 58.5, 12.8, 8.9, ARRAY['italian', 'quick', 'pasta'], 10, 15, 2, 'Easy');

-- Insert sample recipes for Non-Veg diet
INSERT INTO recipes (name, type, category, ingredients, instructions, calories, protein, carbs, fat, fiber, tags, prep_time, cook_time, servings, difficulty) VALUES
-- Breakfast Recipes (Non-Veg)
('Eggs Benedict', 'Non-Veg', 'Breakfast', '["2 eggs", "2 slices ham", "1 English muffin", "Hollandaise sauce", "Fresh herbs"]', '["Poach eggs", "Toast muffin", "Assemble with ham and eggs", "Top with hollandaise"]', 420, 22.5, 28.3, 28.2, 2.5, ARRAY['classic', 'protein-rich', 'brunch'], 10, 15, 1, 'Medium'),
('Chicken Sausage Omelette', 'Non-Veg', 'Breakfast', '["3 eggs", "1 chicken sausage", "1/4 cup cheese", "1/4 cup vegetables", "1 tbsp butter"]', '["Beat eggs", "Cook sausage", "Make omelette with cheese and vegetables"]', 380, 28.5, 8.2, 26.8, 3.2, ARRAY['high-protein', 'quick', 'savory'], 8, 12, 1, 'Easy'),
('Salmon Bagel', 'Non-Veg', 'Breakfast', '["1 whole grain bagel", "2 oz smoked salmon", "2 tbsp cream cheese", "Red onion", "Capers"]', '["Toast bagel", "Spread cream cheese", "Add salmon, onion, and capers"]', 320, 18.5, 42.1, 12.8, 4.2, ARRAY['omega-3', 'protein-rich', 'quick'], 5, 3, 1, 'Easy'),
('Turkey Breakfast Burrito', 'Non-Veg', 'Breakfast', '["2 eggs", "2 oz turkey", "1 tortilla", "1/4 cup cheese", "Salsa"]', '["Scramble eggs with turkey", "Wrap in tortilla with cheese", "Top with salsa"]', 350, 24.2, 28.5, 16.8, 3.8, ARRAY['portable', 'protein-rich', 'mexican'], 8, 10, 1, 'Easy'),

-- Lunch Recipes (Non-Veg)
('Grilled Chicken Salad', 'Non-Veg', 'Lunch', '["4 oz grilled chicken", "2 cups mixed greens", "1/4 cup nuts", "1/4 cup dried cranberries", "Balsamic vinaigrette"]', '["Grill chicken", "Assemble salad", "Add nuts and cranberries", "Dress with vinaigrette"]', 380, 32.5, 18.2, 22.8, 6.5, ARRAY['high-protein', 'healthy', 'grilled'], 15, 20, 1, 'Easy'),
('Tuna Sandwich', 'Non-Veg', 'Lunch', '["1 can tuna", "2 slices whole grain bread", "1/4 cup mayonnaise", "1/4 cup celery", "1 tbsp mustard"]', '["Mix tuna with mayonnaise, celery, and mustard", "Spread on bread"]', 320, 28.5, 32.1, 12.8, 4.2, ARRAY['quick', 'protein-rich', 'portable'], 8, 0, 1, 'Easy'),
('Beef Stir Fry', 'Non-Veg', 'Lunch', '["4 oz beef strips", "2 cups vegetables", "2 tbsp soy sauce", "1 tbsp sesame oil", "1 cup brown rice"]', '["Stir fry beef", "Add vegetables", "Season with soy sauce", "Serve over rice"]', 420, 28.5, 42.3, 18.5, 6.8, ARRAY['asian', 'high-protein', 'quick'], 15, 20, 1, 'Medium'),
('Shrimp Pasta', 'Non-Veg', 'Lunch', '["6 oz shrimp", "8 oz pasta", "2 tbsp olive oil", "2 cloves garlic", "Fresh herbs"]', '["Cook pasta", "Sauté shrimp with garlic", "Combine with pasta", "Add herbs"]', 380, 24.2, 48.5, 12.8, 4.2, ARRAY['seafood', 'pasta', 'quick'], 10, 15, 1, 'Easy'),

-- Dinner Recipes (Non-Veg)
('Grilled Salmon', 'Non-Veg', 'Dinner', '["6 oz salmon fillet", "1 cup quinoa", "2 cups roasted vegetables", "Lemon herb butter"]', '["Grill salmon", "Cook quinoa", "Roast vegetables", "Serve with lemon herb butter"]', 480, 32.5, 42.3, 22.8, 8.5, ARRAY['omega-3', 'high-protein', 'healthy'], 15, 25, 1, 'Medium'),
('Chicken Marsala', 'Non-Veg', 'Dinner', '["6 oz chicken breast", "1/2 cup marsala wine", "1 cup mushrooms", "1 cup pasta", "Fresh herbs"]', '["Cook chicken", "Make marsala sauce", "Serve with pasta"]', 420, 28.5, 38.2, 18.5, 4.2, ARRAY['italian', 'elegant', 'protein-rich'], 20, 30, 1, 'Medium'),
('Beef Tacos', 'Non-Veg', 'Dinner', '["6 oz ground beef", "4 corn tortillas", "1 cup lettuce", "1/2 cup cheese", "Salsa and sour cream"]', '["Cook beef with taco seasoning", "Warm tortillas", "Assemble tacos with toppings"]', 450, 28.5, 32.1, 24.8, 6.5, ARRAY['mexican', 'fun', 'protein-rich'], 15, 20, 2, 'Easy'),
('Lamb Curry', 'Non-Veg', 'Dinner', '["8 oz lamb", "1 cup rice", "2 cups curry sauce", "Fresh cilantro", "Naan bread"]', '["Cook lamb in curry sauce", "Serve with rice", "Garnish with cilantro", "Serve with naan"]', 520, 32.5, 48.2, 28.5, 6.8, ARRAY['indian', 'spicy', 'rich'], 20, 45, 2, 'Hard');

-- Create a function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers to automatically update the updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_recipes_updated_at BEFORE UPDATE ON recipes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_meal_plans_updated_at BEFORE UPDATE ON user_meal_plans FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_preferences_updated_at BEFORE UPDATE ON user_preferences FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO meal_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO meal_user; 