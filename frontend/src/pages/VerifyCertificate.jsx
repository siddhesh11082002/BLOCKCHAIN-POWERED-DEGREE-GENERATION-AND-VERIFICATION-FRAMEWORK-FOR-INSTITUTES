// src/pages/VerifyCertificate.jsx
import React, { useState, useRef } from 'react';
import { QrReader } from 'react-qr-reader';
import api from '../services/api.jsx';
import '../styles/VerifyCertificate.css';

const VerifyCertificate = () => {
  const [scanning, setScanning] = useState(false);
  const [verificationResult, setVerificationResult] = useState(null);
  const [qrData, setQrData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const fileInputRef = useRef(null);

  const handleQrScan = async (result) => {
    if (result) {
      setScanning(false);
      setQrData(result.text);
      await verifyCertificate(result.text);
    }
  };

  const handleQrError = (error) => {
    console.error(error);
    setError('Failed to scan QR code. Please try again or upload a certificate PDF.');
    setScanning(false);
  };

  const verifyCertificate = async (qrCodeData) => {
    try {
      setIsLoading(true);
      setError(null);

      // Send the QR data to the verification endpoint
      const response = await api.post('/certificates/verify/qr', { qrData: qrCodeData });
      
      setVerificationResult(response);
    } catch (err) {
      console.error('Verification failed:', err);
      setError(err.message || 'Failed to verify certificate');
      setVerificationResult({ valid: false });
    } finally {
      setIsLoading(false);
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (file.type !== 'application/pdf') {
      setError('Please upload a valid PDF certificate');
      return;
    }

    try {
      setIsLoading(true);
      setError(null);

      const formData = new FormData();
      formData.append('pdf', file);

      const response = await api.post('/certificates/verify', formData);
      
      setVerificationResult(response);
    } catch (err) {
      console.error('Verification failed:', err);
      setError(err.message || 'Failed to verify certificate');
      setVerificationResult({ valid: false });
    } finally {
      setIsLoading(false);
    }
  };

  const toggleScanner = () => {
    setScanning(!scanning);
    setError(null);
  };

  const uploadPdf = () => {
    fileInputRef.current.click();
  };

  const manualInputQrData = () => {
    const data = prompt('Enter the QR code data:');
    if (data) {
      setQrData(data);
      verifyCertificate(data);
    }
  };

  const extractMonthAndYear = (dateStr) => {
    // Check if we have a string
    if (!dateStr || typeof dateStr !== 'string') {
      return "Unknown date";
    }
    
    // Log for debugging
    console.log("Extracting from:", dateStr);
    
    // Example format: "Sun Jun 15 00:00:00 IST 2025"
    // We'll use regex to extract the month and year
    const regex = /\w{3}\s(\w{3}).*(\d{4})/;
    const match = dateStr.match(regex);
    
    if (match && match.length >= 3) {
      const month = match[1]; // "Jun"
      const year = match[2];  // "2025"
      return `${month} ${year}`;
    }
    
    // If extraction fails, return the original
    return dateStr;
  }

  const renderVerificationResult = () => {
    if (!verificationResult) return null;
  
    const { valid, certificateDetails } = verificationResult;
    // console.log("Raw graduation date:", certificateDetails.graduationDate);
    return (
      <div className={`verification-result ${valid ? 'valid' : 'invalid'}`}>
        <div className="result-header">
          <div className="result-icon">
            {valid ? (
              <i className="fas fa-check-circle"></i>
            ) : (
              <i className="fas fa-times-circle"></i>
            )}
          </div>
          <h2>Certificate {valid ? 'Verified' : 'Invalid'}</h2>
        </div>
  
        {valid && certificateDetails && (
          <div className="certificate-verification-confirmation">
            <p className="verification-message">
              This is to confirm that 
              <span className="highlight-field">{certificateDetails.studentName}</span> 
              has successfully completed 
              <span className="highlight-field">{certificateDetails.degreeName}</span> 
              from <span className="highlight-field">{certificateDetails.universityName}</span> 
              on <span className="highlight-field">{extractMonthAndYear(certificateDetails.graduationDate)}</span>.
            </p>
            <div className="verification-seal">
              <i className="fas fa-certificate"></i>
              <span>Blockchain Verified</span>
            </div>
          </div>
        )}
  
        {!valid && (
          <p className="invalid-message">
            This certificate could not be verified. It may be tampered with or not issued by our system.
          </p>
        )}
      </div>
    );
  };

  return (
    <div className="verify-certificate-container">
      <header className="verify-header">
        <h1>Certificate Verification</h1>
        <p>Verify the authenticity of academic certificates</p>
      </header>
  
      <main className="verify-content">
        {error && <div className="error-message">{error}</div>}
  
        <div className="verification-methods">
          <div className="method-card">
            <div className="method-icon">
              <i className="fas fa-file-pdf"></i>
            </div>
            <h3>Upload Certificate</h3>
            <p>Upload the PDF certificate file for verification</p>
            <input
              type="file"
              accept="application/pdf"
              onChange={handleFileUpload}
              ref={fileInputRef}
              style={{ display: 'none' }}
            />
            <button onClick={uploadPdf} className="method-button">
              Upload PDF
            </button>
          </div>
        </div>
  
        {isLoading && (
          <div className="loading-verification">
            <div className="loading-spinner"></div>
            <p>Verifying certificate...</p>
          </div>
        )}
  
        {renderVerificationResult()}
      </main>
  
      <footer className="verify-footer">
        <p>
          This verification tool confirms if a certificate was legitimately issued by our system
          and has not been tampered with.
        </p>
      </footer>
    </div>
  );
};

export default VerifyCertificate;