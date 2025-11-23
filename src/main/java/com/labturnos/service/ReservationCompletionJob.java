package com.labturnos.service;

import com.labturnos.domain.Equipment;
import com.labturnos.domain.EquipmentStatus;
import com.labturnos.domain.Reservation;
import com.labturnos.domain.ReservationStatus;
import com.labturnos.repository.EquipmentRepository;
import com.labturnos.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class ReservationCompletionJob {
  private final ReservationRepository reservations;
  private final EquipmentRepository equipment;

  public ReservationCompletionJob(ReservationRepository reservations, EquipmentRepository equipment) {
    this.reservations = reservations;
    this.equipment = equipment;
  }

  @Scheduled(fixedDelay = 60000)
  public void run() {
    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();
    List<Reservation> all = reservations.findAll();
    all.stream().filter(r -> (r.getStatus() == ReservationStatus.ACTIVA || r.getStatus() == ReservationStatus.MODIFICADA) && (r.getDate().isBefore(today) || (r.getDate().isEqual(today) && r.getEndTime().isBefore(now)))).forEach(r -> {
      r.setStatus(ReservationStatus.COMPLETADA);
      reservations.save(r);
      Equipment e = equipment.findById(r.getEquipmentId()).orElse(null);
      if (e != null && e.getStatus() == EquipmentStatus.EN_USO) {
        e.setStatus(EquipmentStatus.DISPONIBLE);
        equipment.save(e);
      }
    });
  }
}