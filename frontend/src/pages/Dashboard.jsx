// src/pages/Dashboard.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth.jsx';
import Sidebar from '../components/common/Sidebar';
import StatsOverview from '../components/dashboard/StatsOverview';
import DegreePreviewBox from '../components/dashboard/DegreePreviewBox';
import DepartmentStatsTable from '../components/dashboard/DepartmentStatsTable';
import api from '../services/api.jsx';
import '../styles/Dashboard.css';

const Dashboard = () => {
  const { user, loading, logout } = useAuth();
  const navigate = useNavigate();
  const [statistics, setStatistics] = useState({
    statusCounts: { pending: 0, queued: 0, issued: 0 },
    departmentStats: []
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Redirect to login if user is not authenticated
    if (!loading && !user) {
      navigate('/login');
      return;
    }

    // Fetch statistics
    if (user) {
      fetchStatistics();
    }
  }, [user, loading, navigate]);

  const fetchStatistics = async () => {
    try {
      setIsLoading(true);
      const response = await api.get('/students/statistics');
      setStatistics({
        statusCounts: response.data?.statusCounts || { pending: 0, queued: 0, issued: 0 },
        departmentStats: response.data?.departmentStats || []
      });
    } catch (err) {
      console.error('Failed to fetch statistics:', err);
      setError('Failed to load dashboard statistics');
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  if (loading || isLoading) {
    return <div className="loading">Loading...</div>;
  }

  if (!user) {
    return null; // Will redirect in useEffect
  }

  return (
    <div className="dashboard-container">
      <Sidebar activePage="dashboard" />
      
      <div className="dashboard-content">
        <header className="dashboard-header">
          <div className="header-content">
            <h1>Dashboard</h1>
            <div className="user-info">
              <span>{user.fullName}</span>
              <button onClick={handleLogout} className="logout-button">
                Logout
              </button>
            </div>
          </div>
        </header>

        <main className="main-content">
          <div className="welcome-card">
            <h2>Welcome, {user.fullName}!</h2>
            <p>This is your dashboard for the Certificate Management System.</p>
          </div>

          {error && <div className="error-message">{error}</div>}

          <StatsOverview statusCounts={statistics.statusCounts} />
          <div className="dashboard-grid">
            <DegreePreviewBox />
            <DepartmentStatsTable departmentStats={statistics.departmentStats || []} />
          </div>
        </main>
      </div>
    </div>
  );
};

export default Dashboard;