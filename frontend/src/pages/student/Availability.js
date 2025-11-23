import React, { useState, useEffect } from 'react';
import api from '../../services/api';

export default function Availability() {
  const [availability, setAvailability] = useState([]);
  const [labsAvailable, setLabsAvailable] = useState([]);
  const [showReserveModal, setShowReserveModal] = useState(false);
  const [selectedLab, setSelectedLab] = useState(null);
  const [formData, setFormData] = useState({
    labCode: '',
    equipmentIdentifier: '',
    date: '',
    startTime: '',
    endTime: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadAvailability();
    loadLabsAvailable();
  }, []);

  const loadAvailability = async () => {
    try {
      const response = await api.get('/student/availability');
      setAvailability(response.data);
    } catch (err) {
      setError('Error al cargar disponibilidad');
    }
  };

  const loadLabsAvailable = async () => {
    try {
      const response = await api.get('/student/labs/available');
      setLabsAvailable(response.data);
    } catch (err) {
      setError('Error al cargar laboratorios');
    }
  };

  const openReserveModal = (lab) => {
    setSelectedLab(lab);
    setFormData({
      ...formData,
      labCode: lab.laboratorio
    });
    setShowReserveModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    try {
      await api.post('/student/reservas', formData);
      setSuccess('Reserva creada exitosamente');
      setShowReserveModal(false);
      setFormData({
        labCode: '',
        equipmentIdentifier: '',
        date: '',
        startTime: '',
        endTime: ''
      });
      loadAvailability();
      loadLabsAvailable();
    } catch (err) {
      setError(err.response?.data?.error || 'Error al crear reserva');
    }
  };

  const getAvailableEquipment = () => {
    if (!selectedLab) return [];
    const lab = labsAvailable.find(l => l.laboratorio === selectedLab.laboratorio);
    return lab ? lab.equipos.filter(e => e.status === 'DISPONIBLE') : [];
  };

  return (
    <div>
      <h2>Disponibilidad de Laboratorios</h2>
      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}

      <div className="grid grid-3" style={{ marginBottom: '30px' }}>
        {availability.map((lab, index) => (
          <div key={index} className="stat-card">
            <h3>{lab.laboratorio}</h3>
            <div style={{ marginTop: '15px' }}>
              <div style={{ marginBottom: '8px' }}>
                <span style={{ color: '#28a745', fontWeight: 'bold', fontSize: '20px' }}>
                  {lab.equipos_disponibles}
                </span>
                <span style={{ fontSize: '14px', color: '#666' }}> Disponibles</span>
              </div>
              <div style={{ marginBottom: '8px' }}>
                <span style={{ color: '#ffc107', fontWeight: 'bold', fontSize: '20px' }}>
                  {lab.equipos_en_uso}
                </span>
                <span style={{ fontSize: '14px', color: '#666' }}> En Uso</span>
              </div>
              <div style={{ marginBottom: '15px' }}>
                <span style={{ color: '#dc3545', fontWeight: 'bold', fontSize: '20px' }}>
                  {lab.equipos_mantenimiento}
                </span>
                <span style={{ fontSize: '14px', color: '#666' }}> Mantenimiento</span>
              </div>
              <button 
                onClick={() => openReserveModal(lab)} 
                className="btn-primary"
                style={{ width: '100%' }}
                disabled={lab.equipos_disponibles === 0}
              >
                Reservar
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="card">
        <h3>Equipos por Laboratorio</h3>
        {labsAvailable.map((lab, index) => (
          <div key={index} style={{ marginBottom: '20px' }}>
            <h4>{lab.laboratorio}</h4>
            <table>
              <thead>
                <tr>
                  <th>Identificador</th>
                  <th>Tipo</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {lab.equipos.map((eq, eqIndex) => (
                  <tr key={eqIndex}>
                    <td>{eq.identifier}</td>
                    <td>{eq.type}</td>
                    <td>
                      <span className={`badge ${
                        eq.status === 'DISPONIBLE' ? 'badge-success' :
                        eq.status === 'EN_USO' ? 'badge-warning' : 'badge-danger'
                      }`}>
                        {eq.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ))}
      </div>

      {showReserveModal && (
        <div className="modal-overlay" onClick={() => setShowReserveModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Nueva Reserva - {selectedLab.laboratorio}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Equipo</label>
                <select
                  value={formData.equipmentIdentifier}
                  onChange={(e) => setFormData({ ...formData, equipmentIdentifier: e.target.value })}
                  required
                >
                  <option value="">Seleccionar equipo...</option>
                  {getAvailableEquipment().map((eq, index) => (
                    <option key={index} value={eq.identifier}>
                      {eq.identifier} - {eq.type}
                    </option>
                  ))}
                </select>
              </div>
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
                <button type="button" onClick={() => setShowReserveModal(false)} className="btn-secondary">
                  Cancelar
                </button>
                <button type="submit" className="btn-primary">Reservar</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
