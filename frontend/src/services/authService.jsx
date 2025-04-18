// src/services/authService.jsx
const API_URL = 'http://localhost:8081/api';

/**
 * Service to handle authentication-related API calls
 */
const authService = {
  /**
   * Attempts to log in a user with the provided credentials
   * @param {string} username - The user's username
   * @param {string} password - The user's password
   * @returns {Promise<Object>} The user data if successful
   */
  login: async (username, password) => {
    try {
      const response = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include', // Important for cookies/CSRF tokens
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Login failed');
      }

      const data = await response.json();
      // Store user info in local storage for persistence across page refreshes
      if (data.user) {
        localStorage.setItem('user', JSON.stringify(data.user));
      }
      return data;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  },

  /**
   * Logs out the current user
   * @returns {Promise<void>}
   */
  logout: async () => {
    try {
      await fetch(`${API_URL}/auth/logout`, {
        method: 'POST',
        credentials: 'include',
      });
      // Clear user from local storage
      localStorage.removeItem('user');
    } catch (error) {
      console.error('Logout error:', error);
      // Still remove user from storage even if the API call fails
      localStorage.removeItem('user');
      throw error;
    }
  },

  /**
   * Gets the current user's profile
   * @returns {Promise<Object>} The user data
   */
  getCurrentUser: async () => {
    try {
      const response = await fetch(`${API_URL}/users/current`, {
        method: 'GET',
        credentials: 'include',
      });

      if (!response.ok) {
        throw new Error('Failed to fetch current user');
      }

      const user = await response.json();
      return user;
    } catch (error) {
      console.error('Get current user error:', error);
      throw error;
    }
  },

  /**
   * Checks if the system requires initial setup (no users exist)
   * @returns {Promise<Object>} Status information
   */
  getSystemStatus: async () => {
    try {
      const response = await fetch(`${API_URL}/auth/status`, {
        method: 'GET',
      });

      if (!response.ok) {
        throw new Error('Failed to check system status');
      }

      return await response.json();
    } catch (error) {
      console.error('System status check error:', error);
      throw error;
    }
  },

  /**
   * Gets the currently saved user from local storage
   * @returns {Object|null} The user object or null if not logged in
   */
  getStoredUser: () => {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;
    
    try {
      return JSON.parse(userStr);
    } catch (e) {
      console.error('Error parsing stored user:', e);
      return null;
    }
  }
};

export default authService;