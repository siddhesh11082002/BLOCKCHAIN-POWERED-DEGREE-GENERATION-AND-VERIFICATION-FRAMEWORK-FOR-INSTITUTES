// src/components/common/Sidebar.jsx
import { NavLink } from 'react-router-dom';
import '../../styles/Sidebar.css';

const Sidebar = ({ activePage }) => {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <h2>Certificate System</h2>
      </div>
      <nav className="sidebar-nav">
        <ul>
          <li>
            <NavLink to="/dashboard" className={activePage === 'dashboard' ? 'active' : ''}>
              <i className="fas fa-home"></i>
              <span>Dashboard</span>
            </NavLink>
          </li>
          <li>
            <NavLink to="/students" className={activePage === 'students' ? 'active' : ''}>
              <i className="fas fa-user-graduate"></i>
              <span>Students</span>
            </NavLink>
          </li>
          <li>
            <NavLink to="/degrees" className={activePage === 'degrees' ? 'active' : ''}>
              <i className="fas fa-graduation-cap"></i>
              <span>Degrees</span>
            </NavLink>
          </li>
          <li>
            <NavLink to="/profile" className={activePage === 'profile' ? 'active' : ''}>
              <i className="fas fa-cog"></i>
              <span>Profile</span>
            </NavLink>
          </li>
        </ul>
      </nav>
    </aside>
  );
};

export default Sidebar;