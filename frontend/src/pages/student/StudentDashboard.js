import React, { useState } from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Availability from './Availability';
import Reservations from './Reservations';
import Notifications from './Notifications';

export default function StudentDashboard() {
  const { user, logout } = useAuth();
  const [activeTab, setActiveTab] = useState('availability');

  return (
    <div>
      <nav className="navbar">
        <div className="navbar-content">
          <h1>🔬 Lab Turnos - Estudiante</h1>
          <div className="navbar-user">
            <span>{user.fullName}</span>
            <button onClick={logout} className="btn-secondary">Salir</button>
          </div>
        </div>
      </nav>
      
      <div className="container">
        <div className="tabs">
          <Link to="/student/availability">
            <button className={`tab ${activeTab === 'availability' ? 'active' : ''}`} onClick={() => setActiveTab('availability')}>
              Disponibilidad
            </button>
          </Link>
          <Link to="/student/reservations">
            <button className={`tab ${activeTab === 'reservations' ? 'active' : ''}`} onClick={() => setActiveTab('reservations')}>
              Mis Reservas
            </button>
          </Link>
          <Link to="/student/notifications">
            <button className={`tab ${activeTab === 'notifications' ? 'active' : ''}`} onClick={() => setActiveTab('notifications')}>
              Notificaciones
            </button>
          </Link>
        </div>

        <Routes>
          <Route path="/" element={<Availability />} />
          <Route path="/availability" element={<Availability />} />
          <Route path="/reservations" element={<Reservations />} />
          <Route path="/notifications" element={<Notifications />} />
        </Routes>
      </div>
    </div>
  );
}
