package com.labturnos.repository;

import com.labturnos.domain.Lab;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LabRepository extends MongoRepository<Lab, String> {
  Optional<Lab> findByCode(String code);
}