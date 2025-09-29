/// <reference types="react-scripts" />

// Web Audio API types
declare global {
  interface Window {
    webkitAudioContext: typeof AudioContext;
  }
}
