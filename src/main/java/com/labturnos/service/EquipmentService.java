package com.labturnos.service;

import com.labturnos.domain.Equipment;
import com.labturnos.domain.EquipmentStatus;
import com.labturnos.domain.Notification;
import com.labturnos.domain.NotificationType;
import com.labturnos.repository.EquipmentRepository;
import com.labturnos.repository.NotificationRepository;
import com.labturnos.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EquipmentService {
  private final EquipmentRepository equipment;
  private final ReservationRepository reservations;
  private final NotificationRepository notifications;

  public EquipmentService(EquipmentRepository equipment, ReservationRepository reservations, NotificationRepository notifications) {
    this.equipment = equipment;
    this.reservations = reservations;
    this.notifications = notifications;
  }

  public Equipment add(String labId, String identifier, String type) {
    if (equipment.findByIdentifier(identifier).isPresent()) throw new IllegalStateException("Identificador de equipo ya existe");
    Equipment e = new Equipment();
    e.setIdentifier(identifier);
    e.setType(type);
    e.setLabId(labId);
    e.setStatus(EquipmentStatus.DISPONIBLE);
    return equipment.save(e);
  }

  public void remove(String equipmentId) {
    equipment.deleteById(equipmentId);
  }

  public Equipment require(String id) {
    return equipment.findById(id).orElseThrow(() -> new IllegalStateException("Equipo no encontrado"));
  }

  public Equipment requireByIdentifier(String identifier) {
    return equipment.findByIdentifier(identifier).orElseThrow(() -> new IllegalStateException("Equipo no encontrado"));
  }

  public Equipment block(String id) {
    Equipment e = require(id);
    e.setStatus(EquipmentStatus.MANTENIMIENTO);
    Equipment saved = equipment.save(e);
    var rs = reservations.findByStatus(com.labturnos.domain.ReservationStatus.ACTIVA);
    rs.stream().filter(r -> id.equals(r.getEquipmentId())).forEach(r -> {
      Notification n = new Notification();
      n.setUserId(r.getStudentId());
      n.setType(NotificationType.EQUIPO_MANTENIMIENTO);
      n.setMessage("Equipo en mantenimiento: " + e.getIdentifier());
      n.setCreatedAt(Instant.now());
      n.setRead(false);
      notifications.save(n);
    });
    return saved;
  }

  public Equipment unblock(String id) {
    Equipment e = require(id);
    e.setStatus(EquipmentStatus.DISPONIBLE);
    return equipment.save(e);
  }

  public List<Equipment> listByLab(String labId) {
    return equipment.findByLabId(labId);
  }
}