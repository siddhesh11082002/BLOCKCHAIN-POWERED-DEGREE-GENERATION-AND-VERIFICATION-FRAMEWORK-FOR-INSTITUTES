// src/hooks/useAuth.jsx
import { useContext } from 'react';
import AuthContext from '../context/AuthContext.jsx';

/**
 * Custom hook that provides access to authentication context
 * @returns {Object} Authentication context with user data and methods
 */
const useAuth = () => {
  const context = useContext(AuthContext);
  
  if (context === null) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  
  return context;
};

export default useAuth;