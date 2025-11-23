# Solución de Problemas - Endpoint Set Schedule

## Endpoint
```
PUT http://localhost:8080/admin/labs/{id}/schedule
```

## Headers Requeridos
```
Authorization: Bearer {tu_token_admin}
Content-Type: application/json
```

## Body Correcto (JSON)

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
  }
]
```

## ✅ Checklist de Verificación

### 1. Verificar Headers
- [ ] Header `Authorization` está presente con formato: `Bearer {token}`
- [ ] Header `Content-Type` está configurado como `application/json`
- [ ] El token no ha expirado (tokens expiran en 120 minutos)

### 2. Verificar URL
- [ ] La URL es correcta: `PUT http://localhost:8080/admin/labs/{id}/schedule`
- [ ] El `{id}` es un ID válido de MongoDB (24 caracteres hexadecimales)
- [ ] Ejemplo correcto: `http://localhost:8080/admin/labs/6922a0e57a50f00f3b739d05/schedule`

### 3. Verificar Body
- [ ] El body es un **array** (empieza con `[`)
- [ ] Cada objeto tiene los campos: `dayOfWeek`, `start`, `end`
- [ ] `dayOfWeek` está en mayúsculas: `MONDAY`, `TUESDAY`, etc.
- [ ] Las horas están en formato `HH:mm` (ej: `08:00`, `14:30`)
- [ ] No hay comas extras al final del array

## 📋 Valores Válidos

### Días de la Semana (dayOfWeek)
- `MONDAY`
- `TUESDAY`
- `WEDNESDAY`
- `THURSDAY`
- `FRIDAY`
- `SATURDAY`
- `SUNDAY`

### Formato de Horas
- Formato: `HH:mm`
- Ejemplos válidos: `08:00`, `09:30`, `14:00`, `23:59`
- Ejemplos inválidos: `8:00`, `9:30`, `08:00:00`, `8:00 AM`

## 🔍 Errores Comunes y Soluciones

### Error 1: "Cannot deserialize value of type `java.time.LocalTime`"
**Causa:** Formato de hora incorrecto
**Solución:** Usar formato `HH:mm` (ej: `08:00` en lugar de `8:00`)

### Error 2: "No enum constant java.time.DayOfWeek.MONDAY" (o similar)
**Causa:** El día está en minúsculas o con formato incorrecto
**Solución:** Usar mayúsculas: `MONDAY`, `TUESDAY`, etc.

### Error 3: "Required request body is missing"
**Causa:** No se está enviando el body o el Content-Type es incorrecto
**Solución:** 
- Verificar que el body esté presente
- Verificar que el header `Content-Type: application/json` esté configurado

### Error 4: "Laboratorio no encontrado"
**Causa:** El ID del laboratorio no existe
**Solución:** Verificar que el ID sea correcto usando el endpoint `GET /admin/labs`

### Error 5: "401 Unauthorized" o "403 Forbidden"
**Causa:** Token inválido, expirado o sin permisos de administrador
**Solución:**
- Verificar que el token sea válido
- Hacer login nuevamente si el token expiró
- Verificar que el usuario tenga rol `ADMINISTRADOR`

### Error 6: "Validation failed"
**Causa:** Campos requeridos faltantes o valores nulos
**Solución:** Verificar que todos los campos (`dayOfWeek`, `start`, `end`) estén presentes y no sean nulos

## 📝 Ejemplo Completo en Postman

### Request
```
PUT http://localhost:8080/admin/labs/6922a0e57a50f00f3b739d05/schedule
```

### Headers
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2OTIyYTA5YzdhNTBmMDBmM2I3MzlkMDIiLCJyb2xlIjoiQURNSU5JU1RSQURPUiIsImlhdCI6MTc2Mzg3NzAzMiwiZXhwIjoxNzYzODg0MjMyfQ.qaIKr-iVGwzwPj-MCsII75hMaAB480iMWF4SJQdxfy8
Content-Type: application/json
```

### Body (raw JSON)
```json
[
  {
    "dayOfWeek": "MONDAY",
    "start": "08:00",
    "end": "17:00"
  },
  {
    "dayOfWeek": "TUESDAY",
    "start": "08:00",
    "end": "17:00"
  },
  {
    "dayOfWeek": "WEDNESDAY",
    "start": "08:00",
    "end": "17:00"
  },
  {
    "dayOfWeek": "THURSDAY",
    "start": "08:00",
    "end": "17:00"
  },
  {
    "dayOfWeek": "FRIDAY",
    "start": "08:00",
    "end": "17:00"
  }
]
```

## 🧪 Prueba Rápida

1. **Verificar que el laboratorio existe:**
   ```
   GET http://localhost:8080/admin/labs
   Headers: Authorization: Bearer {token}
   ```

2. **Establecer horario con un solo día (prueba simple):**
   ```json
   [
     {
       "dayOfWeek": "MONDAY",
       "start": "08:00",
       "end": "12:00"
     }
   ]
   ```

3. **Si funciona, agregar más días**

## 💡 Tips

- Siempre usa el formato de hora de 24 horas (`HH:mm`)
- Los días deben estar en inglés y en mayúsculas
- El body debe ser un array, no un objeto
- Verifica que el token no haya expirado (haz login nuevamente si es necesario)

