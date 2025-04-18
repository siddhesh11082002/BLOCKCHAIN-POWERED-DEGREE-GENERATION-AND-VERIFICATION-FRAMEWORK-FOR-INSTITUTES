// src/pages/Degrees.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth.jsx';
import Sidebar from '../components/common/Sidebar';
import api from '../services/api.jsx';
import '../styles/Degrees.css';

const Degrees = () => {
  const { user, loading } = useAuth();
  const navigate = useNavigate();
  const [queuedStudents, setQueuedStudents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [processingStudents, setProcessingStudents] = useState({}); // Track processing state per student
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  useEffect(() => {
    if (!loading && !user) {
      navigate('/login');
      return;
    }

    if (user) {
      fetchQueuedStudents();
    }
  }, [user, loading, navigate]);

  const fetchQueuedStudents = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      const response = await api.get('/students/status/QUEUED');
      console.log('Queued students response:', response);
      
      // Handle different response formats
      if (Array.isArray(response)) {
        setQueuedStudents(response);
      } else if (response && Array.isArray(response.data)) {
        setQueuedStudents(response.data);
      } else {
        console.warn('Unexpected response format:', response);
        setQueuedStudents([]);
      }
    } catch (err) {
      console.error('Failed to fetch queued students:', err);
      setError('Failed to load queued students: ' + (err.message || 'Unknown error'));
      setQueuedStudents([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleGenerateCertificate = async (studentId) => {
    try {
      // Set processing state for this specific student
      setProcessingStudents(prev => ({ ...prev, [studentId]: true }));
      setError(null);
      setSuccessMessage(null);
      
      // Call the certificate generation endpoint
      await api.post(`/certificates/generateForStudent/${studentId}`);
      
      setSuccessMessage(`Certificate successfully generated for student ID: ${studentId}`);
      
      // Refresh the full list to get updated statuses
      await fetchQueuedStudents();
    } catch (err) {
      console.error('Certificate generation failed:', err);
      setError(`Failed to generate certificate: ${err.message || 'Unknown error'}`);
    } finally {
      // Clear processing state for this student
      setProcessingStudents(prev => {
        const updated = { ...prev };
        delete updated[studentId];
        return updated;
      });
    }
  };

  // We need to render conditionally based on our state
  const renderContent = () => {
    if (isLoading) {
      return <div className="loading">Loading students...</div>;
    }
    
    if (!queuedStudents || queuedStudents.length === 0) {
      return (
        <div className="no-students">
          <p>No students are currently queued for certificate generation.</p>
          <button className="primary-button" onClick={() => navigate('/students')}>
            Go to Student Management
          </button>
        </div>
      );
    }
    
    return (
      <div className="queued-students">
        <h3>{queuedStudents.length} Students Waiting for Certificates</h3>
        <table className="students-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>ID</th>
              <th>Degree</th>
              <th>University</th>
              <th>Graduation Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {queuedStudents.map((student) => (
              <tr key={student.id}>
                <td>{student.studentName}</td>
                <td>{student.studentId}</td>
                <td>{student.degreeName}</td>
                <td>{student.universityName}</td>
                <td>{student.graduationDate ? new Date(student.graduationDate).toLocaleDateString() : 'N/A'}</td>
                <td>
                  <button 
                    className="generate-button"
                    onClick={() => handleGenerateCertificate(student.id)}
                    disabled={processingStudents[student.id]}
                  >
                    {processingStudents[student.id] ? 'Processing...' : 'Generate Certificate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  return (
    <div className="degrees-container">
      <Sidebar activePage="degrees" />
      
      <div className="dashboard-content">
        <header className="dashboard-header">
          <div className="header-content">
            <h1>Degree Certificate Management</h1>
          </div>
        </header>

        <main className="main-content">
          {error && (
            <div className="error-message">
              <p>{error}</p>
              <button onClick={() => setError(null)} className="dismiss-button">Dismiss</button>
            </div>
          )}
          
          {successMessage && (
            <div className="success-message">
              <p>{successMessage}</p>
              <button onClick={() => setSuccessMessage(null)} className="dismiss-button">Dismiss</button>
            </div>
          )}
          
          <div className="degrees-header">
            <h2>Students Queued for Certificate Generation</h2>
            <p>Review and generate certificates for queued students</p>
            <button 
              className="refresh-button"
              onClick={fetchQueuedStudents}
              disabled={isLoading}
            >
              {isLoading ? 'Refreshing...' : 'Refresh List'}
            </button>
          </div>
          
          {renderContent()}
        </main>
      </div>
    </div>
  );
};

export default Degrees;
