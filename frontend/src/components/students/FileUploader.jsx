// src/components/students/FileUploader.jsx
import React, { useState, useRef } from 'react';
import '../../styles/FileUploader.css';

const FileUploader = ({ onFileUpload }) => {
  const [isDragging, setIsDragging] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploadError, setUploadError] = useState(null);
  const fileInputRef = useRef(null);

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    setUploadError(null);
    
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const file = e.dataTransfer.files[0];
      handleFileSelection(file);
    }
  };

  const handleFileInput = (e) => {
    setUploadError(null);
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0];
      handleFileSelection(file);
    }
  };

  const handleFileSelection = (file) => {
    // Check if file is Excel
    const validTypes = [
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'application/vnd.oasis.opendocument.spreadsheet'
    ];
    
    if (!validTypes.includes(file.type)) {
      setUploadError('Please upload an Excel file (.xls or .xlsx)');
      return;
    }
    
    setSelectedFile(file);
  };

  const handleUpload = () => {
    if (selectedFile) {
      try {
        onFileUpload(selectedFile);
        setSelectedFile(null); // Reset after upload
      } catch (error) {
        setUploadError('Error uploading file. Please try again.');
        console.error('Upload error:', error);
      }
    }
  };

  const openFileDialog = () => {
    fileInputRef.current.click();
  };

  return (
    <div className="file-uploader">
      {uploadError && (
        <div className="upload-error">
          <p>{uploadError}</p>
        </div>
      )}
      
      <div 
        className={`drop-zone ${isDragging ? 'dragging' : ''} ${selectedFile ? 'has-file' : ''}`}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={openFileDialog}
      >
        <input 
          type="file" 
          ref={fileInputRef} 
          className="file-input" 
          accept=".xls,.xlsx,.ods" 
          onChange={handleFileInput} 
        />
        
        <div className="upload-icon">
          <i className="fas fa-file-excel"></i>
        </div>
        
        {selectedFile ? (
          <div className="file-info">
            <p className="file-name">{selectedFile.name}</p>
            <p className="file-size">{Math.round(selectedFile.size / 1024)} KB</p>
          </div>
        ) : (
          <div className="upload-message">
            <p>Drag & Drop an Excel file here</p>
            <p className="upload-hint">- or -</p>
            <button className="browse-button" onClick={openFileDialog}>
              Browse Files
            </button>
          </div>
        )}
      </div>
      
      {selectedFile && (
        <div className="upload-actions">
          <button className="upload-button" onClick={handleUpload}>
            <i className="fas fa-upload"></i> Upload Student Data
          </button>
          <button className="cancel-button" onClick={() => {
            setSelectedFile(null);
            setUploadError(null);
          }}>
            Cancel
          </button>
        </div>
      )}
    </div>
  );
};

export default FileUploader;