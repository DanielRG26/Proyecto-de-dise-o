$ErrorActionPreference = 'Stop'
function Invoke-Api { param($Path,$Method='GET',$Body=$null) $h=@{'Content-Type'='application/json'}; if ($Global:Token) { $h['Authorization']="Bearer $Global:Token" } $b=$null; if ($Body) { $b=($Body | ConvertTo-Json -Depth 6) } return Invoke-RestMethod -Method $Method -Uri ("http://localhost:8080"+$Path) -Headers $h -ContentType 'application/json' -Body $b }
function Register { param($name,$email,$phone,$password) Invoke-Api "/auth/register" 'POST' @{ fullName=$name; email=$email; phone=$phone; password=$password } }
function Login { param($email,$password) $r=Invoke-Api "/auth/login" 'POST' @{ email=$email; password=$password }; $Global:Token=$r.token; Write-Host "Conectado" }
function CrearLab { param($code,$name,$capacity) Invoke-Api "/admin/labs" 'POST' @{ code=$code; name=$name; capacity=[int]$capacity } }
function ListLabs { $r=Invoke-Api "/admin/labs"; $r | ConvertTo-Json -Depth 6 }
function SetCapacity { param($id,$capacity) Invoke-Api "/admin/labs/$id/capacity/$capacity" 'PUT' }
function SetSchedule { param($id,$day,$start,$end) $slot=@( @{ dayOfWeek=$day; start=$start; end=$end } ); Invoke-Api "/admin/labs/$id/schedule" 'PUT' $slot }
function AddEquipment { param($labId,$identifier,$type) Invoke-Api "/admin/labs/$labId/equipment" 'POST' @{ identifier=$identifier; type=$type } }
function BlockEq { param($id) Invoke-Api "/admin/equipment/$id/block" 'POST' }
function UnblockEq { param($id) Invoke-Api "/admin/equipment/$id/unblock" 'POST' }
function ReportUso { $r=Invoke-Api "/admin/reports/uso"; $r | ConvertTo-Json -Depth 6 }
function ReportMant { $r=Invoke-Api "/admin/reports/mantenimiento"; $r | ConvertTo-Json -Depth 6 }
function ReportActivos { $r=Invoke-Api "/admin/reports/usuarios-activos"; $r | ConvertTo-Json -Depth 6 }
function ListUsers { $r=Invoke-Api "/admin/users"; $r | ConvertTo-Json -Depth 6 }
function ChangeRole { param($id,$role) Invoke-Api "/admin/users/$id/role" 'POST' @{ role=$role } }
function DeleteUser { param($id) Invoke-Api "/admin/users/$id" 'DELETE' }
Write-Host "Administrador"
$m=@"
1) Registrar
2) Login
3) Crear lab
4) Listar labs
5) Set aforo
6) Set horario
7) Agregar equipo
8) Bloquear equipo
9) Desbloquear equipo
10) Reporte uso
11) Reporte mantenimiento
12) Usuarios activos
13) Listar usuarios
14) Cambiar rol
15) Eliminar usuario
0) Salir
"@
while ($true) {
  Write-Host $m; $op=Read-Host "Opción"; if ($op -eq '0') { break }
  try {
    switch ($op) {
      '1' { $n=Read-Host "Nombre"; $e=Read-Host "Correo"; $p=Read-Host "Teléfono"; $pw=Read-Host "Contraseña"; Register $n $e $p $pw }
      '2' { $e=Read-Host "Correo"; $pw=Read-Host "Contraseña"; Login $e $pw }
      '3' { $c=Read-Host "Código"; $n=Read-Host "Nombre"; $cap=Read-Host "Aforo"; CrearLab $c $n $cap }
      '4' { ListLabs }
      '5' { $id=Read-Host "ID laboratorio"; $cap=Read-Host "Nuevo aforo"; SetCapacity $id $cap }
      '6' { $id=Read-Host "ID laboratorio"; $d=Read-Host "Día ej: MONDAY"; $st=Read-Host "Inicio HH:MM"; $en=Read-Host "Fin HH:MM"; SetSchedule $id $d $st $en }
      '7' { $id=Read-Host "ID laboratorio"; $eq=Read-Host "Identificador"; $ty=Read-Host "Tipo"; AddEquipment $id $eq $ty }
      '8' { $id=Read-Host "ID equipo"; BlockEq $id }
      '9' { $id=Read-Host "ID equipo"; UnblockEq $id }
      '10' { ReportUso }
      '11' { ReportMant }
      '12' { ReportActivos }
      '13' { ListUsers }
      '14' { $id=Read-Host "ID usuario"; $r=Read-Host "Rol (ESTUDIANTE/ADMINISTRADOR)"; ChangeRole $id $r }
      '15' { $id=Read-Host "ID usuario"; DeleteUser $id }
      default { Write-Host "Opción inválida" }
    }
  } catch { Write-Host $_.Exception.Message }
}