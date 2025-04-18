// src/components/dashboard/StatsOverview.jsx
import React from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';
import '../../styles/StatsOverview.css';

const StatsOverview = ({ statusCounts }) => {
  // Default values if statusCounts is null/undefined
  const counts = statusCounts || { pending: 0, queued: 0, issued: 0 };
  
  const data = [
    { name: 'Pending', value: counts.pending || 0, color: '#FFA500' },
    { name: 'Queued', value: counts.queued || 0, color: '#3498db' },
    { name: 'Issued', value: counts.issued || 0, color: '#2ecc71' }
  ];

  const total = data.reduce((sum, item) => sum + item.value, 0);

  // Calculate percentages
  const dataWithPercentage = data.map(item => ({
    ...item,
    percentage: total > 0 ? Math.round((item.value / total) * 100) : 0
  }));

  return (
    <div className="stats-overview">
      <div className="stats-header">
        <h3>Certificate Status Overview</h3>
      </div>
      <div className="stats-content">
        <div className="stats-chart">
          {total > 0 ? (
            <ResponsiveContainer width="100%" height={250}>
              <PieChart>
                <Pie
                  data={dataWithPercentage}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                  label={({ name, percentage }) => `${name}: ${percentage}%`}
                >
                  {dataWithPercentage.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => [`${value} students`, 'Count']} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="no-data">
              <p>No certificate data available</p>
            </div>
          )}
        </div>
        
        <div className="stats-summary">
          <div className="stat-item pending">
            <div className="stat-icon">🎓</div>
            <div className="stat-info">
              <span className="stat-value">{counts.pending || 0}</span>
              <span className="stat-label">Pending ({dataWithPercentage[0].percentage}%)</span>
            </div>
          </div>
          <div className="stat-item queued">
            <div className="stat-icon">📝</div>
            <div className="stat-info">
              <span className="stat-value">{counts.queued || 0}</span>
              <span className="stat-label">Queued ({dataWithPercentage[1].percentage}%)</span>
            </div>
          </div>
          <div className="stat-item issued">
            <div className="stat-icon">✅</div>
            <div className="stat-info">
              <span className="stat-value">{counts.issued || 0}</span>
              <span className="stat-label">Issued ({dataWithPercentage[2].percentage}%)</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StatsOverview;