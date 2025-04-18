// src/components/dashboard/DegreePreviewBox.jsx
import { useState } from 'react';
import '../../styles/DegreePreviewBox.css';

const DegreePreviewBox = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  return (
    <div className="degree-preview-box">
      <div className="preview-header">
        <h3>Degree Certificate Preview</h3>
      </div>
      <div className="preview-content">
        <div className="preview-image">
          {/* Simple placeholder certificate instead of an image */}
          <div className="certificate-placeholder">
            <div className="certificate-header">
              <h2>University Name</h2>
              <h3>Certificate of Completion</h3>
            </div>
            <div className="certificate-body">
              <p>This certifies that</p>
              <h4>Student Name</h4>
              <p>has successfully completed the requirements for</p>
              <h4>Degree Name</h4>
              <p>Awarded on: Graduation Date</p>
            </div>
            <div className="certificate-footer">
              <div className="signature">University President</div>
              <div className="signature">Dean of Faculty</div>
            </div>
          </div>
        </div>
      </div>

      {isModalOpen && (
        <div className="preview-modal">
          <div className="modal-content">
            <span className="close-button" onClick={() => setIsModalOpen(false)}>
              &times;
            </span>
            <h2>Certificate Template</h2>
            <div className="certificate-placeholder large">
              <div className="certificate-header">
                <h2>University Name</h2>
                <h3>Certificate of Completion</h3>
              </div>
              <div className="certificate-body">
                <p>This certifies that</p>
                <h4>Student Name</h4>
                <p>has successfully completed the requirements for</p>
                <h4>Degree Name</h4>
                <p>Awarded on: Graduation Date</p>
              </div>
              <div className="certificate-footer">
                <div className="signature">University President</div>
                <div className="signature">Dean of Faculty</div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DegreePreviewBox;