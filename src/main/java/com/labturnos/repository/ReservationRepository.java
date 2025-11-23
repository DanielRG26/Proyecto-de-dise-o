package com.labturnos.repository;

import com.labturnos.domain.Reservation;
import com.labturnos.domain.ReservationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
  List<Reservation> findByStudentIdOrderByDateDescStartTimeDesc(String studentId);
  List<Reservation> findByEquipmentIdAndDateAndStatus(String equipmentId, LocalDate date, ReservationStatus status);
  List<Reservation> findByLabIdAndDateAndStatus(String labId, LocalDate date, ReservationStatus status);
  long countByLabId(String labId);
  List<Reservation> findByStatus(ReservationStatus status);
}