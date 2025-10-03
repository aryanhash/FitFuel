const API_CONFIG = {
  // Development
  development: {
    baseURL: 'http://localhost:8081',
    apiURL: 'http://localhost:8081/api'
  },
  // Production
  production: {
    baseURL: process.env.REACT_APP_API_URL || 'https://your-backend-url.onrender.com',
    apiURL: process.env.REACT_APP_API_BASE_URL || 'https://your-backend-url.onrender.com/api'
  }
};

const environment = process.env.NODE_ENV || 'development';
export const API_BASE_URL = API_CONFIG[environment].baseURL;
export const API_URL = API_CONFIG[environment].apiURL;

export default API_CONFIG;
