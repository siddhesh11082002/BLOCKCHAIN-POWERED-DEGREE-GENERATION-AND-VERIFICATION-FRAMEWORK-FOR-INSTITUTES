// src/App.jsx
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { lazy, Suspense } from 'react';
import { AuthProvider } from './context/AuthContext.jsx';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Students from './pages/Students';
import Degrees from './pages/Degrees';
import ProtectedRoute from './components/common/ProtectedRoute';
import VerifyCertificate from './pages/VerifyCertificate';
import './styles/App.css';

// Lazily load the Profile component
const Profile = lazy(() => import('./pages/Profile'));

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/verify" element={<VerifyCertificate />} />
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/students" element={
            <ProtectedRoute>
              <Students />
            </ProtectedRoute>
          } />
          
          <Route path="/degrees" element={
            <ProtectedRoute>
              <Degrees />
            </ProtectedRoute>
          } />
          
          <Route path="/profile" element={
            <ProtectedRoute>
              <Suspense fallback={<div className="loading">Loading profile...</div>}>
                <Profile />
              </Suspense>
            </ProtectedRoute>
          } />
          
          {/* Redirect root to dashboard, with protection */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          
          {/* Add a catch-all route */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;