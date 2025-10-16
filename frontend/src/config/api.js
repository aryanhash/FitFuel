const API_CONFIG = {
  // Development - temporarily using production backend
  development: {
    baseURL: 'https://fitfuel-backend.onrender.com',
    apiURL: 'https://fitfuel-backend.onrender.com/api'
  },
  // Production
  production: {
    baseURL: process.env.REACT_APP_API_URL || 'https://fitfuel-backend.onrender.com',
    apiURL: process.env.REACT_APP_API_BASE_URL || 'https://fitfuel-backend.onrender.com/api'
  }
};

const environment = process.env.NODE_ENV || 'development';
export const API_BASE_URL = API_CONFIG[environment].baseURL;
export const API_URL = API_CONFIG[environment].apiURL;

export default API_CONFIG;
