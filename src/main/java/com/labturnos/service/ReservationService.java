package com.labturnos.service;

import com.labturnos.domain.*;
import com.labturnos.dto.CreateReservationRequest;
import com.labturnos.dto.ModifyReservationRequest;
import com.labturnos.repository.EquipmentRepository;
import com.labturnos.repository.LabRepository;
import com.labturnos.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {
  private final ReservationRepository reservations;
  private final LabRepository labs;
  private final EquipmentRepository equipmentRepo;
  private final NotificationService notifications;

  public ReservationService(ReservationRepository reservations, LabRepository labs, EquipmentRepository equipmentRepo, NotificationService notifications) {
    this.reservations = reservations;
    this.labs = labs;
    this.equipmentRepo = equipmentRepo;
    this.notifications = notifications;
  }

  private boolean overlaps(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
    return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
  }

  private void ensureLabOpen(Lab lab, LocalDate date, LocalTime start, LocalTime end) {
    DayOfWeek d = date.getDayOfWeek();
    boolean ok = lab.getSchedule().stream().anyMatch(s -> s.getDayOfWeek() == d && !start.isBefore(s.getStart()) && !end.isAfter(s.getEnd()));
    if (!ok) throw new IllegalStateException("Laboratorio cerrado en ese horario");
  }

  private void ensureCapacity(Lab lab, LocalDate date, LocalTime start, LocalTime end) {
    List<Reservation> rs = reservations.findByLabIdAndDateAndStatus(lab.getId(), date, ReservationStatus.ACTIVA);
    long concurrent = rs.stream().filter(r -> overlaps(start, end, r.getStartTime(), r.getEndTime())).count();
    if (concurrent >= lab.getCapacity()) throw new IllegalStateException("Aforo completo");
  }

  private void ensureEquipmentAvailable(Equipment e, LocalDate date, LocalTime start, LocalTime end) {
    if (e.getStatus() == EquipmentStatus.MANTENIMIENTO) throw new IllegalStateException("Equipo en mantenimiento");
    List<Reservation> rs = reservations.findByEquipmentIdAndDateAndStatus(e.getId(), date, ReservationStatus.ACTIVA);
    boolean conflict = rs.stream().anyMatch(r -> overlaps(start, end, r.getStartTime(), r.getEndTime()));
    if (conflict) throw new IllegalStateException("Equipo ocupado en ese horario");
  }

  public Reservation create(String studentId, CreateReservationRequest req) {
    Lab lab = labs.findByCode(req.getLabCode()).orElseThrow(() -> new IllegalStateException("Laboratorio no encontrado"));
    Equipment e = equipmentRepo.findByIdentifier(req.getEquipmentIdentifier()).orElseThrow(() -> new IllegalStateException("Equipo no encontrado"));
    if (!lab.getId().equals(e.getLabId())) throw new IllegalStateException("Equipo no pertenece al laboratorio");
    if (!req.getStartTime().isBefore(req.getEndTime())) throw new IllegalStateException("Horario inválido");
    ensureLabOpen(lab, req.getDate(), req.getStartTime(), req.getEndTime());
    ensureCapacity(lab, req.getDate(), req.getStartTime(), req.getEndTime());
    ensureEquipmentAvailable(e, req.getDate(), req.getStartTime(), req.getEndTime());
    Reservation r = new Reservation();
    r.setStudentId(studentId);
    r.setLabId(lab.getId());
    r.setEquipmentId(e.getId());
    r.setDate(req.getDate());
    r.setStartTime(req.getStartTime());
    r.setEndTime(req.getEndTime());
    r.setStatus(ReservationStatus.ACTIVA);
    Reservation saved = reservations.save(r);
    e.setStatus(EquipmentStatus.EN_USO);
    equipmentRepo.save(e);
    notifications.push(studentId, NotificationType.RESERVA_CONFIRMADA, "Reserva confirmada " + saved.getId());
    return saved;
  }

  public Reservation modify(String studentId, String reservationId, ModifyReservationRequest req) {
    Reservation r = reservations.findById(reservationId).orElseThrow(() -> new IllegalStateException("Reserva no encontrada"));
    if (!studentId.equals(r.getStudentId())) throw new IllegalStateException("No autorizado");
    if (r.getStatus() != ReservationStatus.ACTIVA) throw new IllegalStateException("Reserva no activa");
    Lab lab = labs.findById(r.getLabId()).orElseThrow();
    Equipment e = equipmentRepo.findById(r.getEquipmentId()).orElseThrow();
    if (!req.getStartTime().isBefore(req.getEndTime())) throw new IllegalStateException("Horario inválido");
    ensureLabOpen(lab, req.getDate(), req.getStartTime(), req.getEndTime());
    ensureCapacity(lab, req.getDate(), req.getStartTime(), req.getEndTime());
    ensureEquipmentAvailable(e, req.getDate(), req.getStartTime(), req.getEndTime());
    r.setDate(req.getDate());
    r.setStartTime(req.getStartTime());
    r.setEndTime(req.getEndTime());
    r.setStatus(ReservationStatus.MODIFICADA);
    Reservation saved = reservations.save(r);
    notifications.push(studentId, NotificationType.RESERVA_MODIFICADA, "Reserva modificada " + saved.getId());
    return saved;
  }

  public void cancel(String studentId, String reservationId) {
    Reservation r = reservations.findById(reservationId).orElseThrow(() -> new IllegalStateException("Reserva no encontrada"));
    if (!studentId.equals(r.getStudentId())) throw new IllegalStateException("No autorizado");
    if (r.getStatus() != ReservationStatus.ACTIVA && r.getStatus() != ReservationStatus.MODIFICADA) throw new IllegalStateException("Estado inválido");
    r.setStatus(ReservationStatus.CANCELADA);
    reservations.save(r);
    Equipment e = equipmentRepo.findById(r.getEquipmentId()).orElseThrow();
    e.setStatus(EquipmentStatus.DISPONIBLE);
    equipmentRepo.save(e);
    notifications.push(studentId, NotificationType.RESERVA_CANCELADA, "Reserva cancelada " + r.getId());
  }

  public List<Reservation> history(String studentId) {
    return reservations.findByStudentIdOrderByDateDescStartTimeDesc(studentId);
  }
}