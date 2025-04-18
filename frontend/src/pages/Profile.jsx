// src/pages/Profile.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth.jsx';
import Sidebar from '../components/common/Sidebar';
import '../styles/Profile.css';

const Profile = () => {
  const { user, loading, refreshUser } = useAuth();
  const navigate = useNavigate();
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Populate form with user data when component loads
  useState(() => {
    if (user) {
      setFormData({
        fullName: user.fullName || '',
        email: user.email || '',
        password: '',
        confirmPassword: '',
      });
    }
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate passwords match if changing password
    if (formData.password && formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    try {
      setError(null);
      // Implementation would depend on your API service
      // Example: await api.put(`/users/${user.id}`, { ...formData });
      
      setSuccessMessage('Profile updated successfully');
      setIsEditing(false);
      refreshUser(); // Refresh user data after update
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to update profile');
    }
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  if (!user) {
    navigate('/login');
    return null;
  }

  return (
    <div className="profile-container">
      <Sidebar activePage="profile" />
      
      <div className="dashboard-content">
        <header className="dashboard-header">
          <div className="header-content">
            <h1>Profile Settings</h1>
          </div>
        </header>

        <main className="main-content">
          {error && <div className="error-message">{error}</div>}
          {successMessage && <div className="success-message">{successMessage}</div>}
          
          <div className="profile-card">
            <div className="profile-header">
              <div className="profile-avatar">
                <div className="avatar-placeholder">
                  {user.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
                </div>
              </div>
              <div className="profile-info">
                <h2>{user.fullName}</h2>
                <p>{user.email}</p>
                <span className="role-badge">Administrator</span>
              </div>
            </div>

            <div className="profile-details">
              {isEditing ? (
                <form onSubmit={handleSubmit} className="profile-form">
                  <div className="form-group">
                    <label htmlFor="fullName">Full Name</label>
                    <input
                      type="text"
                      id="fullName"
                      name="fullName"
                      value={formData.fullName}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="email">Email Address</label>
                    <input
                      type="email"
                      id="email"
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="password">New Password (leave blank to keep current)</label>
                    <input
                      type="password"
                      id="password"
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                    />
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="confirmPassword">Confirm New Password</label>
                    <input
                      type="password"
                      id="confirmPassword"
                      name="confirmPassword"
                      value={formData.confirmPassword}
                      onChange={handleChange}
                    />
                  </div>
                  
                  <div className="form-actions">
                    <button type="submit" className="save-button">Save Changes</button>
                    <button 
                      type="button" 
                      className="cancel-button"
                      onClick={() => setIsEditing(false)}
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              ) : (
                <div className="profile-info-details">
                  <div className="info-group">
                    <label>Username</label>
                    <p>{user.username}</p>
                  </div>
                  
                  <div className="info-group">
                    <label>Full Name</label>
                    <p>{user.fullName}</p>
                  </div>
                  
                  <div className="info-group">
                    <label>Email</label>
                    <p>{user.email}</p>
                  </div>
                  
                  <div className="info-group">
                    <label>Account Created</label>
                    <p>{new Date(user.createdAt).toLocaleDateString()}</p>
                  </div>
                  
                  <div className="info-group">
                    <label>Last Login</label>
                    <p>{user.lastLogin ? new Date(user.lastLogin).toLocaleString() : 'N/A'}</p>
                  </div>
                  
                  <button 
                    className="edit-profile-button"
                    onClick={() => setIsEditing(true)}
                  >
                    Edit Profile
                  </button>
                </div>
              )}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Profile;