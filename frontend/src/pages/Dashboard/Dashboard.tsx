import React, { useState, useEffect } from 'react';
import { 
  Clock, 
  TrendingUp, 
  Activity, 
  Target, 
  CheckCircle, 
  Circle
} from 'lucide-react';
import FoodScannerCard from '../../components/FoodScanner/FoodScannerCard';

const Dashboard: React.FC = () => {
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const todayMeals = [
    {
      id: 1,
      name: 'Oatmeal with Berries',
      time: '8:00 AM',
      calories: 320,
      completed: true,
      image: 'https://images.unsplash.com/photo-1517686469429-8bdb88b9f907?w=150&h=150&fit=crop'
    },
    {
      id: 2,
      name: 'Grilled Chicken Salad',
      time: '12:30 PM',
      calories: 450,
      completed: false,
      image: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=150&h=150&fit=crop'
    },
    {
      id: 3,
      name: 'Salmon with Vegetables',
      time: '7:00 PM',
      calories: 580,
      completed: false,
      image: 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=150&h=150&fit=crop'
    }
  ];

  const workoutSchedule = [
    { day: 'Monday', type: 'Cardio', time: '6:00 AM', completed: true },
    { day: 'Tuesday', type: 'Strength', time: '6:00 AM', completed: false },
    { day: 'Wednesday', type: 'Yoga', time: '6:00 AM', completed: false },
    { day: 'Thursday', type: 'Cardio', time: '6:00 AM', completed: false },
    { day: 'Friday', type: 'Strength', time: '6:00 AM', completed: false },
  ];

  const totalCalories = todayMeals.reduce((sum, meal) => sum + meal.calories, 0);
  const completedMeals = todayMeals.filter(meal => meal.completed).length;
  const progressPercentage = (completedMeals / todayMeals.length) * 100;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Today's Plan</h1>
          <p className="text-gray-600">
            {currentTime.toLocaleDateString('en-US', { 
              weekday: 'long', 
              year: 'numeric', 
              month: 'long', 
              day: 'numeric' 
            })}
          </p>
        </div>
        <div className="flex items-center space-x-2 text-sm text-gray-500">
          <Clock className="h-4 w-4" />
          <span>{currentTime.toLocaleTimeString()}</span>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Meal Progress */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-semibold text-gray-900">Today's Meals</h2>
              <div className="flex items-center space-x-2">
                <span className="text-sm text-gray-600">Progress</span>
                <span className="text-sm font-medium text-primary-600">
                  {completedMeals}/{todayMeals.length}
                </span>
              </div>
            </div>

            {/* Progress Bar */}
            <div className="mb-6">
              <div className="flex justify-between text-sm text-gray-600 mb-2">
                <span>Meal Completion</span>
                <span>{Math.round(progressPercentage)}%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-gradient-to-r from-primary-500 to-secondary-500 h-2 rounded-full transition-all duration-300"
                  style={{ width: `${progressPercentage}%` }}
                ></div>
              </div>
            </div>

            {/* Meals List */}
            <div className="space-y-4">
              {todayMeals.map((meal) => (
                <div key={meal.id} className="flex items-center space-x-4 p-4 bg-gray-50 rounded-lg">
                  <img 
                    src={meal.image} 
                    alt={meal.name}
                    className="w-12 h-12 rounded-lg object-cover"
                  />
                  <div className="flex-1">
                    <h3 className="font-medium text-gray-900">{meal.name}</h3>
                    <p className="text-sm text-gray-600">{meal.time} • {meal.calories} cal</p>
                  </div>
                  <div className="flex items-center space-x-2">
                    {meal.completed ? (
                      <CheckCircle className="h-5 w-5 text-success-500" />
                    ) : (
                      <Circle className="h-5 w-5 text-gray-300" />
                    )}
                  </div>
                </div>
              ))}
            </div>

            <div className="mt-6 pt-4 border-t border-gray-200">
              <div className="flex justify-between items-center">
                <span className="text-sm text-gray-600">Total Calories</span>
                <span className="text-lg font-semibold text-gray-900">{totalCalories} cal</span>
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Food Scanner */}
          <FoodScannerCard />

          {/* Workout Schedule */}
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">This Week's Workouts</h3>
            <div className="space-y-3">
              {workoutSchedule.map((workout, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">{workout.day}</p>
                    <p className="text-sm text-gray-600">{workout.type} • {workout.time}</p>
                  </div>
                  {workout.completed ? (
                    <CheckCircle className="h-5 w-5 text-success-500" />
                  ) : (
                    <Circle className="h-5 w-5 text-gray-300" />
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Quick Stats */}
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Stats</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className="p-2 bg-primary-100 rounded-lg">
                    <TrendingUp className="h-4 w-4 text-primary-600" />
                  </div>
                  <span className="text-sm text-gray-600">Weekly Progress</span>
                </div>
                <span className="text-sm font-medium text-gray-900">+2.3 lbs</span>
              </div>
              
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className="p-2 bg-success-100 rounded-lg">
                    <Activity className="h-4 w-4 text-success-600" />
                  </div>
                  <span className="text-sm text-gray-600">Workouts</span>
                </div>
                <span className="text-sm font-medium text-gray-900">3/5</span>
              </div>
              
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className="p-2 bg-warning-100 rounded-lg">
                    <Target className="h-4 w-4 text-warning-600" />
                  </div>
                  <span className="text-sm text-gray-600">Calorie Goal</span>
                </div>
                <span className="text-sm font-medium text-gray-900">1,350/1,800</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard; 