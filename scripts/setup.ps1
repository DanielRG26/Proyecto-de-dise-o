$ErrorActionPreference = 'Stop'
function Invoke-Api { param($Path,$Method='GET',$Body=$null) $h=@{'Content-Type'='application/json'}; if ($Global:Token) { $h['Authorization']="Bearer $Global:Token" } $b=$null; if ($Body) { $b=($Body | ConvertTo-Json -Depth 6) } return Invoke-RestMethod -Method $Method -Uri ("http://localhost:8080"+$Path) -Headers $h -ContentType 'application/json' -Body $b }
function Register { param($name,$email,$phone,$password) Invoke-Api "/auth/register" 'POST' @{ fullName=$name; email=$email; phone=$phone; password=$password } }
function Login { param($email,$password) $r=Invoke-Api "/auth/login" 'POST' @{ email=$email; password=$password }; $Global:Token=$r.token; Write-Host "Token asignado" }
function CrearLab { param($code,$name,$capacity) $r=Invoke-Api "/admin/labs" 'POST' @{ code=$code; name=$name; capacity=[int]$capacity }; $script:LabId=$r.id; Write-Host "Lab Id=$($script:LabId)" }
function SetSchedule { param($labId,$day,$start,$end) $slot=@( @{ dayOfWeek=$day; start=$start; end=$end } ); Invoke-Api "/admin/labs/$labId/schedule" 'PUT' $slot; Write-Host "Horario actualizado" }
function AddEquipment { param($labId,$identifier,$type) $r=Invoke-Api "/admin/labs/$labId/equipment" 'POST' @{ identifier=$identifier; type=$type }; $script:EqId=$r.id; Write-Host "Equipo Id=$($script:EqId)" }
Write-Host "Registro de administrador"
$n=Read-Host "Nombre"; $e=Read-Host "Correo"; $p=Read-Host "Teléfono"; $pw=Read-Host "Contraseña"
try { Register $n $e $p $pw } catch { Write-Host $_.Exception.Message }
Write-Host "Login de administrador"
$le=Read-Host "Correo"; $lpw=Read-Host "Contraseña"; try { Login $le $lpw } catch { Write-Host $_.Exception.Message; exit 1 }
Write-Host "Crear laboratorio"
$lc=Read-Host "Código"; $ln=Read-Host "Nombre"; $cap=Read-Host "Aforo"; try { CrearLab $lc $ln $cap } catch { Write-Host $_.Exception.Message }
Write-Host "Configurar horario"
$day=Read-Host "Día ej: MONDAY"; $st=Read-Host "Inicio HH:MM"; $en=Read-Host "Fin HH:MM"; try { SetSchedule $script:LabId $day $st $en } catch { Write-Host $_.Exception.Message }
Write-Host "Agregar equipo"
$id=Read-Host "Identificador"; $ty=Read-Host "Tipo"; try { AddEquipment $script:LabId $id $ty } catch { Write-Host $_.Exception.Message }
Write-Host "Hecho"