package com.labturnos.controller;

import com.labturnos.domain.Equipment;
import com.labturnos.domain.EquipmentStatus;
import com.labturnos.domain.Notification;
import com.labturnos.domain.Reservation;
import com.labturnos.service.EquipmentService;
import com.labturnos.service.LabService;
import com.labturnos.service.NotificationService;
import com.labturnos.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('ESTUDIANTE')")
public class StudentController {
  private final LabService labs;
  private final EquipmentService equipment;
  private final ReservationService reservations;
  private final NotificationService notifications;

  public StudentController(LabService labs, EquipmentService equipment, ReservationService reservations, NotificationService notifications) {
    this.labs = labs;
    this.equipment = equipment;
    this.reservations = reservations;
    this.notifications = notifications;
  }

  @GetMapping("/availability")
  public ResponseEntity<?> availability() {
    var list = labs.listAll();
    var map = new java.util.ArrayList<Map<String, Object>>();
    list.forEach(l -> {
      var eqs = equipment.listByLab(l.getId());
      long disponibles = eqs.stream().filter(e -> e.getStatus() == EquipmentStatus.DISPONIBLE).count();
      long enUso = eqs.stream().filter(e -> e.getStatus() == EquipmentStatus.EN_USO).count();
      long mantenimiento = eqs.stream().filter(e -> e.getStatus() == EquipmentStatus.MANTENIMIENTO).count();
      map.add(Map.of(
        "laboratorio", l.getCode(),
        "equipos_disponibles", disponibles,
        "equipos_en_uso", enUso,
        "equipos_mantenimiento", mantenimiento
      ));
    });
    return ResponseEntity.ok(map);
  }

  @GetMapping("/labs/available")
  public ResponseEntity<?> labsAvailable() {
    var list = labs.listAll();
    var data = new java.util.ArrayList<java.util.Map<String, Object>>();
    list.forEach(l -> {
      var eqs = equipment.listByLab(l.getId());
      var recursos = eqs.stream()
        .map(e -> java.util.Map.of(
          "identifier", e.getIdentifier(),
          "type", e.getType(),
          "status", e.getStatus().name()
        ))
        .toList();
      data.add(java.util.Map.of(
        "laboratorio", l.getCode(),
        "equipos", recursos
      ));
    });
    return ResponseEntity.ok(data);
  }

  @PostMapping("/reservas")
  public ResponseEntity<Reservation> reservar(Authentication auth, @Valid @RequestBody com.labturnos.dto.CreateReservationRequest req) {
    Reservation r = reservations.create(auth.getName(), req);
    return ResponseEntity.ok(r);
  }

  @PutMapping("/reservas/{id}")
  public ResponseEntity<Reservation> modificar(Authentication auth, @PathVariable String id, @Valid @RequestBody com.labturnos.dto.ModifyReservationRequest req) {
    Reservation r = reservations.modify(auth.getName(), id, req);
    return ResponseEntity.ok(r);
  }

  @DeleteMapping("/reservas/{id}")
  public ResponseEntity<Void> cancelar(Authentication auth, @PathVariable String id) {
    reservations.cancel(auth.getName(), id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/reservas")
  public ResponseEntity<?> historial(Authentication auth) {
    return ResponseEntity.ok(reservations.history(auth.getName()));
  }

  @GetMapping("/notificaciones")
  public ResponseEntity<?> notificaciones(Authentication auth) {
    return ResponseEntity.ok(notifications.list(auth.getName()));
  }

  @PostMapping("/notificaciones/{id}/leer")
  public ResponseEntity<Void> leer(@PathVariable String id) {
    notifications.markRead(id);
    return ResponseEntity.ok().build();
  }
}