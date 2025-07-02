# Cal AI Clone - Smart Meal Planner

A comprehensive meal planning application with AI-powered food recognition, nutrition tracking, and personalized meal recommendations.

## 🚀 Features

- **AI Food Recognition**: Upload food images and get instant nutrition analysis using GPT-4o Vision
- **Multiple Recognition APIs**: Support for Clarifai, Nutritionix, and Google Vision APIs
- **Indian Cuisine Focus**: Specialized recognition for Indian dishes
- **Meal Planning**: Create and manage personalized meal plans
- **Nutrition Tracking**: Track calories, protein, carbs, and fat
- **Recipe Management**: Store and organize your favorite recipes
- **Grocery Lists**: Generate shopping lists based on meal plans
- **User Authentication**: Secure login and registration system
- **Responsive Design**: Modern UI that works on all devices

## 🛠️ Tech Stack

### Backend
- **Spring Boot 3.2.0** - Java framework
- **PostgreSQL** - Database
- **Spring Security** - Authentication & authorization
- **JWT** - Token-based authentication
- **Spring Data JPA** - Data persistence
- **Maven** - Build tool

### Frontend
- **React 18** - UI framework
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **Axios** - HTTP client
- **React Router** - Navigation

### AI & APIs
- **OpenAI GPT-4o Vision** - Food image analysis
- **Clarifai** - Food recognition
- **Nutritionix** - Nutrition database
- **Google Vision API** - Image analysis (backup)
- **YouTube API** - Recipe videos

## 📋 Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd Cal-Ai-clone
```

### 2. Backend Setup

#### Database Setup
```bash
# Start PostgreSQL (if using Docker)
docker-compose up -d postgres

# Or install PostgreSQL locally and create database
createdb meal_planner
```

#### Environment Configuration
```bash
cd backend
cp env.example .env
```

Edit `.env` file with your API keys:
```env
# OpenAI API Configuration
OPENAI_API_KEY=your_openai_api_key
GPT4O_API_KEY=your_gpt4o_api_key

# Food Recognition APIs
CLARIFAI_API_KEY=your_clarifai_api_key
NUTRITIONIX_APP_ID=your_nutritionix_app_id
NUTRITIONIX_APP_KEY=your_nutritionix_app_key
GOOGLE_VISION_API_KEY=your_google_vision_api_key

# YouTube API
YOUTUBE_API_KEY=your_youtube_api_key

# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/meal_planner
DB_USERNAME=meal_user
DB_PASSWORD=meal_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here_make_it_long_and_secure_in_production
```

#### Start Backend
```bash
cd backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8081`

### 3. Frontend Setup

```bash
cd frontend
npm install
npm start
```

The frontend will start on `http://localhost:3000`

## 🔧 Configuration

### Food Recognition API Selection

In `backend/src/main/resources/application.properties`, you can choose which food recognition API to use:

```properties
# Options: "gpt4o", "enhanced", "clarifai", "nutritionix", "google-vision"
food.recognition.api=gpt4o
```

### API Endpoints

- **Backend API**: `http://localhost:8081`
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **Frontend**: `http://localhost:3000`

## 📱 Usage

1. **Register/Login**: Create an account or sign in
2. **Upload Food Images**: Use the food scanner to analyze meals
3. **Create Meal Plans**: Plan your weekly meals
4. **Track Nutrition**: Monitor your daily intake
5. **Manage Recipes**: Save and organize your favorite recipes
6. **Generate Grocery Lists**: Get shopping lists based on your meal plans

## 🔐 Security

- JWT-based authentication
- Password encryption
- CORS configuration
- Input validation
- API key protection

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📦 Deployment

### Backend Deployment
```bash
cd backend
mvn clean package
java -jar target/smart-meal-planner-1.0.0.jar
```

### Frontend Deployment
```bash
cd frontend
npm run build
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- OpenAI for GPT-4o Vision API
- Clarifai for food recognition
- Nutritionix for nutrition data
- Spring Boot team for the excellent framework
- React team for the amazing UI library

## 📞 Support

If you encounter any issues or have questions, please open an issue on GitHub.

---

**Note**: Make sure to keep your API keys secure and never commit them to version control. The `.env` file is already included in `.gitignore` to prevent accidental commits. 