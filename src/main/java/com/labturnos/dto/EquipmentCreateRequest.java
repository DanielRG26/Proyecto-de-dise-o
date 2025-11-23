package com.labturnos.dto;

import jakarta.validation.constraints.NotBlank;

public class EquipmentCreateRequest {
  @NotBlank
  private String identifier;
  @NotBlank
  private String type;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}