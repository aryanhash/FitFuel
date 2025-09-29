import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const monthNames = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

function getDaysInMonth(year: number, month: number) {
  // month is 1-indexed for this function (1=Jan, 12=Dec)
  return new Date(year, month, 0).getDate();
}

function formatDate(year: number, month: number, day: number) {
  // month is 1-indexed
  return new Date(year, month - 1, day).toISOString().slice(0, 10);
}

const MealPlanner: React.FC = () => {
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [selectedDate, setSelectedDate] = useState(new Date().getDate());
  const [meals, setMeals] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const navigate = useNavigate();

  const daysInMonth = getDaysInMonth(selectedYear, selectedMonth);

  // Handle month navigation
  const goToPrevMonth = () => {
    if (selectedMonth === 1) {
      setSelectedMonth(12);
      setSelectedYear(selectedYear - 1);
      setSelectedDate(1);
    } else {
      setSelectedMonth(selectedMonth - 1);
      setSelectedDate(1);
    }
  };
  const goToNextMonth = () => {
    if (selectedMonth === 12) {
      setSelectedMonth(1);
      setSelectedYear(selectedYear + 1);
      setSelectedDate(1);
    } else {
      setSelectedMonth(selectedMonth + 1);
      setSelectedDate(1);
    }
  };

  useEffect(() => {
    setSelectedDate(1); // Reset to 1st when month/year changes
  }, [selectedMonth, selectedYear]);

  useEffect(() => {
    const fetchMeals = async () => {
      setLoading(true);
      setError(null);
      setMessage(null);
      try {
        const dateStr = formatDate(selectedYear, selectedMonth, selectedDate);
        const res = await fetch(`/api/meal-plan/day?date=${dateStr}`);
        if (!res.ok) throw new Error('Failed to fetch meals');
        const data = await res.json();
        setMeals(data.meals || []);
        setMessage(data.message || null);
      } catch (err: any) {
        setError(err.message || 'Unknown error');
        setMeals([]);
      } finally {
        setLoading(false);
      }
    };
    fetchMeals();
  }, [selectedDate, selectedMonth, selectedYear]);

  // Get the weekday of the 1st of the month (0=Sun, 1=Mon...)
  const firstDayOfWeek = new Date(selectedYear, selectedMonth - 1, 1).getDay();

  // Check if the selected year/month has data
  const hasData = selectedYear >= 2025;

  return (
    <div className="min-h-screen bg-[#f8fbfa] font-sans">
      <div className="flex flex-col items-center w-full">
        <div className="w-full flex flex-col items-center">
          <div className="flex flex-col w-full max-w-6xl">
            <div className="flex flex-wrap justify-between gap-3 p-4">
              <div className="flex min-w-72 flex-col gap-3">
                <p className="text-[#0e1a13] text-[32px] font-bold leading-tight">Your Monthly Meal Plan</p>
                <p className="text-[#51946c] text-sm font-normal leading-normal">Tailored to your fitness goals and preferences</p>
                {message && (
                  <p className="text-[#51946c] text-sm font-normal leading-normal bg-[#e8f2ec] p-2 rounded">
                    {message}
                  </p>
                )}
                {!hasData && (
                  <p className="text-[#e74c3c] text-sm font-normal leading-normal bg-[#fdf2f2] p-2 rounded border border-[#fecaca]">
                    Meal plans are available from 2025 onwards with yearly variations and seasonal considerations.
                  </p>
                )}
              </div>
            </div>
            {/* Calendar */}
            <div className="flex flex-wrap items-center justify-center gap-6 p-4">
              {/* Calendar */}
              <div className="flex min-w-72 max-w-[336px] flex-1 flex-col gap-0.5">
                <div className="flex items-center p-1 justify-between">
                  <button className="text-[#0e1a13] flex size-10 items-center justify-center" onClick={goToPrevMonth}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" fill="currentColor" viewBox="0 0 256 256"><path d="M165.66,202.34a8,8,0,0,1-11.32,11.32l-80-80a8,8,0,0,1,0-11.32l80-80a8,8,0,0,1,11.32,11.32L91.31,128Z"></path></svg>
                  </button>
                  <p className="text-[#0e1a13] text-base font-bold leading-tight flex-1 text-center pr-10">{monthNames[selectedMonth - 1]} {selectedYear}</p>
                  <button className="text-[#0e1a13] flex size-10 items-center justify-center" onClick={goToNextMonth}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" fill="currentColor" viewBox="0 0 256 256"><path d="M181.66,133.66l-80,80a8,8,0,0,1-11.32-11.32L164.69,128,90.34,53.66a8,8,0,0,1,11.32-11.32l80,80A8,8,0,0,1,181.66,133.66Z"></path></svg>
                  </button>
                </div>
                <div className="grid grid-cols-7">
                  {'SMTWTFS'.split('').map((d) => (
                    <p key={d} className="text-[#0e1a13] text-[13px] font-bold flex h-12 w-full items-center justify-center pb-0.5">{d}</p>
                  ))}
                  {/* Empty slots for days before the 1st */}
                  {Array(firstDayOfWeek).fill(null).map((_, i) => (
                    <div key={i} />
                  ))}
                  {[...Array(daysInMonth)].map((_, i) => {
                    const dayNum = i + 1;
                    const isSelected = selectedDate === dayNum;
                    return (
                      <button
                        key={i}
                        className={`h-12 w-full text-[#0e1a13] text-sm font-medium leading-normal bg-[#e8f2ec] ${isSelected ? 'ring-2 ring-[#38e07b] z-10' : ''} ${hasData ? '' : 'opacity-50'}`}
                        onClick={() => setSelectedDate(dayNum)}
                        style={{ cursor: hasData ? 'pointer' : 'not-allowed' }}
                      >
                        <div className={`flex size-full items-center justify-center rounded-full ${isSelected ? 'bg-[#38e07b] text-white' : ''}`}>{dayNum}</div>
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>
            {/* Meals for selected day */}
            <div>
              <h2 className="text-[#0e1a13] text-[22px] font-bold leading-tight tracking-[-0.015em] px-4 pb-3 pt-5">
                {monthNames[selectedMonth - 1]} {selectedDate}, {selectedYear}
              </h2>
              {loading ? (
                <div className="text-center text-gray-500 py-8">Loading meals...</div>
              ) : error ? (
                <div className="text-center text-red-500 py-8">{error}</div>
              ) : !hasData ? (
                <div className="text-center text-gray-400 py-8">
                  <div className="mb-4">No meal plan data available for {selectedYear}.</div>
                  <div className="text-sm">Please select a year from 2025 onwards to view meal plans with yearly variations and seasonal considerations.</div>
                </div>
              ) : meals.length === 0 ? (
                <div className="text-center text-gray-400 py-8">No meals for this day.</div>
              ) : (
                meals.map((meal: any, index: number) => (
                  <div key={index} className="p-4">
                    <div className="flex items-stretch justify-between gap-4 rounded-lg">
                      <div className="flex flex-[2_2_0px] flex-col gap-4">
                        <div className="flex flex-col gap-1">
                          <p className="text-[#51946c] text-sm font-normal leading-normal capitalize">{meal.mealType}</p>
                          <p className="text-[#0e1a13] text-base font-bold leading-tight">
                            {meal.name}
                          </p>
                          <p className="text-[#51946c] text-sm font-normal leading-normal">
                            {meal.description}
                          </p>
                          <div className="flex gap-4 text-xs text-[#51946c]">
                            <span>{meal.calories} cal</span>
                            <span>{meal.protein}g protein</span>
                            <span>{meal.carbs}g carbs</span>
                            <span>{meal.fat}g fat</span>
                          </div>
                        </div>
                        <button
                          className="flex min-w-[84px] max-w-[480px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-8 px-4 flex-row-reverse bg-[#e8f2ec] text-[#0e1a13] text-sm font-medium leading-normal w-fit"
                          onClick={() => navigate('/recipes', { state: { meal: meal } })}
                        >
                          <span className="truncate">View Recipe</span>
                        </button>
                      </div>
                      <div className="w-full bg-center bg-no-repeat aspect-video bg-cover rounded-lg flex-1 bg-gradient-to-br from-[#e8f2ec] to-[#d1e7dd] flex items-center justify-center">
                        <span className="text-[#51946c] text-sm">Meal Image</span>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MealPlanner; 