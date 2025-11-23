package com.labturnos.controller;

import com.labturnos.domain.Equipment;
import com.labturnos.domain.Role;
import com.labturnos.dto.ChangeRoleRequest;
import com.labturnos.dto.EquipmentCreateRequest;
import com.labturnos.dto.LabCreateRequest;
import com.labturnos.dto.ScheduleSlotDTO;
import com.labturnos.service.EquipmentService;
import com.labturnos.service.LabService;
import com.labturnos.service.UserService;
import com.labturnos.repository.ReservationRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {
  private final LabService labs;
  private final EquipmentService equipment;
  private final UserService users;
  private final ReservationRepository reservations;

  public AdminController(LabService labs, EquipmentService equipment, UserService users, ReservationRepository reservations) {
    this.labs = labs;
    this.equipment = equipment;
    this.users = users;
    this.reservations = reservations;
  }

  @PostMapping("/labs")
  public ResponseEntity<?> crearLab(@Valid @RequestBody LabCreateRequest req) {
    return ResponseEntity.ok(labs.create(req));
  }

  @GetMapping("/labs")
  public ResponseEntity<?> listarLabs() {
    return ResponseEntity.ok(labs.listAll());
  }

  @PutMapping("/labs/{id}/capacity/{capacity}")
  public ResponseEntity<?> actualizarAforo(@PathVariable String id, @PathVariable int capacity) {
    return ResponseEntity.ok(labs.updateCapacity(id, capacity));
  }

  @PutMapping("/labs/{id}/schedule")
  public ResponseEntity<?> configurarHorario(@PathVariable String id, @Valid @RequestBody List<ScheduleSlotDTO> slots) {
    return ResponseEntity.ok(labs.setSchedule(id, slots));
  }

  @PostMapping("/labs/{id}/equipment")
  public ResponseEntity<Equipment> agregarEquipo(@PathVariable String id, @Valid @RequestBody EquipmentCreateRequest req) {
    return ResponseEntity.ok(equipment.add(id, req.getIdentifier(), req.getType()));
  }

  @GetMapping("/labs/{id}/equipment")
  public ResponseEntity<?> listarEquiposPorLab(@PathVariable String id) {
    return ResponseEntity.ok(equipment.listByLab(id));
  }

  @DeleteMapping("/equipment/{id}")
  public ResponseEntity<Void> eliminarEquipo(@PathVariable String id) {
    equipment.remove(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/equipment/{id}/block")
  public ResponseEntity<Equipment> bloquearEquipo(@PathVariable String id) {
    return ResponseEntity.ok(equipment.block(id));
  }

  @PostMapping("/equipment/{id}/unblock")
  public ResponseEntity<Equipment> desbloquearEquipo(@PathVariable String id) {
    return ResponseEntity.ok(equipment.unblock(id));
  }

  @GetMapping("/equipment")
  public ResponseEntity<?> listarEquipos() {
    return ResponseEntity.ok(equipment.listAll());
  }

  @GetMapping("/reports/uso")
  public ResponseEntity<?> reporteUso() {
    var labsList = labs.listAll();
    var data = new java.util.ArrayList<Map<String, Object>>();
    labsList.forEach(l -> {
      var eqs = equipment.listByLab(l.getId());
      data.add(Map.of(
        "laboratorio", l.getCode(),
        "total_equipos", eqs.size(),
        "aforo", l.getCapacity(),
        "total_reservas", reservations.countByLabId(l.getId())
      ));
    });
    return ResponseEntity.ok(data);
  }

  @GetMapping("/reports/mantenimiento")
  public ResponseEntity<?> reporteMantenimiento() {
    var labsList = labs.listAll();
    var data = new java.util.ArrayList<Map<String, Object>>();
    labsList.forEach(l -> {
      var eqs = equipment.listByLab(l.getId());
      long mant = eqs.stream().filter(e -> e.getStatus() == com.labturnos.domain.EquipmentStatus.MANTENIMIENTO).count();
      data.add(Map.of(
        "laboratorio", l.getCode(),
        "equipos_mantenimiento", mant
      ));
    });
    return ResponseEntity.ok(data);
  }

  @GetMapping("/users")
  public ResponseEntity<?> listarUsuarios() {
    return ResponseEntity.ok(users.listAll());
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
    users.deleteUser(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/users/{id}/role")
  public ResponseEntity<Void> cambiarRol(@PathVariable String id, @Valid @RequestBody ChangeRoleRequest req) {
    users.changeRole(id, Role.valueOf(req.getRole().toUpperCase()));
    return ResponseEntity.ok().build();
  }

  @GetMapping("/reports/usuarios-activos")
  public ResponseEntity<?> usuariosActivos() {
    var activos = reservations.findByStatus(com.labturnos.domain.ReservationStatus.ACTIVA).stream().map(r -> r.getStudentId()).distinct().toList();
    return ResponseEntity.ok(Map.of("usuarios_activos", activos));
  }
}