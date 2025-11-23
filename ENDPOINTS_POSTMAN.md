# Endpoints API - Lab Turnos

## Base URL
```
http://localhost:8080
```

## Autenticación
Todos los endpoints (excepto `/auth/register` y `/auth/login`) requieren un token JWT en el header:
```
Authorization: Bearer {token}
```

---

## 🔐 AUTH - Autenticación

### 1. Registrar Usuario
**POST** `/auth/register`

**Body:**
```json
{
  "fullName": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+1234567890",
  "password": "password123",
  "role": "ESTUDIANTE"
}
```

**Nota:** El campo `role` es opcional. Valores posibles: `ESTUDIANTE` o `ADMINISTRADOR`. Si no se especifica, el primer usuario será ADMINISTRADOR, los demás serán ESTUDIANTE.

---

### 2. Iniciar Sesión
**POST** `/auth/login`

**Body:**
```json
{
  "email": "juan@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 3. Obtener Usuario Actual
**GET** `/auth/me`

**Headers:**
```
Authorization: Bearer {token}
```

---

## 👨‍💼 ADMIN - Gestión de Laboratorios

### 4. Crear Laboratorio
**POST** `/admin/labs`

**Headers:**
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Body:**
```json
{
  "code": "LAB-001",
  "name": "Laboratorio de Computación",
  "capacity": 30
}
```

---

### 5. Listar Todos los Laboratorios
**GET** `/admin/labs`

**Headers:**
```
Authorization: Bearer {admin_token}
```

---

### 6. Actualizar Aforo de Laboratorio
**PUT** `/admin/labs/{id}/capacity/{capacity}`

**Headers:**
```
Authorization: Bearer {admin_token}
```

**Ejemplo:**
```
PUT http://localhost:8080/admin/labs/507f1f77bcf86cd799439011/capacity/40
```

---

### 7. Configurar Horario de Laboratorio
**PUT** `/admin/labs/{id}/schedule`

**Headers:**
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Body:**
```json
[
  {
    "dayOfWeek": "MONDAY",
    "start": "08:00",
    "end": "12:00"
  },
  {
    "dayOfWeek": "TUESDAY",
    "start": "08:00",
    "end": "12:00"
  },
  {
    "dayOfWeek": "WEDNESDAY",
    "start": "08:00",
    "end": "12:00"
  }
]
```

**Días válidos:** `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY`

---

## 🔧 ADMIN - Gestión de Equipos

### 8. Agregar Equipo a Laboratorio
**POST** `/admin/labs/{id}/equipment`

**Headers:**
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Body:**
```json
{
  "identifier": "COMP-001",
  "type": "Computadora"
}
```

---

### 9. Listar Equipos por Laboratorio
**GET** `/admin/labs/{id}/equipment`

**Headers:**
```
Authorization: Bearer {admin_token}
```

---

### 10. Listar Todos los Equipos
**GET** `/admin/equipment`

**Headers:**
```
Authorization: Bearer {admin_token}
```

---

### 11. Bloquear Equipo
**POST** `/admin/equipment/{id}/block`

**Headers:**
```
Authorization: Bearer {admin_token}
```

---

### 12. Desbloquear Equipo
**POST** `/admin/equipment/{id}/unblock`

**Headers:**
```
Authorization: Bearer {admin_token}
```

---

### 13. Eliminar Equipo
**DELETE** `/admin/equipment/{id}`

**Headers:**
```
Authorization: Bearer {admin_token}
```

---

## 👥 ADMIN - Gestión de Usuarios

### 14. Listar Todos los Usuarios
**GET** `/admin/users`

**Headers:**
```
Authorization: Bearer {admin_token}
```

---

### 15. Eliminar Usuario
**DELETE** `/admin/users/{id}`

**Headers:**
```
Authorization: Bearer {admin_token}
```

---

### 16. Cambiar Rol de Usuario
**POST** `/admin/users/{id}/role`

**Headers:**
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**Body:**
```json
{
  "role": "ADMINISTRADOR"
}
```

**Valores posibles:** `ADMINISTRADOR` o `ESTUDIANTE`

---

## 📊 ADMIN - Reportes

### 17. Reporte de Uso
**GET** `/admin/reports/uso`

**Headers:**
```
Authorization: Bearer {admin_token}
```

**Response:** Lista con información de uso por laboratorio (total equipos, aforo, total reservas)

---

### 18. Reporte de Mantenimiento
**GET** `/admin/reports/mantenimiento`

**Headers:**
```
Authorization: Bearer {admin_token}
```

**Response:** Lista con equipos en mantenimiento por laboratorio

---

### 19. Reporte de Usuarios Activos
**GET** `/admin/reports/usuarios-activos`

**Headers:**
```
Authorization: Bearer {admin_token}
```

**Response:** Lista de IDs de usuarios con reservas activas

---

## 🎓 STUDENT - Funcionalidades para Estudiantes

### 20. Obtener Disponibilidad
**GET** `/student/availability`

**Headers:**
```
Authorization: Bearer {student_token}
```

**Response:** Lista de laboratorios con conteo de equipos disponibles, en uso y en mantenimiento

---

### 21. Obtener Laboratorios Disponibles
**GET** `/student/labs/available`

**Headers:**
```
Authorization: Bearer {student_token}
```

**Response:** Lista detallada de laboratorios con sus equipos y estados

---

### 22. Crear Reserva
**POST** `/student/reservas`

**Headers:**
```
Authorization: Bearer {student_token}
Content-Type: application/json
```

**Body:**
```json
{
  "labCode": "LAB-001",
  "equipmentIdentifier": "COMP-001",
  "date": "2024-12-01",
  "startTime": "09:00",
  "endTime": "11:00"
}
```

**Formato de fecha:** `YYYY-MM-DD` (ej: 2024-12-01)
**Formato de hora:** `HH:mm` (ej: 09:00, 14:30)

---

### 23. Modificar Reserva
**PUT** `/student/reservas/{id}`

**Headers:**
```
Authorization: Bearer {student_token}
Content-Type: application/json
```

**Body:**
```json
{
  "date": "2024-12-01",
  "startTime": "10:00",
  "endTime": "12:00"
}
```

---

### 24. Cancelar Reserva
**DELETE** `/student/reservas/{id}`

**Headers:**
```
Authorization: Bearer {student_token}
```

---

### 25. Historial de Reservas
**GET** `/student/reservas`

**Headers:**
```
Authorization: Bearer {student_token}
```

**Response:** Lista de todas las reservas del estudiante autenticado

---

### 26. Obtener Notificaciones
**GET** `/student/notificaciones`

**Headers:**
```
Authorization: Bearer {student_token}
```

**Response:** Lista de notificaciones del estudiante

---

### 27. Marcar Notificación como Leída
**POST** `/student/notificaciones/{id}/leer`

**Headers:**
```
Authorization: Bearer {student_token}
```

---

## 📝 Notas Importantes

1. **Tokens JWT:** Después de hacer login, copia el token de la respuesta y úsalo en el header `Authorization: Bearer {token}` para los demás endpoints.

2. **Roles:** 
   - `ADMINISTRADOR`: Acceso a todos los endpoints de `/admin/*`
   - `ESTUDIANTE`: Acceso a todos los endpoints de `/student/*`

3. **IDs:** Los IDs en las URLs deben ser reemplazados con los IDs reales obtenidos de las respuestas de la API.

4. **Fechas y Horas:** 
   - Fechas: Formato `YYYY-MM-DD` (ej: 2024-12-01)
   - Horas: Formato `HH:mm` (ej: 09:00, 14:30)

5. **Días de la Semana:** Para el horario de laboratorios, usar: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY`

---

## 🚀 Flujo Recomendado para Probar

1. **Registrar un usuario administrador:**
   ```
   POST /auth/register
   Body: { "fullName": "Admin", "email": "admin@test.com", "password": "admin123", "role": "ADMINISTRADOR" }
   ```

2. **Iniciar sesión como administrador:**
   ```
   POST /auth/login
   Body: { "email": "admin@test.com", "password": "admin123" }
   ```
   Guarda el token recibido como `admin_token`

3. **Crear un laboratorio:**
   ```
   POST /admin/labs
   Headers: Authorization: Bearer {admin_token}
   Body: { "code": "LAB-001", "name": "Lab de Computación", "capacity": 30 }
   ```
   Guarda el ID del laboratorio creado

4. **Agregar equipos al laboratorio:**
   ```
   POST /admin/labs/{lab_id}/equipment
   Headers: Authorization: Bearer {admin_token}
   Body: { "identifier": "COMP-001", "type": "Computadora" }
   ```

5. **Registrar un estudiante:**
   ```
   POST /auth/register
   Body: { "fullName": "Estudiante", "email": "estudiante@test.com", "password": "est123", "role": "ESTUDIANTE" }
   ```

6. **Iniciar sesión como estudiante:**
   ```
   POST /auth/login
   Body: { "email": "estudiante@test.com", "password": "est123" }
   ```
   Guarda el token recibido como `student_token`

7. **Crear una reserva:**
   ```
   POST /student/reservas
   Headers: Authorization: Bearer {student_token}
   Body: { "labCode": "LAB-001", "equipmentIdentifier": "COMP-001", "date": "2024-12-01", "startTime": "09:00", "endTime": "11:00" }
   ```

