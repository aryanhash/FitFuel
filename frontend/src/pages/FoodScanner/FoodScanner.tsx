import React, { useState, useRef, useEffect } from 'react';
import { Camera, Upload, Search, X, Zap, Plus } from 'lucide-react';

interface FoodItem {
  name: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  confidence: number;
}

const FoodScanner: React.FC = () => {
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>('');
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [results, setResults] = useState<FoodItem[]>([]);
  const [error, setError] = useState<string>('');
  const [showCamera, setShowCamera] = useState(false);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [searchResults, setSearchResults] = useState<FoodItem[]>([]);
  const [isCameraLoading, setIsCameraLoading] = useState(false);
  const [cameraError, setCameraError] = useState<string>('');
  const [isCameraReady, setIsCameraReady] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const videoRef = useRef<HTMLVideoElement>(null);
  const streamRef = useRef<MediaStream | null>(null);

  // useEffect to handle video stream setup when camera modal opens
  useEffect(() => {
    if (showCamera && videoRef.current && streamRef.current) {
      console.log('Setting video stream to video element');
      videoRef.current.srcObject = streamRef.current;
      
      // Ensure video plays
      videoRef.current.play().catch(err => {
        console.error('Error playing video:', err);
        setCameraError('Failed to start video playback');
      });
    }
  }, [showCamera]);

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

  const searchFood = async () => {
    if (!searchQuery.trim()) return;

    try {
      const response = await fetch(`http://localhost:8081/api/food/search?query=${encodeURIComponent(searchQuery)}`);
      
      if (!response.ok) {
        throw new Error('Failed to search for food');
      }

      const data = await response.json();
      setSearchResults(data);
    } catch (err) {
      console.error('Error searching food:', err);
      setError('Failed to search for food. Please try again.');
    }
  };

  const startCamera = async () => {
    setIsCameraLoading(true);
    setCameraError('');
    setIsCameraReady(false);
    setError('');
    
    try {
      console.log('Requesting camera access...');
      const stream = await navigator.mediaDevices.getUserMedia({
        video: {
          facingMode: 'environment', // Use back camera if available
          width: { ideal: 1280 },
          height: { ideal: 720 }
        }
      });
      
      console.log('Camera stream obtained:', stream);
      streamRef.current = stream;
      
      // Show camera modal first, then useEffect will set the stream
      setShowCamera(true);
      
    } catch (err) {
      console.error('Error accessing camera:', err);
      const errorMessage = err instanceof Error ? err.message : 'Unknown camera error';
      setCameraError(`Camera access failed: ${errorMessage}`);
      
      // Try front camera as fallback
      try {
        console.log('Trying front camera as fallback...');
        const fallbackStream = await navigator.mediaDevices.getUserMedia({
          video: {
            facingMode: 'user',
            width: { ideal: 1280 },
            height: { ideal: 720 }
          }
        });
        
        console.log('Front camera stream obtained:', fallbackStream);
        streamRef.current = fallbackStream;
        setShowCamera(true);
        setCameraError('');
        
      } catch (fallbackErr) {
        console.error('Front camera also failed:', fallbackErr);
        setCameraError('Both back and front cameras failed. Please check permissions.');
      }
    } finally {
      setIsCameraLoading(false);
    }
  };

  const stopCamera = () => {
    console.log('Stopping camera...');
    if (streamRef.current) {
      streamRef.current.getTracks().forEach(track => {
        track.stop();
        console.log('Camera track stopped:', track.kind);
      });
      streamRef.current = null;
    }
    setShowCamera(false);
    setCameraError('');
    setIsCameraReady(false);
  };

  const capturePhoto = () => {
    console.log('Capture photo called');
    
    if (!videoRef.current) {
      setError('Video element not available. Please try again.');
      return;
    }

    if (videoRef.current.videoWidth === 0 || videoRef.current.videoHeight === 0) {
      setError('Camera not ready. Please wait a moment and try again.');
      return;
    }

    if (videoRef.current.readyState < 2) { // HAVE_CURRENT_DATA
      setError('Camera stream not ready. Please wait and try again.');
      return;
    }

    try {
      console.log('Creating canvas for photo capture');
      const canvas = document.createElement('canvas');
      const context = canvas.getContext('2d');
      
      if (!context) {
        setError('Failed to create canvas context');
        return;
      }

      // Set canvas dimensions to match video
      canvas.width = videoRef.current.videoWidth;
      canvas.height = videoRef.current.videoHeight;
      
      console.log('Canvas dimensions:', canvas.width, 'x', canvas.height);
      
      // Draw video frame to canvas
      context.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
      
      // Convert canvas to blob
      canvas.toBlob((blob) => {
        if (blob) {
          console.log('Photo captured successfully, blob size:', blob.size);
          const file = new File([blob], 'captured-photo.jpg', { type: 'image/jpeg' });
          setSelectedImage(file);
          
          // Create preview URL
          const url = URL.createObjectURL(blob);
          setPreviewUrl(url);
          
          // Close camera
          stopCamera();
          
          // Clear any previous errors
          setError('');
        } else {
          setError('Failed to capture photo. Please try again.');
        }
      }, 'image/jpeg', 0.8);
      
    } catch (err) {
      console.error('Error capturing photo:', err);
      setError('Failed to capture photo. Please try again.');
    }
  };

  const resetScanner = () => {
    setSelectedImage(null);
    setPreviewUrl('');
    setResults([]);
    setError('');
    setSearchQuery('');
    setSearchResults([]);
    setCameraError('');
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
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Food Scanner</h1>
        <p className="text-gray-600">
          Take a photo or upload an image to identify food and get nutritional information
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Image Upload/Analysis Section */}
        <div className="space-y-6">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Upload Food Image</h2>
            
            {!selectedImage ? (
              <div className="space-y-4">
                {/* Camera Button */}
                <button
                  onClick={startCamera}
                  disabled={isCameraLoading}
                  className="w-full p-4 border-2 border-dashed border-primary-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors flex items-center justify-center space-x-2 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isCameraLoading ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-primary-600"></div>
                      <span className="text-primary-600 font-medium">Starting Camera...</span>
                    </>
                  ) : (
                    <>
                      <Camera className="w-6 h-6 text-primary-600" />
                      <span className="text-primary-600 font-medium">Take Photo</span>
                    </>
                  )}
                </button>

                {/* Upload Button */}
                <button
                  onClick={() => fileInputRef.current?.click()}
                  className="w-full p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors flex items-center justify-center space-x-2"
                >
                  <Upload className="w-6 h-6 text-gray-400" />
                  <span className="text-gray-600">Upload Image</span>
                </button>
                
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleImageUpload}
                  className="hidden"
                />

                {/* Camera Error Display */}
                {cameraError && (
                  <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
                    <p className="text-red-600 text-sm">{cameraError}</p>
                    <p className="text-red-500 text-xs mt-1">
                      Tip: Try uploading an image instead, or check your browser's camera permissions.
                    </p>
                  </div>
                )}

                {/* Test Backend Connection */}
                <button
                  onClick={async () => {
                    try {
                      const response = await fetch('http://localhost:8081/api/food/test');
                      if (response.ok) {
                        const data = await response.text();
                        alert(`Backend is working: ${data}`);
                      } else {
                        alert('Backend connection failed');
                      }
                    } catch (err) {
                      alert(`Backend error: ${err}`);
                    }
                  }}
                  className="w-full p-2 text-xs bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200"
                >
                  Test Backend Connection
                </button>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="relative">
                  <img
                    src={previewUrl}
                    alt="Food preview"
                    className="w-full h-64 object-cover rounded-lg"
                  />
                  <button
                    onClick={resetScanner}
                    className="absolute top-2 right-2 p-2 bg-black bg-opacity-50 text-white rounded-full hover:bg-opacity-70"
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
                  <p className="text-red-500 text-sm text-center">{error}</p>
                )}
              </div>
            )}
          </div>

          {/* Search Section */}
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Search Food</h2>
            <div className="space-y-4">
              <div className="flex space-x-2">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Enter food name..."
                  className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  onKeyPress={(e) => e.key === 'Enter' && searchFood()}
                />
                <button
                  onClick={searchFood}
                  className="px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600"
                >
                  Search
                </button>
              </div>
              
              {searchResults.length > 0 && (
                <div className="space-y-2">
                  <h3 className="font-medium text-gray-900">Search Results</h3>
                  {searchResults.map((food, index) => (
                    <div
                      key={index}
                      className="p-3 bg-gray-50 rounded-lg"
                    >
                      <div className="flex justify-between items-start mb-1">
                        <span className="font-medium text-gray-900">{food.name}</span>
                        <button
                          onClick={() => addToMealPlan(food)}
                          className="text-primary-600 hover:text-primary-700"
                        >
                          <Plus className="h-4 w-4" />
                        </button>
                      </div>
                      <div className="text-sm text-gray-600">
                        {food.calories} cal • {food.protein}g protein • {food.carbs}g carbs • {food.fat}g fat
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Results Section */}
        <div className="space-y-6">
          {results.length > 0 ? (
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-semibold text-gray-900">Analysis Results</h2>
                <div className="text-sm text-gray-600">
                  Total: {totalCalories} calories
                </div>
              </div>
              
              <div className="space-y-3">
                {results.map((food, index) => (
                  <div
                    key={index}
                    className="p-4 bg-gray-50 rounded-lg"
                  >
                    <div className="flex justify-between items-start mb-2">
                      <div>
                        <h3 className="font-medium text-gray-900">{food.name}</h3>
                        <p className="text-sm text-gray-600">
                          Confidence: {(food.confidence * 100).toFixed(0)}%
                        </p>
                      </div>
                      <button
                        onClick={() => addToMealPlan(food)}
                        className="text-primary-600 hover:text-primary-700"
                      >
                        <Plus className="h-5 w-5" />
                      </button>
                    </div>
                    
                    <div className="grid grid-cols-4 gap-2 text-sm">
                      <div className="text-center">
                        <div className="font-medium text-gray-900">{food.calories}</div>
                        <div className="text-gray-600">calories</div>
                      </div>
                      <div className="text-center">
                        <div className="font-medium text-gray-900">{food.protein}g</div>
                        <div className="text-gray-600">protein</div>
                      </div>
                      <div className="text-center">
                        <div className="font-medium text-gray-900">{food.carbs}g</div>
                        <div className="text-gray-600">carbs</div>
                      </div>
                      <div className="text-center">
                        <div className="font-medium text-gray-900">{food.fat}g</div>
                        <div className="text-gray-600">fat</div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
              
              <button
                onClick={resetScanner}
                className="w-full mt-4 text-sm text-gray-600 hover:text-gray-800 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                Scan Another Food
              </button>
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="text-center">
                <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Zap className="h-8 w-8 text-primary-600" />
                </div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">Ready to Scan</h3>
                <p className="text-gray-600">
                  Upload an image or take a photo to get started with food analysis
                </p>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Camera Modal */}
      {showCamera && (
        <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-4 max-w-md w-full mx-4">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-medium">Take Photo</h3>
              <button
                onClick={stopCamera}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="h-6 w-6" />
              </button>
            </div>
            
            <div className="relative">
              <video
                ref={videoRef}
                autoPlay
                playsInline
                muted
                className="w-full h-64 object-cover rounded-lg mb-4"
                onLoadedMetadata={() => {
                  console.log('Video metadata loaded');
                  console.log('Video dimensions:', videoRef.current?.videoWidth, 'x', videoRef.current?.videoHeight);
                }}
                onCanPlay={() => {
                  console.log('Video can play');
                  console.log('Video ready state:', videoRef.current?.readyState);
                  if (videoRef.current?.videoWidth && videoRef.current?.videoHeight) {
                    setIsCameraReady(true);
                  }
                }}
                onError={(e) => {
                  console.error('Video error:', e);
                  setCameraError('Video playback error');
                }}
              />

              {/* Camera status indicator */}
              {!isCameraLoading && isCameraReady && (
                <div className="absolute top-2 left-2 bg-green-500 text-white text-xs px-2 py-1 rounded">
                  Camera Ready
                </div>
              )}
            </div>

            {/* Camera Error Display */}
            {cameraError && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg mb-4">
                <p className="text-red-600 text-sm">{cameraError}</p>
                <p className="text-red-500 text-xs mt-1">
                  Tip: Try uploading an image instead, or check your browser's camera permissions.
                </p>
              </div>
            )}

            {/* Camera Controls */}
            <div className="flex space-x-2">
              <button
                onClick={capturePhoto}
                disabled={isCameraLoading || !isCameraReady}
                className="flex-1 bg-primary-500 text-white px-4 py-2 rounded-lg hover:bg-primary-600 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
              >
                {isCameraLoading ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    <span>Starting...</span>
                  </>
                ) : !isCameraReady ? (
                  <span>Waiting for camera...</span>
                ) : (
                  <>
                    <Camera className="h-4 w-4" />
                    <span>Capture Photo</span>
                  </>
                )}
              </button>
            </div>

            {/* Camera tips */}
            <div className="mt-3 text-xs text-gray-500 text-center">
              <p>Make sure your food is well-lit and clearly visible</p>
              <p className="mt-1">
                Camera Status: {isCameraReady ? 'Ready' : 'Initializing...'}
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FoodScanner; 