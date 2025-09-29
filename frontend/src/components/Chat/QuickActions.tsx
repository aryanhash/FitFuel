import React from 'react';

interface QuickActionsProps {
  onActionClick: (message: string) => void;
  disabled: boolean;
}

const QuickActions: React.FC<QuickActionsProps> = ({ onActionClick, disabled }) => {
  const quickActions = [
    "How can I lose weight healthily?",
    "What are good protein sources?",
    "How to plan balanced meals?",
    "Tips for healthy eating habits",
    "Should I take supplements?",
    "Nutrition for exercise"
  ];

  return (
    <div className="px-4 py-3 bg-white border-b border-gray-200">
      <p className="text-sm text-gray-600 mb-3">Quick questions:</p>
      <div className="flex flex-wrap gap-2">
        {quickActions.map((action, index) => (
          <button
            key={index}
            onClick={() => onActionClick(action)}
            disabled={disabled}
            className={`px-3 py-1 text-xs rounded-full border transition-colors ${
              disabled
                ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                : 'bg-blue-50 text-blue-600 border-blue-200 hover:bg-blue-100'
            }`}
          >
            {action}
          </button>
        ))}
      </div>
    </div>
  );
};

export default QuickActions; 