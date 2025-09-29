import React from 'react';

const ChatHeader: React.FC = () => {
  return (
    <div className="bg-white border-b border-gray-200 px-6 py-4">
      <div className="flex items-center space-x-3">
        <div className="w-10 h-10 bg-gradient-to-r from-green-400 to-blue-500 rounded-full flex items-center justify-center">
          <span className="text-white font-bold text-lg">🍎</span>
        </div>
        <div>
          <h2 className="text-lg font-semibold text-gray-900">AI Nutrition Assistant</h2>
          <p className="text-sm text-gray-500">Ask me about nutrition, meal planning, and healthy eating tips</p>
        </div>
      </div>
    </div>
  );
};

export default ChatHeader; 