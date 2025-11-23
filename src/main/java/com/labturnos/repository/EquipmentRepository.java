package com.labturnos.repository;

import com.labturnos.domain.Equipment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends MongoRepository<Equipment, String> {
  Optional<Equipment> findByIdentifier(String identifier);
  List<Equipment> findByLabId(String labId);
}