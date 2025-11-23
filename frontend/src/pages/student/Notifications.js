import React, { useState, useEffect } from 'react';
import api from '../../services/api';

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    loadNotifications();
  }, []);

  const loadNotifications = async () => {
    try {
      const response = await api.get('/student/notificaciones');
      setNotifications(response.data);
    } catch (err) {
      setError('Error al cargar notificaciones');
    }
  };

  const handleMarkRead = async (id) => {
    try {
      await api.post(`/student/notificaciones/${id}/leer`);
      loadNotifications();
    } catch (err) {
      setError('Error al marcar como leída');
    }
  };

  const getTypeIcon = (type) => {
    const icons = {
      RESERVA_CONFIRMADA: '✅',
      RESERVA_MODIFICADA: '✏️',
      RESERVA_CANCELADA: '❌',
      EQUIPO_MANTENIMIENTO: '🔧',
      RECORDATORIO: '⏰'
    };
    return icons[type] || '📢';
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div>
      <h2>Notificaciones</h2>
      {error && <div className="error">{error}</div>}

      <div className="card">
        {notifications.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
            No tienes notificaciones
          </div>
        ) : (
          <div>
            {notifications.map(notification => (
              <div
                key={notification.id}
                style={{
                  padding: '15px',
                  borderBottom: '1px solid #eee',
                  backgroundColor: notification.read ? 'white' : '#f0f8ff',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}
              >
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '5px' }}>
                    <span style={{ fontSize: '24px' }}>{getTypeIcon(notification.type)}</span>
                    <span style={{ fontWeight: notification.read ? 'normal' : 'bold' }}>
                      {notification.type.replace(/_/g, ' ')}
                    </span>
                  </div>
                  <p style={{ margin: '5px 0', color: '#333' }}>{notification.message}</p>
                  <small style={{ color: '#666' }}>{formatDate(notification.createdAt)}</small>
                </div>
                {!notification.read && (
                  <button
                    onClick={() => handleMarkRead(notification.id)}
                    className="btn-secondary"
                    style={{ marginLeft: '15px' }}
                  >
                    Marcar como leída
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
