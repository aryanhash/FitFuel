import React, { useState, useRef } from 'react';
import { Camera, Upload, Search, X, Zap, Plus } from 'lucide-react';

interface FoodItem {
  name: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  confidence: number;
}

const FoodScannerCard: React.FC = () => {
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>('');
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [results, setResults] = useState<FoodItem[]>([]);
  const [error, setError] = useState<string>('');
  const [showScanner, setShowScanner] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedImage(file);
      setPreviewUrl(URL.createObjectURL(file));
      setResults([]);
      setError('');
    }
  };

  const analyzeFood = async () => {
    if (!selectedImage) return;

    setIsAnalyzing(true);
    setError('');

    try {
      const formData = new FormData();
      formData.append('image', selectedImage);

      const response = await fetch('http://localhost:8081/api/food/analyze', {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Failed to analyze image');
      }

      const data = await response.json();
      setResults(data);
    } catch (err) {
      console.error('Error analyzing food:', err);
      setError('Failed to analyze food image. Please try again.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  const resetScanner = () => {
    setSelectedImage(null);
    setPreviewUrl('');
    setResults([]);
    setError('');
    setShowScanner(false);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const addToMealPlan = (food: FoodItem) => {
    // TODO: Integrate with meal planner
    alert(`Added ${food.name} to your meal plan!`);
  };

  const totalCalories = results.reduce((sum, food) => sum + food.calories, 0);

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900">Scan Your Food</h3>
        <div className="p-2 bg-primary-100 rounded-lg">
          <Zap className="h-4 w-4 text-primary-600" />
        </div>
      </div>

      {!showScanner ? (
        <div className="text-center">
          <div className="mb-4">
            <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-3">
              <Camera className="h-8 w-8 text-primary-600" />
            </div>
            <p className="text-sm text-gray-600 mb-4">
              Take a photo or upload an image to identify food and get nutritional information
            </p>
          </div>
          
          <button
            onClick={() => setShowScanner(true)}
            className="w-full bg-primary-500 text-white px-4 py-2 rounded-lg hover:bg-primary-600 transition-colors flex items-center justify-center space-x-2"
          >
            <Plus className="h-4 w-4" />
            <span>Start Scanning</span>
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {/* Upload Section */}
          {!selectedImage && (
            <div className="text-center">
              <button
                onClick={() => fileInputRef.current?.click()}
                className="w-full p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
              >
                <Upload className="w-8 h-8 text-gray-400 mx-auto mb-2" />
                <span className="text-sm text-gray-600">Upload Food Image</span>
              </button>
              
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleImageUpload}
                className="hidden"
              />
            </div>
          )}

          {/* Image Preview */}
          {selectedImage && (
            <div className="space-y-3">
              <div className="relative">
                <img
                  src={previewUrl}
                  alt="Food preview"
                  className="w-full h-32 object-cover rounded-lg"
                />
                <button
                  onClick={resetScanner}
                  className="absolute top-2 right-2 p-1 bg-black bg-opacity-50 text-white rounded-full hover:bg-opacity-70"
                >
                  <X className="h-4 w-4" />
                </button>
              </div>
              
              <button
                onClick={analyzeFood}
                disabled={isAnalyzing}
                className="w-full bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
              >
                {isAnalyzing ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    <span>Analyzing...</span>
                  </>
                ) : (
                  <>
                    <Search className="h-4 w-4" />
                    <span>Analyze Food</span>
                  </>
                )}
              </button>
              
              {error && (
                <p className="text-red-500 text-xs text-center">{error}</p>
              )}
            </div>
          )}

          {/* Results */}
          {results.length > 0 && (
            <div className="space-y-3">
              <div className="text-center">
                <p className="text-sm font-medium text-gray-900">Detected Foods</p>
                <p className="text-xs text-gray-600">Total: {totalCalories} calories</p>
              </div>
              
              <div className="space-y-2 max-h-32 overflow-y-auto">
                {results.map((food, index) => (
                  <div
                    key={index}
                    className="p-2 bg-gray-50 rounded-lg text-xs"
                  >
                    <div className="flex justify-between items-start mb-1">
                      <span className="font-medium text-gray-900">{food.name}</span>
                      <button
                        onClick={() => addToMealPlan(food)}
                        className="text-primary-600 hover:text-primary-700"
                      >
                        <Plus className="h-3 w-3" />
                      </button>
                    </div>
                    <div className="flex justify-between text-gray-600">
                      <span>{food.calories} cal</span>
                      <span>{(food.confidence * 100).toFixed(0)}% match</span>
                    </div>
                  </div>
                ))}
              </div>
              
              <button
                onClick={resetScanner}
                className="w-full text-sm text-gray-600 hover:text-gray-800 py-1"
              >
                Scan Another Food
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default FoodScannerCard; 