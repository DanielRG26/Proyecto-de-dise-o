import React, { useState } from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Labs from './Labs';
import Equipment from './Equipment';
import Users from './Users';
import Reports from './Reports';

export default function AdminDashboard() {
  const { user, logout } = useAuth();
  const [activeTab, setActiveTab] = useState('labs');

  return (
    <div>
      <nav className="navbar">
        <div className="navbar-content">
          <h1>🔬 Lab Turnos - Admin</h1>
          <div className="navbar-user">
            <span>{user.fullName}</span>
            <button onClick={logout} className="btn-secondary">Salir</button>
          </div>
        </div>
      </nav>
      
      <div className="container">
        <div className="tabs">
          <Link to="/admin/labs">
            <button className={`tab ${activeTab === 'labs' ? 'active' : ''}`} onClick={() => setActiveTab('labs')}>
              Laboratorios
            </button>
          </Link>
          <Link to="/admin/equipment">
            <button className={`tab ${activeTab === 'equipment' ? 'active' : ''}`} onClick={() => setActiveTab('equipment')}>
              Equipos
            </button>
          </Link>
          <Link to="/admin/users">
            <button className={`tab ${activeTab === 'users' ? 'active' : ''}`} onClick={() => setActiveTab('users')}>
              Usuarios
            </button>
          </Link>
          <Link to="/admin/reports">
            <button className={`tab ${activeTab === 'reports' ? 'active' : ''}`} onClick={() => setActiveTab('reports')}>
              Reportes
            </button>
          </Link>
        </div>

        <Routes>
          <Route path="/" element={<Labs />} />
          <Route path="/labs" element={<Labs />} />
          <Route path="/equipment" element={<Equipment />} />
          <Route path="/users" element={<Users />} />
          <Route path="/reports" element={<Reports />} />
        </Routes>
      </div>
    </div>
  );
}
