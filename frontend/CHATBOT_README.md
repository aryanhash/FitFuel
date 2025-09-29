# Floating ChatBot Feature

## Overview
The floating chatbot is a feature that automatically appears in the bottom-right corner of the screen after 5 seconds when a user is authenticated. It provides an interactive AI assistant for meal planning and nutrition advice.

## Features

### 🎯 **Auto-Appearance**
- Automatically appears after 5 seconds of page load
- Only shows for authenticated users
- Positioned in the bottom-right corner of the screen

### 🔊 **Sound Notification**
- Plays a notification sound when the chatbot appears
- Uses Web Audio API to generate a pleasant notification tone
- Gracefully handles browsers that don't support audio

### 🎨 **Smooth Animations**
- Bounce-in animation when the chat button appears
- Slide-up animation when the chat window opens
- Hover effects and smooth transitions

### 💬 **Interactive Chat**
- Real-time messaging interface
- Message history with timestamps
- Loading indicators for AI responses
- Auto-scroll to latest messages

### 🎨 **Modern UI**
- Clean, modern design with Tailwind CSS
- Responsive layout
- Professional color scheme
- Notification dot with pulse animation

## Technical Implementation

### Components
- **FloatingChatBot.tsx**: Main chatbot component
- **App.tsx**: Integration point for global availability

### Key Features
1. **State Management**: Uses React hooks for managing chat state
2. **Audio Integration**: Web Audio API for notification sounds
3. **Animation System**: Custom CSS animations for smooth UX
4. **Authentication Integration**: Only shows for logged-in users
5. **Responsive Design**: Works on all screen sizes

### File Structure
```
frontend/src/
├── components/Chat/
│   └── FloatingChatBot.tsx    # Main chatbot component
├── App.tsx                    # Integration point
├── index.css                  # Animation styles
└── react-app-env.d.ts         # TypeScript declarations
```

## Usage

The chatbot automatically appears on all authenticated pages. Users can:

1. **Open Chat**: Click the floating chat button
2. **Send Messages**: Type and press Enter or click send
3. **Close Chat**: Click the X button in the header
4. **Minimize**: Click outside or close to return to button state

## Customization

### Timing
- Change the 5-second delay in the `useEffect` timer
- Modify the `playNotificationSound` function for different sounds

### Styling
- Update Tailwind classes for different colors/themes
- Modify CSS animations in `index.css`
- Adjust positioning with the `fixed bottom-6 right-6` classes

### Functionality
- Replace the mock AI response with actual API calls
- Add more interactive features like file uploads
- Implement message persistence across sessions

## Browser Compatibility

- ✅ Modern browsers (Chrome, Firefox, Safari, Edge)
- ✅ Web Audio API support for notification sounds
- ✅ CSS animations and transitions
- ✅ Responsive design for mobile devices

## Future Enhancements

- [ ] Integration with actual AI backend
- [ ] Message history persistence
- [ ] File/image sharing capabilities
- [ ] Voice input/output
- [ ] Customizable themes
- [ ] Multi-language support 