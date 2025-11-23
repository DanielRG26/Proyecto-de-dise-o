import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import AdminDashboard from './pages/admin/AdminDashboard';
import StudentDashboard from './pages/student/StudentDashboard';
import './App.css';

function PrivateRoute({ children, role }) {
  const { user, loading } = useAuth();
  
  if (loading) return <div className="loading">Cargando...</div>;
  if (!user) return <Navigate to="/login" />;
  if (role && user.role !== role) return <Navigate to="/" />;
  
  return children;
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/admin/*" element={
            <PrivateRoute role="ADMINISTRADOR">
              <AdminDashboard />
            </PrivateRoute>
          } />
          <Route path="/student/*" element={
            <PrivateRoute role="ESTUDIANTE">
              <StudentDashboard />
            </PrivateRoute>
          } />
          <Route path="/" element={<Home />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

function Home() {
  const { user } = useAuth();
  
  if (!user) return <Navigate to="/login" />;
  if (user.role === 'ADMINISTRADOR') return <Navigate to="/admin" />;
  if (user.role === 'ESTUDIANTE') return <Navigate to="/student" />;
  
  return <Navigate to="/login" />;
}

export default App;
