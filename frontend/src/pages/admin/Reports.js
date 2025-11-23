import React, { useState, useEffect } from 'react';
import api from '../../services/api';

export default function Reports() {
  const [usoReport, setUsoReport] = useState([]);
  const [mantenimientoReport, setMantenimientoReport] = useState([]);
  const [usuariosActivos, setUsuariosActivos] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    loadReports();
  }, []);

  const loadReports = async () => {
    try {
      const [uso, mant, activos] = await Promise.all([
        api.get('/admin/reports/uso'),
        api.get('/admin/reports/mantenimiento'),
        api.get('/admin/reports/usuarios-activos')
      ]);
      setUsoReport(uso.data);
      setMantenimientoReport(mant.data);
      setUsuariosActivos(activos.data.usuarios_activos);
    } catch (err) {
      setError('Error al cargar reportes');
    }
  };

  return (
    <div>
      <h2>Reportes</h2>
      {error && <div className="error">{error}</div>}

      <div className="grid grid-3" style={{ marginBottom: '30px' }}>
        <div className="stat-card">
          <h3>Total Laboratorios</h3>
          <div className="value">{usoReport.length}</div>
        </div>
        <div className="stat-card">
          <h3>Total Equipos</h3>
          <div className="value">
            {usoReport.reduce((sum, lab) => sum + lab.total_equipos, 0)}
          </div>
        </div>
        <div className="stat-card">
          <h3>Usuarios Activos</h3>
          <div className="value">{usuariosActivos.length}</div>
        </div>
      </div>

      <div className="card" style={{ marginBottom: '20px' }}>
        <h3>Reporte de Uso por Laboratorio</h3>
        <table>
          <thead>
            <tr>
              <th>Laboratorio</th>
              <th>Total Equipos</th>
              <th>Aforo</th>
              <th>Total Reservas</th>
            </tr>
          </thead>
          <tbody>
            {usoReport.map((lab, index) => (
              <tr key={index}>
                <td>{lab.laboratorio}</td>
                <td>{lab.total_equipos}</td>
                <td>{lab.aforo}</td>
                <td>{lab.total_reservas}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="card">
        <h3>Reporte de Mantenimiento</h3>
        <table>
          <thead>
            <tr>
              <th>Laboratorio</th>
              <th>Equipos en Mantenimiento</th>
            </tr>
          </thead>
          <tbody>
            {mantenimientoReport.map((lab, index) => (
              <tr key={index}>
                <td>{lab.laboratorio}</td>
                <td>
                  <span className={`badge ${lab.equipos_mantenimiento > 0 ? 'badge-warning' : 'badge-success'}`}>
                    {lab.equipos_mantenimiento}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
