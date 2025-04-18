// src/components/dashboard/DepartmentStatsTable.jsx
import '../../styles/DepartmentStatsTable.css';

const DepartmentStatsTable = ({ departmentStats }) => {
  return (
    <div className="department-stats-table">
      <div className="stats-header">
        <h3>Department Statistics</h3>
      </div>
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Department</th>
              <th>Pending</th>
              <th>Queued</th>
              <th>Issued</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            {departmentStats.map((department, index) => {
              const total = department.pending + department.queued + department.issued;
              return (
                <tr key={index}>
                  <td>{department.department}</td>
                  <td>{department.pending}</td>
                  <td>{department.queued}</td>
                  <td>{department.issued}</td>
                  <td>{total}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default DepartmentStatsTable;