import React, { useState, useEffect } from 'react';
import api from '../../services/api';

export default function Labs() {
  const [labs, setLabs] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [showScheduleModal, setShowScheduleModal] = useState(false);
  const [selectedLab, setSelectedLab] = useState(null);
  const [formData, setFormData] = useState({ code: '', name: '', capacity: '' });
  const [scheduleSlots, setScheduleSlots] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    loadLabs();
  }, []);

  const loadLabs = async () => {
    try {
      const response = await api.get('/admin/labs');
      setLabs(response.data);
    } catch (err) {
      setError('Error al cargar laboratorios');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/labs', formData);
      setShowModal(false);
      setFormData({ code: '', name: '', capacity: '' });
      loadLabs();
    } catch (err) {
      setError(err.response?.data?.error || 'Error al crear laboratorio');
    }
  };

  const handleUpdateCapacity = async (id, capacity) => {
    try {
      await api.put(`/admin/labs/${id}/capacity/${capacity}`);
      loadLabs();
    } catch (err) {
      setError('Error al actualizar aforo');
    }
  };

  const openScheduleModal = (lab) => {
    setSelectedLab(lab);
    setScheduleSlots(lab.schedule.length > 0 ? lab.schedule : [
      { dayOfWeek: 'MONDAY', start: '08:00', end: '17:00' }
    ]);
    setShowScheduleModal(true);
  };

  const addScheduleSlot = () => {
    setScheduleSlots([...scheduleSlots, { dayOfWeek: 'MONDAY', start: '08:00', end: '17:00' }]);
  };

  const updateScheduleSlot = (index, field, value) => {
    const updated = [...scheduleSlots];
    updated[index][field] = value;
    setScheduleSlots(updated);
  };

  const removeScheduleSlot = (index) => {
    setScheduleSlots(scheduleSlots.filter((_, i) => i !== index));
  };

  const handleScheduleSubmit = async () => {
    try {
      await api.put(`/admin/labs/${selectedLab.id}/schedule`, scheduleSlots);
      setShowScheduleModal(false);
      loadLabs();
    } catch (err) {
      setError('Error al configurar horario');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
        <h2>Gestión de Laboratorios</h2>
        <button onClick={() => setShowModal(true)} className="btn-primary">
          + Nuevo Laboratorio
        </button>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>Código</th>
              <th>Nombre</th>
              <th>Aforo</th>
              <th>Horarios</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {labs.map(lab => (
              <tr key={lab.id}>
                <td>{lab.code}</td>
                <td>{lab.name}</td>
                <td>
                  <input
                    type="number"
                    value={lab.capacity}
                    onChange={(e) => handleUpdateCapacity(lab.id, e.target.value)}
                    style={{ width: '80px' }}
                  />
                </td>
                <td>{lab.schedule.length} configurados</td>
                <td>
                  <button onClick={() => openScheduleModal(lab)} className="btn-secondary">
                    Configurar Horario
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Nuevo Laboratorio</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Código</label>
                <input
                  value={formData.code}
                  onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Nombre</label>
                <input
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Aforo</label>
                <input
                  type="number"
                  value={formData.capacity}
                  onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" onClick={() => setShowModal(false)} className="btn-secondary">
                  Cancelar
                </button>
                <button type="submit" className="btn-primary">Crear</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showScheduleModal && (
        <div className="modal-overlay" onClick={() => setShowScheduleModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Configurar Horario - {selectedLab.code}</h2>
            {scheduleSlots.map((slot, index) => (
              <div key={index} style={{ marginBottom: '15px', padding: '10px', border: '1px solid #ddd', borderRadius: '6px' }}>
                <div className="form-group">
                  <label>Día</label>
                  <select
                    value={slot.dayOfWeek}
                    onChange={(e) => updateScheduleSlot(index, 'dayOfWeek', e.target.value)}
                  >
                    <option value="MONDAY">Lunes</option>
                    <option value="TUESDAY">Martes</option>
                    <option value="WEDNESDAY">Miércoles</option>
                    <option value="THURSDAY">Jueves</option>
                    <option value="FRIDAY">Viernes</option>
                    <option value="SATURDAY">Sábado</option>
                    <option value="SUNDAY">Domingo</option>
                  </select>
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                  <div className="form-group">
                    <label>Inicio</label>
                    <input
                      type="time"
                      value={slot.start}
                      onChange={(e) => updateScheduleSlot(index, 'start', e.target.value)}
                    />
                  </div>
                  <div className="form-group">
                    <label>Fin</label>
                    <input
                      type="time"
                      value={slot.end}
                      onChange={(e) => updateScheduleSlot(index, 'end', e.target.value)}
                    />
                  </div>
                </div>
                <button type="button" onClick={() => removeScheduleSlot(index)} className="btn-danger" style={{ marginTop: '10px' }}>
                  Eliminar
                </button>
              </div>
            ))}
            <button type="button" onClick={addScheduleSlot} className="btn-secondary" style={{ marginBottom: '15px' }}>
              + Agregar Horario
            </button>
            <div className="modal-actions">
              <button onClick={() => setShowScheduleModal(false)} className="btn-secondary">
                Cancelar
              </button>
              <button onClick={handleScheduleSubmit} className="btn-primary">
                Guardar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
