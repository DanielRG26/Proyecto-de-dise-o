package com.labturnos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateReservationRequest {
  @NotBlank
  private String labCode;
  @NotBlank
  private String equipmentIdentifier;
  @NotNull
  private LocalDate date;
  @NotNull
  private LocalTime startTime;
  @NotNull
  private LocalTime endTime;

  public String getLabCode() {
    return labCode;
  }

  public void setLabCode(String labCode) {
    this.labCode = labCode;
  }

  public String getEquipmentIdentifier() {
    return equipmentIdentifier;
  }

  public void setEquipmentIdentifier(String equipmentIdentifier) {
    this.equipmentIdentifier = equipmentIdentifier;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }
}