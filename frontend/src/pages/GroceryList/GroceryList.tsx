import React, { useState, useEffect } from 'react';

interface GroceryListItem {
  name: string;
  quantity: number;
  unit: string;
  imageUrl?: string;
}

interface GroceryCategory {
  category: string;
  items: GroceryListItem[];
}

const GroceryList: React.FC = () => {
  const [groceryList, setGroceryList] = useState<GroceryCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    const fetchGroceryList = async () => {
      setLoading(true);
      setError(null);
      setMessage(null);
      try {
        // Use the current month endpoint for seasonal data
        const res = await fetch(`/api/grocery-list/current-month`);
        console.log('Grocery list response status:', res.status);
        
        if (!res.ok) {
          const errorText = await res.text();
          console.error('Grocery list error response:', errorText);
          throw new Error(`Failed to fetch grocery list: ${res.status} ${errorText}`);
        }
        
        const data = await res.json();
        console.log('Grocery list data:', data);
        
        setGroceryList(data.groceryList || []);
        setMessage(data.message || null);
      } catch (err: any) {
        console.error('Grocery list fetch error:', err);
        setError(err.message || 'Unknown error');
      } finally {
        setLoading(false);
      }
    };
    fetchGroceryList();
  }, []);

  return (
    <div className="max-w-3xl mx-auto py-10 px-4">
      <h1 className="text-3xl font-bold mb-8 text-gray-900">Grocery List</h1>
      {message && (
        <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg">
          <p className="text-green-800 text-sm">{message}</p>
        </div>
      )}
      {loading ? (
        <div className="text-center text-gray-500 py-16">Loading grocery list...</div>
      ) : error ? (
        <div className="text-center text-red-500 py-16">
          <div className="mb-4">Error: {error}</div>
          <button 
            onClick={() => window.location.reload()} 
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Retry
          </button>
        </div>
      ) : groceryList.length === 0 ? (
        <div className="text-center text-gray-400 py-16">
          <div className="mb-4">No grocery items for this month.</div>
          <div className="text-sm">Grocery list data is available from 2025 onwards with seasonal variations.</div>
        </div>
      ) : (
        <div className="space-y-10">
          {groceryList.map((cat) => (
            <div key={cat.category}>
              <h2 className="text-xl font-semibold mb-4 text-gray-800">{cat.category}</h2>
              <div className="space-y-4">
                {cat.items.map((item) => (
                  <div key={item.name} className="flex items-center space-x-4 p-4 bg-white rounded-lg shadow-sm border border-gray-200">
                    <div className="w-14 h-14 rounded-lg bg-gradient-to-br from-green-100 to-green-200 flex items-center justify-center border border-gray-200">
                      <span className="text-green-600 text-xs font-medium">
                        {item.name.split(' ').map(word => word[0]).join('').toUpperCase()}
                      </span>
                    </div>
                    <div className="flex-1">
                      <div className="font-medium text-gray-900">{item.name}</div>
                      <div className="text-green-700 text-sm">{item.quantity} {item.unit}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default GroceryList; 