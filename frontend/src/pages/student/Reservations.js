import React, { useState, useEffect } from 'react';
import api from '../../services/api';

export default function Reservations() {
  const [reservations, setReservations] = useState([]);
  const [showModifyModal, setShowModifyModal] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState(null);
  const [formData, setFormData] = useState({
    date: '',
    startTime: '',
    endTime: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadReservations();
  }, []);

  const loadReservations = async () => {
    try {
      const response = await api.get('/student/reservas');
      setReservations(response.data);
    } catch (err) {
      setError('Error al cargar reservas');
    }
  };

  const openModifyModal = (reservation) => {
    setSelectedReservation(reservation);
    setFormData({
      date: reservation.date,
      startTime: reservation.startTime,
      endTime: reservation.endTime
    });
    setShowModifyModal(true);
  };

  const handleModify = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    try {
      await api.put(`/student/reservas/${selectedReservation.id}`, formData);
      setSuccess('Reserva modificada exitosamente');
      setShowModifyModal(false);
      loadReservations();
    } catch (err) {
      setError(err.response?.data?.error || 'Error al modificar reserva');
    }
  };

  const handleCancel = async (id) => {
    if (!window.confirm('¿Estás seguro de cancelar esta reserva?')) return;
    
    setError('');
    setSuccess('');
    
    try {
      await api.delete(`/student/reservas/${id}`);
      setSuccess('Reserva cancelada exitosamente');
      loadReservations();
    } catch (err) {
      setError(err.response?.data?.error || 'Error al cancelar reserva');
    }
  };

  const getStatusBadge = (status) => {
    const badges = {
      ACTIVA: 'badge-success',
      MODIFICADA: 'badge-info',
      CANCELADA: 'badge-danger',
      COMPLETADA: 'badge-warning'
    };
    return <span className={`badge ${badges[status]}`}>{status}</span>;
  };

  const canModifyOrCancel = (reservation) => {
    return reservation.status === 'ACTIVA' || reservation.status === 'MODIFICADA';
  };

  return (
    <div>
      <h2>Mis Reservas</h2>
      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Horario</th>
              <th>Laboratorio</th>
              <th>Equipo</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {reservations.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
                  No tienes reservas
                </td>
              </tr>
            ) : (
              reservations.map(reservation => (
                <tr key={reservation.id}>
                  <td>{reservation.date}</td>
                  <td>{reservation.startTime} - {reservation.endTime}</td>
                  <td>{reservation.labId}</td>
                  <td>{reservation.equipmentId}</td>
                  <td>{getStatusBadge(reservation.status)}</td>
                  <td>
                    {canModifyOrCancel(reservation) && (
                      <div className="action-buttons">
                        <button 
                          onClick={() => openModifyModal(reservation)} 
                          className="btn-secondary"
                        >
                          Modificar
                        </button>
                        <button 
                          onClick={() => handleCancel(reservation.id)} 
                          className="btn-danger"
                        >
                          Cancelar
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showModifyModal && (
        <div className="modal-overlay" onClick={() => setShowModifyModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Modificar Reserva</h2>
            <form onSubmit={handleModify}>
              <div className="form-group">
                <label>Fecha</label>
                <input
                  type="date"
                  value={formData.date}
                  onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                  required
                  min={new Date().toISOString().split('T')[0]}
                />
              </div>
              <div className="form-group">
                <label>Hora Inicio</label>
                <input
                  type="time"
                  value={formData.startTime}
                  onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Hora Fin</label>
                <input
                  type="time"
                  value={formData.endTime}
                  onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" onClick={() => setShowModifyModal(false)} className="btn-secondary">
                  Cancelar
                </button>
                <button type="submit" className="btn-primary">Guardar Cambios</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
