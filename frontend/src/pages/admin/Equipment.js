import React, { useState, useEffect } from 'react';
import api from '../../services/api';

export default function Equipment() {
  const [equipment, setEquipment] = useState([]);
  const [labs, setLabs] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({ labId: '', identifier: '', type: '' });
  const [error, setError] = useState('');

  useEffect(() => {
    loadEquipment();
    loadLabs();
  }, []);

  const loadEquipment = async () => {
    try {
      const response = await api.get('/admin/equipment');
      setEquipment(response.data);
    } catch (err) {
      setError('Error al cargar equipos');
    }
  };

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
      await api.post(`/admin/labs/${formData.labId}/equipment`, {
        identifier: formData.identifier,
        type: formData.type
      });
      setShowModal(false);
      setFormData({ labId: '', identifier: '', type: '' });
      loadEquipment();
    } catch (err) {
      setError(err.response?.data?.error || 'Error al agregar equipo');
    }
  };

  const handleBlock = async (id) => {
    try {
      await api.post(`/admin/equipment/${id}/block`);
      loadEquipment();
    } catch (err) {
      setError('Error al bloquear equipo');
    }
  };

  const handleUnblock = async (id) => {
    try {
      await api.post(`/admin/equipment/${id}/unblock`);
      loadEquipment();
    } catch (err) {
      setError('Error al desbloquear equipo');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar este equipo?')) return;
    try {
      await api.delete(`/admin/equipment/${id}`);
      loadEquipment();
    } catch (err) {
      setError('Error al eliminar equipo');
    }
  };

  const getStatusBadge = (status) => {
    const badges = {
      DISPONIBLE: 'badge-success',
      EN_USO: 'badge-warning',
      MANTENIMIENTO: 'badge-danger'
    };
    return <span className={`badge ${badges[status]}`}>{status}</span>;
  };

  const getLabName = (labId) => {
    const lab = labs.find(l => l.id === labId);
    return lab ? lab.code : labId;
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
        <h2>Gestión de Equipos</h2>
        <button onClick={() => setShowModal(true)} className="btn-primary">
          + Nuevo Equipo
        </button>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>Identificador</th>
              <th>Tipo</th>
              <th>Laboratorio</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {equipment.map(eq => (
              <tr key={eq.id}>
                <td>{eq.identifier}</td>
                <td>{eq.type}</td>
                <td>{getLabName(eq.labId)}</td>
                <td>{getStatusBadge(eq.status)}</td>
                <td>
                  <div className="action-buttons">
                    {eq.status === 'DISPONIBLE' && (
                      <button onClick={() => handleBlock(eq.id)} className="btn-secondary">
                        Bloquear
                      </button>
                    )}
                    {eq.status === 'MANTENIMIENTO' && (
                      <button onClick={() => handleUnblock(eq.id)} className="btn-success">
                        Desbloquear
                      </button>
                    )}
                    <button onClick={() => handleDelete(eq.id)} className="btn-danger">
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Nuevo Equipo</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Laboratorio</label>
                <select
                  value={formData.labId}
                  onChange={(e) => setFormData({ ...formData, labId: e.target.value })}
                  required
                >
                  <option value="">Seleccionar...</option>
                  {labs.map(lab => (
                    <option key={lab.id} value={lab.id}>{lab.code} - {lab.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Identificador</label>
                <input
                  value={formData.identifier}
                  onChange={(e) => setFormData({ ...formData, identifier: e.target.value })}
                  required
                  placeholder="Ej: PC-001"
                />
              </div>
              <div className="form-group">
                <label>Tipo</label>
                <input
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  required
                  placeholder="Ej: Computadora, Microscopio"
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
    </div>
  );
}
