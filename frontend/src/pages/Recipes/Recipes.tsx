import React, { useEffect, useState } from 'react';
import { useLocation, Link } from 'react-router-dom';

const Recipes: React.FC = () => {
  const location = useLocation();
  const meal = location.state?.meal;

  // Move hooks to the top, before any conditional returns
  const [videoUrl, setVideoUrl] = useState<string | null>(null);

  useEffect(() => {
    async function fetchVideo() {
      if (meal?.name) {
        try {
          // Adjust the API URL if your backend runs on a different port (e.g., http://localhost:8080)
          const res = await fetch(`/api/youtube/search?query=${encodeURIComponent(meal.name)}`);
          const url = await res.text();
          setVideoUrl(url || null);
        } catch (err) {
          setVideoUrl(null);
        }
      }
    }
    fetchVideo();
  }, [meal?.name]);

  if (!meal) {
    return (
      <div className="space-y-6">
        <h1 className="text-2xl font-bold text-gray-900">Recipe Details</h1>
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <p className="text-gray-600">No meal selected. Please select a meal from the Meal Planner.</p>
        </div>
      </div>
    );
  }

  // Demo fallback data for ingredients, instructions, nutrition
  const ingredients = meal.ingredients || [
    { name: 'Quinoa', amount: '1 cup' },
    { name: 'Cucumber', amount: '1 cup, diced' },
    { name: 'Tomatoes', amount: '1 cup, diced' },
    { name: 'Red Onion', amount: '1/2 cup, finely chopped' },
    { name: 'Kalamata Olives', amount: '1/2 cup, pitted and halved' },
    { name: 'Feta Cheese', amount: '1/2 cup, crumbled' },
    { name: 'Fresh Parsley', amount: '1/4 cup, chopped' },
    { name: 'Lemon', amount: '1, juiced' },
    { name: 'Olive Oil', amount: '2 tablespoons' },
    { name: 'Salt', amount: '1/2 teaspoon' },
    { name: 'Pepper', amount: '1/4 teaspoon' },
  ];
  const instructions = meal.instructions || [
    {
      title: 'Cook the Quinoa',
      step: 'Rinse quinoa thoroughly under cold water. In a medium saucepan, combine quinoa with 2 cups of water. Bring to a boil, then reduce heat and simmer for 15 minutes, or until quinoa is cooked and water is absorbed. Fluff with a fork and let cool.'
    },
    {
      title: 'Combine Ingredients',
      step: 'In a large bowl, combine cooked quinoa, diced cucumber, tomatoes, red onion, Kalamata olives, and crumbled feta cheese.'
    },
    {
      title: 'Prepare Dressing',
      step: 'In a small bowl, whisk together lemon juice, olive oil, salt, and pepper. Pour dressing over the salad and toss gently to combine.'
    },
    {
      title: 'Garnish and Serve',
      step: 'Garnish with fresh parsley. Serve immediately or chill for later.'
    },
  ];
  const nutrition = meal.nutrition || [
    { name: 'Calories', value: '350' },
    { name: 'Protein', value: '12g' },
    { name: 'Fat', value: '18g' },
    { name: 'Carbohydrates', value: '35g' },
  ];

  return (
    <div className="max-w-3xl mx-auto py-8 px-4">
      {/* Breadcrumb */}
      <nav className="text-sm mb-4 text-[#51946c]">
        <Link to="/recipes" className="hover:underline">Recipes</Link>
        <span className="mx-2">/</span>
        <span className="text-gray-700">{meal.name}</span>
      </nav>

      {/* Hero Image */}
      <img src={meal.img} alt={meal.name} className="w-full rounded-xl object-cover aspect-video mb-6" />

      {/* Title & Description */}
      <h1 className="text-2xl font-bold text-gray-900 mb-2">{meal.name}</h1>
      <p className="text-gray-600 mb-6">{meal.desc || 'A delicious and healthy meal.'}</p>

      {/* Ingredients */}
      <h2 className="text-lg font-bold text-gray-900 mb-2">Ingredients</h2>
      <div className="mb-6">
        <table className="w-full text-left">
          <tbody>
            {ingredients.map((ing: any, idx: number) => (
              <tr key={idx} className="border-b last:border-b-0">
                <td className="py-2 text-[#51946c] font-medium w-1/2">{ing.name}</td>
                <td className="py-2 text-gray-700">{ing.amount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Instructions */}
      <h2 className="text-lg font-bold text-gray-900 mb-2">Instructions</h2>
      <div className="mb-6">
        {instructions.map((ins: any, idx: number) => (
          <div key={idx} className="mb-6">
            <div className="flex items-baseline gap-2 mb-1">
              <span className="text-xs text-gray-400 font-semibold align-top pt-0.5">{idx + 1}</span>
              <span className="font-semibold text-gray-900">{ins.title}</span>
            </div>
            <div className="text-[#51946c] text-sm ml-6">{ins.step}</div>
          </div>
        ))}
      </div>

      {/* Video Tutorial */}
      <div className="mb-6">
        <h2 className="text-lg font-bold text-gray-900 mb-2">Video Tutorial</h2>
        <div className="aspect-video w-full rounded-xl overflow-hidden mb-4">
          {videoUrl ? (
            <iframe
              width="100%"
              height="315"
              src={videoUrl}
              title={meal.name}
              frameBorder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            ></iframe>
          ) : (
            <div className="flex items-center justify-center h-full text-gray-400">
              No video found.
            </div>
          )}
        </div>
      </div>

      {/* Nutrition */}
      <h2 className="text-lg font-bold text-gray-900 mb-2">Nutritional Information (per serving)</h2>
      <div className="mb-8">
        <table className="w-full text-left">
          <tbody>
            {nutrition.map((n: any, idx: number) => (
              <tr key={idx} className="border-b last:border-b-0">
                <td className="py-2 text-[#51946c] font-medium w-1/2">{n.name}</td>
                <td className="py-2 text-gray-700">{n.value}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Action Buttons */}
      <div className="flex gap-4">
        <button className="bg-[#38e07b] text-[#0e1a13] font-bold px-6 py-2 rounded-lg shadow-sm hover:bg-[#2fc76b] transition">Save Recipe</button>
        <button className="bg-white border border-gray-300 text-[#0e1a13] font-medium px-6 py-2 rounded-lg shadow-sm hover:bg-gray-100 transition">Share</button>
      </div>
    </div>
  );
};

export default Recipes; 