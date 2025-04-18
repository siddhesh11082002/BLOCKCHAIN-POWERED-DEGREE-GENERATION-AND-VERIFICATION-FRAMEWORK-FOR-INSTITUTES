// src/components/common/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';
import useAuth from '../../hooks/useAuth.jsx';

/**
 * A wrapper component for routes that require authentication
 * Redirects to login if user is not authenticated
 */
const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    // You could render a spinner here
    return <div className="loading">Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;