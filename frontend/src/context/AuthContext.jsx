// src/context/AuthContext.jsx
import { createContext, useState, useEffect } from 'react';
import authService from '../services/authService.jsx';

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Check if user is already logged in (from local storage)
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        // First try to get user from local storage
        const storedUser = authService.getStoredUser();
        
        if (storedUser) {
          // Verify the stored user with a server call
          try {
            const currentUser = await authService.getCurrentUser();
            setUser(currentUser);
          } catch (error) {
            // If verification fails (e.g., session expired), clear local storage
            localStorage.removeItem('user');
            setUser(null);
          }
        }
      } catch (error) {
        console.error('Auth initialization error:', error);
        setError('Failed to initialize authentication');
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
  }, []);

  // Login function
  const login = async (username, password) => {
    try {
      setLoading(true);
      setError(null);
      const response = await authService.login(username, password);
      setUser(response.user);
      return response;
    } catch (error) {
      setError(error.message || 'Login failed');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Logout function
  const logout = async () => {
    try {
      setLoading(true);
      await authService.logout();
      setUser(null);
    } catch (error) {
      setError(error.message || 'Logout failed');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Check system setup status
  const checkSystemStatus = async () => {
    try {
      return await authService.getSystemStatus();
    } catch (error) {
      setError(error.message || 'Failed to check system status');
      throw error;
    }
  };

  // Refresh user data
  const refreshUser = async () => {
    try {
      if (user) {
        const currentUser = await authService.getCurrentUser();
        setUser(currentUser);
        return currentUser;
      }
      return null;
    } catch (error) {
      console.error('Failed to refresh user data:', error);
      return null;
    }
  };

  const value = {
    user,
    loading,
    error,
    login,
    logout,
    checkSystemStatus,
    refreshUser,
    isAuthenticated: !!user
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthContext;