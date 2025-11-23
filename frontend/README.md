# Lab Turnos - Frontend

Frontend de la aplicación de gestión de laboratorios desarrollado con React.

## Características

### Para Administradores
- ✅ Gestión de laboratorios (crear, actualizar aforo, configurar horarios)
- ✅ Gestión de equipos (agregar, bloquear/desbloquear, eliminar)
- ✅ Gestión de usuarios (cambiar roles, eliminar)
- ✅ Reportes de uso y mantenimiento

### Para Estudiantes
- ✅ Ver disponibilidad de laboratorios y equipos
- ✅ Crear reservas
- ✅ Modificar reservas activas
- ✅ Cancelar reservas
- ✅ Ver historial de reservas
- ✅ Recibir y gestionar notificaciones

## Instalación

1. Instalar dependencias:
```bash
cd frontend
npm install
```

2. Configurar el backend:
   - Asegúrate de que el backend esté corriendo en `http://localhost:8080`
   - Si usas otro puerto, actualiza el `proxy` en `package.json`

3. Iniciar la aplicación:
```bash
npm start
```

La aplicación se abrirá en `http://localhost:3000`

## Estructura del Proyecto

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── context/
│   │   └── AuthContext.js          # Contexto de autenticación
│   ├── pages/
│   │   ├── admin/
│   │   │   ├── AdminDashboard.js   # Dashboard principal admin
│   │   │   ├── Labs.js             # Gestión de laboratorios
│   │   │   ├── Equipment.js        # Gestión de equipos
│   │   │   ├── Users.js            # Gestión de usuarios
│   │   │   └── Reports.js          # Reportes
│   │   ├── student/
│   │   │   ├── StudentDashboard.js # Dashboard principal estudiante
│   │   │   ├── Availability.js     # Ver disponibilidad
│   │   │   ├── Reservations.js     # Gestión de reservas
│   │   │   └── Notifications.js    # Notificaciones
│   │   ├── Login.js                # Página de login
│   │   └── Register.js             # Página de registro
│   ├── services/
│   │   └── api.js                  # Configuración de Axios
│   ├── App.js                      # Componente principal
│   ├── App.css                     # Estilos de la app
│   ├── index.js                    # Punto de entrada
│   └── index.css                   # Estilos globales
└── package.json
```

## Tecnologías Utilizadas

- **React 18** - Framework principal
- **React Router 6** - Navegación
- **Axios** - Cliente HTTP
- **CSS Modules** - Estilos

## Funcionalidades Implementadas

### Autenticación
- Login con email y contraseña
- Registro de nuevos usuarios
- Protección de rutas por rol
- Persistencia de sesión con JWT

### Panel de Administrador
- **Laboratorios**: CRUD completo con configuración de horarios por día
- **Equipos**: Agregar, eliminar, bloquear/desbloquear
- **Usuarios**: Ver lista, cambiar roles, eliminar
- **Reportes**: Uso por laboratorio, equipos en mantenimiento, usuarios activos

### Panel de Estudiante
- **Disponibilidad**: Ver equipos disponibles por laboratorio en tiempo real
- **Reservas**: Crear, modificar y cancelar reservas
- **Notificaciones**: Ver y marcar como leídas

## Notas de Desarrollo

- El frontend usa un proxy configurado en `package.json` para conectarse al backend
- Los tokens JWT se almacenan en `localStorage`
- Las rutas están protegidas según el rol del usuario
- Los formularios incluyen validación básica

## Scripts Disponibles

- `npm start` - Inicia el servidor de desarrollo
- `npm build` - Crea la versión de producción
- `npm test` - Ejecuta los tests
- `npm eject` - Expone la configuración de webpack

## Próximas Mejoras

- [ ] Agregar tests unitarios
- [ ] Implementar paginación en tablas
- [ ] Agregar filtros y búsqueda
- [ ] Mejorar diseño responsive
- [ ] Agregar notificaciones en tiempo real con WebSockets
- [ ] Implementar tema oscuro
