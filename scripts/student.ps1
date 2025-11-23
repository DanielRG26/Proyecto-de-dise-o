$ErrorActionPreference = 'Stop'
function Invoke-Api { param($Path,$Method='GET',$Body=$null) $h=@{'Content-Type'='application/json'}; if ($Global:Token) { $h['Authorization']="Bearer $Global:Token" } $b=$null; if ($Body) { $b=($Body | ConvertTo-Json -Depth 6) } return Invoke-RestMethod -Method $Method -Uri ("http://localhost:8080"+$Path) -Headers $h -ContentType 'application/json' -Body $b }
function Register { param($name,$email,$phone,$password) Invoke-Api "/auth/register" 'POST' @{ fullName=$name; email=$email; phone=$phone; password=$password } }
function Login { param($email,$password) $r=Invoke-Api "/auth/login" 'POST' @{ email=$email; password=$password }; $Global:Token=$r.token; Write-Host "Conectado" }
function Availability { $r=Invoke-Api "/student/availability"; $r | ConvertTo-Json -Depth 6 }
function Reserve { param($labCode,$eqId,$date,$start,$end) $r=Invoke-Api "/student/reservas" 'POST' @{ labCode=$labCode; equipmentIdentifier=$eqId; date=$date; startTime=$start; endTime=$end }; $r | ConvertTo-Json -Depth 6 }
function History { $r=Invoke-Api "/student/reservas"; $r | ConvertTo-Json -Depth 6 }
function Modify { param($id,$date,$start,$end) $r=Invoke-Api "/student/reservas/$id" 'PUT' @{ date=$date; startTime=$start; endTime=$end }; $r | ConvertTo-Json -Depth 6 }
function Cancel { param($id) Invoke-Api "/student/reservas/$id" 'DELETE'; Write-Host "Cancelada" }
function Notifs { $r=Invoke-Api "/student/notificaciones"; $r | ConvertTo-Json -Depth 6 }
function ReadNotif { param($id) Invoke-Api "/student/notificaciones/$id/leer" 'POST'; Write-Host "Leída" }
Write-Host "Estudiante"
$m=@"
1) Iniciar sesión
2) Registrarse
3) Disponibilidad
4) Reservar
5) Historial
6) Modificar
7) Cancelar
8) Notificaciones
9) Marcar notificación
0) Salir
"@
while ($true) {
  Write-Host $m; $op=Read-Host "Opción"; if ($op -eq '0') { break }
  try {
    switch ($op) {
      '1' { $e=Read-Host "Correo"; $pw=Read-Host "Contraseña"; Login $e $pw }
      '2' { $n=Read-Host "Nombre"; $e=Read-Host "Correo"; $p=Read-Host "Teléfono"; $pw=Read-Host "Contraseña"; Register $n $e $p $pw }
      '3' { Availability }
      '4' { $lc=Read-Host "Código laboratorio"; $eq=Read-Host "Identificador equipo"; $d=Read-Host "Fecha YYYY-MM-DD"; $st=Read-Host "Inicio HH:MM"; $en=Read-Host "Fin HH:MM"; Reserve $lc $eq $d $st $en }
      '5' { History }
      '6' { $id=Read-Host "ID reserva"; $d=Read-Host "Fecha YYYY-MM-DD"; $st=Read-Host "Inicio HH:MM"; $en=Read-Host "Fin HH:MM"; Modify $id $d $st $en }
      '7' { $id=Read-Host "ID reserva"; Cancel $id }
      '8' { Notifs }
      '9' { $id=Read-Host "ID notificación"; ReadNotif $id }
      default { Write-Host "Opción inválida" }
    }
  } catch { Write-Host $_.Exception.Message }
}