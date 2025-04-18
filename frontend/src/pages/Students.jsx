// src/pages/Students.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth.jsx';
import Sidebar from '../components/common/Sidebar';
import FileUploader from '../components/students/FileUploader';
import StudentList from '../components/students/StudentList';
import api from '../services/api.jsx';
import '../styles/Students.css';

const Students = () => {
  const { user, loading } = useAuth();
  const navigate = useNavigate();
  const [students, setStudents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [filter, setFilter] = useState('all');

  useEffect(() => {
    if (!loading && !user) {
      navigate('/login');
      return;
    }

    if (user) {
      fetchStudents();
    }
  }, [user, loading, navigate]);

  const fetchStudents = async () => {
    try {
      setIsLoading(true);
      const response = await api.get('/students');
      console.log('API Response:', response);
      
      // Simplified approach to match what works in Degrees.jsx
      setStudents(Array.isArray(response) ? response : []);
    } catch (err) {
      console.error('Failed to fetch students:', err);
      setError('Failed to load students');
      setStudents([]); // Set empty array on error
    } finally {
      setIsLoading(false);
    }
  };

  const handleFileUpload = async (file) => {
    try {
      setIsLoading(true);
      setError(null);
      setSuccessMessage(null);
      
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await api.post('/students/import', formData);
      
      setSuccessMessage(response.message || 'Students imported successfully');
      await fetchStudents(); // Refresh the list after import
    } catch (err) {
      console.error('File upload failed:', err);
      
      // Better error message handling
      if (err.message && err.message.includes('Student ID already exists')) {
        setError('Import failed: The Excel file contains students with IDs that already exist in the database. Please update your file with unique student IDs.');
      } else {
        setError(err.message || 'Failed to import students');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleStatusChange = async (studentId, newStatus) => {
    try {
      setIsLoading(true);
      await api.patch(`/students/${studentId}/status`, { status: newStatus });
      
      // Update the local state to reflect the change
      setStudents(prevStudents => 
        prevStudents.map(student => 
          student.id === studentId 
            ? { ...student, status: newStatus } 
            : student
        )
      );
      
      setSuccessMessage(`Student status updated to ${newStatus}`);
      await fetchStudents(); // Refresh the list after status change
    } catch (err) {
      console.error('Status update failed:', err);
      setError('Failed to update student status');
    } finally {
      setIsLoading(false);
    }
  };
  
  const handleDownloadClick = (student) => {
    // Set a timeout to refresh the list after a reasonable time for download + deletion
    setTimeout(() => {
      fetchStudents();
      setSuccessMessage(`Certificate downloaded for ${student.studentName}. Record has been removed.`);
    }, 2000);
  };
  
  // Make sure to handle the case when students is undefined
  const filteredStudents = filter === 'all' 
    ? students 
    : (students || []).filter(student => student.status === filter.toUpperCase());

  return (
    <div className="students-container">
      <Sidebar activePage="students" />
      
      <div className="dashboard-content">
        <header className="dashboard-header">
          <div className="header-content">
            <h1>Student Management</h1>
          </div>
        </header>

        <main className="main-content">
          {error && <div className="error-message">{error}</div>}
          {successMessage && <div className="success-message">{successMessage}</div>}
          
          <div className="students-actions">
            <FileUploader onFileUpload={handleFileUpload} />
            
            <div className="filter-controls">
              <label htmlFor="status-filter">Filter by Status:</label>
              <select 
                id="status-filter" 
                value={filter}
                onChange={(e) => setFilter(e.target.value)}
              >
                <option value="all">All Students</option>
                <option value="pending">Pending</option>
                <option value="queued">Queued</option>
                <option value="issued">Issued</option>
              </select>
            </div>
          </div>
          
          {isLoading ? (
            <div className="loading">Loading students...</div>
          ) : (
            <>
              <div className="student-count">
                {filteredStudents.length} {filter === 'all' ? 'total' : filter.toLowerCase()} students
              </div>
              <StudentList 
                students={filteredStudents} 
                onStatusChange={handleStatusChange}
                onDownloadClick={handleDownloadClick}
              />
            </>
          )}
        </main>
      </div>
    </div>
  );
};

export default Students;