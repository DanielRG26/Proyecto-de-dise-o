package com.labturnos.service;

import com.labturnos.domain.Lab;
import com.labturnos.domain.ScheduleSlot;
import com.labturnos.dto.LabCreateRequest;
import com.labturnos.dto.ScheduleSlotDTO;
import com.labturnos.repository.LabRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class LabService {
  private final LabRepository labs;

  public LabService(LabRepository labs) {
    this.labs = labs;
  }

  public Lab create(LabCreateRequest r) {
    if (labs.findByCode(r.getCode()).isPresent()) throw new IllegalStateException("Código de laboratorio ya existe");
    Lab lab = new Lab();
    lab.setCode(r.getCode());
    lab.setName(r.getName());
    lab.setCapacity(r.getCapacity());
    return labs.save(lab);
  }

  public Lab requireByCode(String code) {
    return labs.findByCode(code).orElseThrow(() -> new IllegalStateException("Laboratorio no encontrado"));
  }

  public Lab require(String id) {
    return labs.findById(id).orElseThrow(() -> new IllegalStateException("Laboratorio no encontrado"));
  }

  public Lab updateCapacity(String id, int capacity) {
    Lab lab = require(id);
    lab.setCapacity(capacity);
    return labs.save(lab);
  }

  public Lab updateName(String id, String name) {
    Lab lab = require(id);
    lab.setName(name);
    return labs.save(lab);
  }

  public Lab setSchedule(String id, List<ScheduleSlotDTO> slots) {
    Lab lab = require(id);
    lab.setSchedule(slots.stream().map(s -> {
      ScheduleSlot slot = new ScheduleSlot();
      slot.setDayOfWeek(DayOfWeek.valueOf(s.getDayOfWeek().toUpperCase()));
      slot.setStart(s.getStart());
      slot.setEnd(s.getEnd());
      return slot;
    }).toList());
    return labs.save(lab);
  }

  public Iterable<Lab> listAll() {
    return labs.findAll();
  }
}