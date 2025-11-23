import React, { useState, useEffect } from 'react';
import api from '../../services/api';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const response = await api.get('/admin/users');
      setUsers(response.data);
    } catch (err) {
      setError('Error al cargar usuarios');
    }
  };

  const handleChangeRole = async (userId, currentRole) => {
    const newRole = currentRole === 'ADMINISTRADOR' ? 'ESTUDIANTE' : 'ADMINISTRADOR';
    if (!window.confirm(`¿Cambiar rol a ${newRole}?`)) return;
    
    try {
      await api.post(`/admin/users/${userId}/role`, { role: newRole });
      loadUsers();
    } catch (err) {
      setError('Error al cambiar rol');
    }
  };

  const handleDelete = async (userId) => {
    if (!window.confirm('¿Estás seguro de eliminar este usuario?')) return;
    
    try {
      await api.delete(`/admin/users/${userId}`);
      loadUsers();
    } catch (err) {
      setError('Error al eliminar usuario');
    }
  };

  return (
    <div>
      <h2>Gestión de Usuarios</h2>
      {error && <div className="error">{error}</div>}

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Email</th>
              <th>Teléfono</th>
              <th>Rol</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.fullName}</td>
                <td>{user.email}</td>
                <td>{user.phone}</td>
                <td>
                  <span className={`badge ${user.role === 'ADMINISTRADOR' ? 'badge-danger' : 'badge-info'}`}>
                    {user.role}
                  </span>
                </td>
                <td>
                  <div className="action-buttons">
                    <button onClick={() => handleChangeRole(user.id, user.role)} className="btn-secondary">
                      Cambiar Rol
                    </button>
                    <button onClick={() => handleDelete(user.id)} className="btn-danger">
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
