// src/components/students/StudentList.jsx
import React, { useState } from 'react';
import '../../styles/StudentList.css';
import api from '../../services/api.jsx';

const StudentList = ({ students, onStatusChange, onDownloadClick }) => {
  const [expandedStudent, setExpandedStudent] = useState(null);
  const [isEmailingCertificate, setIsEmailingCertificate] = useState({});
  const [emailSuccess, setEmailSuccess] = useState(null);
  const [emailError, setEmailError] = useState(null);
  const [isDownloading, setIsDownloading] = useState({});
  
  // Ensure students is always an array
  const studentList = students || [];

  // Base API URL - make sure this matches your API service configuration
  const API_URL = 'http://localhost:8080/api';

  const toggleExpand = (studentId) => {
    setExpandedStudent(expandedStudent === studentId ? null : studentId);
  };

  // Function to format date strings
  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    try {
      return new Date(dateStr).toLocaleDateString();
    } catch (e) {
      return dateStr;
    }
  };

  const getStatusClass = (status) => {
    switch(status) {
      case 'PENDING': return 'status-pending';
      case 'QUEUED': return 'status-queued';
      case 'ISSUED': return 'status-issued';
      default: return '';
    }
  };

  // Handle certificate download with prevent default
  const handleDownloadCertificate = (e, student) => {
    e.preventDefault(); // This prevents navigation/redirect
    
    if (!student.certificateId) {
      console.error('Certificate ID is missing');
      return;
    }
    
    // Set downloading state
    setIsDownloading(prev => ({ ...prev, [student.id]: true }));
    
    // Create download URL with full path
    const downloadUrl = `${API_URL}/certificates/download/${student.certificateId}`;
    
    // Open in new tab to trigger download
    window.open(downloadUrl, '_blank');
    
    // Notify parent component for UI update after download
    if (onDownloadClick) {
      onDownloadClick(student);
    }
    
    // Reset downloading state after a delay
    setTimeout(() => {
      setIsDownloading(prev => {
        const updated = { ...prev };
        delete updated[student.id];
        return updated;
      });
    }, 2000);
  };

  // Handle sending certificate via email
  const handleEmailCertificate = async (e, student) => {
    e.preventDefault();
    
    if (!student.certificateId || !student.email) {
      setEmailError('Certificate ID or email is missing');
      setTimeout(() => setEmailError(null), 5000);
      return;
    }
    
    try {
      setIsEmailingCertificate(prev => ({ ...prev, [student.id]: true }));
      setEmailError(null);
      setEmailSuccess(null);
      
      // Call your API to send the certificate with student details
      const response = await api.post(`/certificates/email/${student.certificateId}`, { 
        email: student.email,
        studentName: student.studentName,
        degreeName: student.degreeName,
        universityName: student.universityName
      });
      
      setEmailSuccess(`Certificate successfully sent to ${student.email}`);
      setTimeout(() => setEmailSuccess(null), 5000);
      
      // Also notify parent about the download/deletion
      if (onDownloadClick) {
        onDownloadClick(student);
      }
    } catch (err) {
      console.error('Failed to email certificate:', err);
      setEmailError(`Failed to send certificate: ${err.message || 'Unknown error'}`);
      setTimeout(() => setEmailError(null), 5000);
    } finally {
      setIsEmailingCertificate(prev => {
        const updated = { ...prev };
        delete updated[student.id];
        return updated;
      });
    }
  };

  return (
    <div className="student-list">
      {emailSuccess && <div className="success-message">{emailSuccess}</div>}
      {emailError && <div className="error-message">{emailError}</div>}
      
      {studentList.length === 0 ? (
        <div className="no-students">
          <p>No students found. Import students using the excel uploader above.</p>
        </div>
      ) : (
        <table className="students-table">
          <thead>
            <tr>
              <th></th>
              <th>Name</th>
              <th>ID</th>
              <th>Degree</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {studentList.map((student) => (
              <React.Fragment key={student.id}>
                <tr className={expandedStudent === student.id ? 'expanded' : ''}>
                  <td className="expand-cell">
                    <button 
                      className="expand-button" 
                      onClick={() => toggleExpand(student.id)}
                    >
                      <i className={`fas fa-chevron-${expandedStudent === student.id ? 'down' : 'right'}`}></i>
                    </button>
                  </td>
                  <td>{student.studentName}</td>
                  <td>{student.studentId}</td>
                  <td>{student.degreeName}</td>
                  <td>
                    <span className={`status-badge ${getStatusClass(student.status)}`}>
                      {student.status}
                    </span>
                  </td>
                  <td>
                    {student.status === 'PENDING' && (
                      <button 
                        className="action-button queue-button"
                        onClick={() => onStatusChange(student.id, 'QUEUED')}
                      >
                        Queue for Certificate
                      </button>
                    )}
                    {student.status === 'ISSUED' && student.certificateId && (
                      <div className="button-group">
                        <button 
                          className="action-button download-button"
                          onClick={(e) => handleDownloadCertificate(e, student)}
                          disabled={isDownloading[student.id]}
                        >
                          {isDownloading[student.id] ? 
                            <><i className="fas fa-spinner fa-spin"></i> Processing...</> : 
                            <><i className="fas fa-download"></i> Download</>
                          }
                        </button>
                        <button 
                          className="action-button email-button"
                          onClick={(e) => handleEmailCertificate(e, student)}
                          disabled={isEmailingCertificate[student.id]}
                        >
                          {isEmailingCertificate[student.id] ? 
                            <><i className="fas fa-spinner fa-spin"></i> Sending...</> : 
                            <><i className="fas fa-envelope"></i> Email</>
                          }
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
                {expandedStudent === student.id && (
                  <tr className="details-row">
                    <td colSpan="6">
                      <div className="student-details">
                        <div className="detail-group">
                          <label>Email:</label>
                          <span>{student.email}</span>
                        </div>
                        <div className="detail-group">
                          <label>University:</label>
                          <span>{student.universityName}</span>
                        </div>
                        <div className="detail-group">
                          <label>Graduation Date:</label>
                          <span>{formatDate(student.graduationDate)}</span>
                        </div>
                        {student.certificateId && (
                          <div className="detail-group">
                            <label>Certificate ID:</label>
                            <span>{student.certificateId}</span>
                          </div>
                        )}
                        {student.transactionId && (
                          <div className="detail-group">
                            <label>Blockchain Transaction:</label>
                            <span className="transaction-id">{student.transactionId}</span>
                          </div>
                        )}
                        {student.status === 'ISSUED' && student.certificateId && (
                          <div className="detail-group detail-actions">
                            <button 
                              className="detail-button download-button"
                              onClick={(e) => handleDownloadCertificate(e, student)}
                              disabled={isDownloading[student.id]}
                            >
                              {isDownloading[student.id] ? 
                                <><i className="fas fa-spinner fa-spin"></i> Processing...</> : 
                                <><i className="fas fa-download"></i> Download Certificate</>
                              }
                            </button>
                            <button 
                              className="detail-button email-button"
                              onClick={(e) => handleEmailCertificate(e, student)}
                              disabled={isEmailingCertificate[student.id]}
                            >
                              {isEmailingCertificate[student.id] ? 
                                <><i className="fas fa-spinner fa-spin"></i> Sending...</> : 
                                <><i className="fas fa-envelope"></i> Email Certificate</>
                              }
                            </button>
                            <p className="note">Note: Student record will be removed after download</p>
                          </div>
                        )}
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default StudentList;