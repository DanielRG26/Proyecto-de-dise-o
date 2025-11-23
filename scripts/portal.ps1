$ErrorActionPreference = 'Stop'
function Invoke-Api { param($Path,$Method='GET',$Body=$null) $h=@{'Content-Type'='application/json'}; if ($Global:Token) { $h['Authorization']="Bearer $Global:Token" } $b=$null; if ($Body) { $b=($Body | ConvertTo-Json -Depth 6) } return Invoke-RestMethod -Method $Method -Uri ("http://localhost:8080"+$Path) -Headers $h -ContentType 'application/json' -Body $b }
function RegisterStudent { param($name,$email,$phone,$password) Invoke-Api "/auth/register" 'POST' @{ fullName=$name; email=$email; phone=$phone; password=$password; role='ESTUDIANTE' } }
function RegisterAdmin { param($name,$email,$phone,$password) Invoke-Api "/auth/register" 'POST' @{ fullName=$name; email=$email; phone=$phone; password=$password; role='ADMINISTRADOR' } }
function Login { param($email,$password) $r=Invoke-Api "/auth/login" 'POST' @{ email=$email; password=$password }; $Global:Token=$r.token; Write-Host "Sesión iniciada" }
function GetRole { $u=Invoke-Api "/auth/me"; return $u.role }
function AdminMenu {
  $m=@"
1) Crear lab
2) Listar labs
3) Set aforo
4) Set horario
5) Agregar equipo
6) Bloquear equipo
7) Desbloquear equipo
8) Reporte uso
9) Reporte mantenimiento
10) Usuarios activos
11) Listar usuarios
12) Cambiar rol
13) Eliminar usuario
0) Salir
"@
  while ($true) {
    Write-Host $m; $op=Read-Host "Opción"; if ($op -eq '0') { break }
    try {
      switch ($op) {
        '1' { $c=Read-Host "Código"; $n=Read-Host "Nombre"; $cap=Read-Host "Aforo"; Invoke-Api "/admin/labs" 'POST' @{ code=$c; name=$n; capacity=[int]$cap } | ConvertTo-Json -Depth 6 }
        '2' { Invoke-Api "/admin/labs" | ConvertTo-Json -Depth 6 }
        '3' { $id=Read-Host "ID laboratorio"; $cap=Read-Host "Nuevo aforo"; Invoke-Api "/admin/labs/$id/capacity/$cap" 'PUT' | ConvertTo-Json -Depth 6 }
        '4' { $id=Read-Host "ID laboratorio"; $d=Read-Host "Día ej: MONDAY"; $st=Read-Host "Inicio HH:MM"; $en=Read-Host "Fin HH:MM"; $slot=@( @{ dayOfWeek=$d; start=$st; end=$en } ); Invoke-Api "/admin/labs/$id/schedule" 'PUT' $slot | ConvertTo-Json -Depth 6 }
        '5' { $id=Read-Host "ID laboratorio"; $eq=Read-Host "Identificador"; $ty=Read-Host "Tipo"; Invoke-Api "/admin/labs/$id/equipment" 'POST' @{ identifier=$eq; type=$ty } | ConvertTo-Json -Depth 6 }
        '6' { $id=Read-Host "ID equipo"; Invoke-Api "/admin/equipment/$id/block" 'POST' | ConvertTo-Json -Depth 6 }
        '7' { $id=Read-Host "ID equipo"; Invoke-Api "/admin/equipment/$id/unblock" 'POST' | ConvertTo-Json -Depth 6 }
        '8' { Invoke-Api "/admin/reports/uso" | ConvertTo-Json -Depth 6 }
        '9' { Invoke-Api "/admin/reports/mantenimiento" | ConvertTo-Json -Depth 6 }
        '10' { Invoke-Api "/admin/reports/usuarios-activos" | ConvertTo-Json -Depth 6 }
        '11' { Invoke-Api "/admin/users" | ConvertTo-Json -Depth 6 }
        '12' { $id=Read-Host "ID usuario"; $r=Read-Host "Rol (ESTUDIANTE/ADMINISTRADOR)"; Invoke-Api "/admin/users/$id/role" 'POST' @{ role=$r } | Out-Null; Write-Host "Rol actualizado" }
        '13' { $id=Read-Host "ID usuario"; Invoke-Api "/admin/users/$id" 'DELETE' | Out-Null; Write-Host "Usuario eliminado" }
        default { Write-Host "Opción inválida" }
      }
    } catch { Write-Host $_.Exception.Message }
  }
}
function StudentMenu {
  $m=@"
1) Disponibilidad
2) Reservar
3) Historial
4) Modificar
5) Cancelar
6) Notificaciones
7) Marcar notificación
0) Salir
"@
  while ($true) {
    Write-Host $m; $op=Read-Host "Opción"; if ($op -eq '0') { break }
    try {
      switch ($op) {
        '1' { Invoke-Api "/student/availability" | ConvertTo-Json -Depth 6 }
        '2' { $lc=Read-Host "Código laboratorio"; $eq=Read-Host "Identificador equipo"; $d=Read-Host "Fecha YYYY-MM-DD"; $st=Read-Host "Inicio HH:MM"; $en=Read-Host "Fin HH:MM"; Invoke-Api "/student/reservas" 'POST' @{ labCode=$lc; equipmentIdentifier=$eq; date=$d; startTime=$st; endTime=$en } | ConvertTo-Json -Depth 6 }
        '3' { Invoke-Api "/student/reservas" | ConvertTo-Json -Depth 6 }
        '4' { $id=Read-Host "ID reserva"; $d=Read-Host "Fecha YYYY-MM-DD"; $st=Read-Host "Inicio HH:MM"; $en=Read-Host "Fin HH:MM"; Invoke-Api "/student/reservas/$id" 'PUT' @{ date=$d; startTime=$st; endTime=$en } | ConvertTo-Json -Depth 6 }
        '5' { $id=Read-Host "ID reserva"; Invoke-Api "/student/reservas/$id" 'DELETE' | Out-Null; Write-Host "Cancelada" }
        '6' { Invoke-Api "/student/notificaciones" | ConvertTo-Json -Depth 6 }
        '7' { $id=Read-Host "ID notificación"; Invoke-Api "/student/notificaciones/$id/leer" 'POST' | Out-Null; Write-Host "Leída" }
        default { Write-Host "Opción inválida" }
      }
    } catch { Write-Host $_.Exception.Message }
  }
}
Write-Host "Portal"
$menu=@"
1) Registrarse como ESTUDIANTE
2) Registrarse como ADMINISTRADOR
3) Iniciar sesión
0) Salir
"@
while ($true) { 
  Write-Host $menu; $op=Read-Host "Opción"; if ($op -eq '0') { break }
  try {
    switch ($op) {
      '1' { $n=Read-Host "Nombre"; $e=Read-Host "Correo"; $p=Read-Host "Teléfono"; $pw=Read-Host "Contraseña"; RegisterStudent $n $e $p $pw }
      '2' { $n=Read-Host "Nombre"; $e=Read-Host "Correo"; $p=Read-Host "Teléfono"; $pw=Read-Host "Contraseña"; RegisterAdmin $n $e $p $pw }
      '3' { $e=Read-Host "Correo"; $pw=Read-Host "Contraseña"; Login $e $pw; $role=GetRole; Write-Host "Rol: $role"; if ($role -eq 'ADMINISTRADOR') { AdminMenu } elseif ($role -eq 'ESTUDIANTE') { StudentMenu } else { Write-Host "No autorizado" } }
      default { Write-Host "Opción inválida" }
    }
  } catch { if ($_.ErrorDetails.Message) { Write-Host $_.ErrorDetails.Message } else { Write-Host $_.Exception.Message } }
}