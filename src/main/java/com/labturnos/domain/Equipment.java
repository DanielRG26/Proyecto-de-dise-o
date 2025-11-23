package com.labturnos.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("equipment")
public class Equipment {
  @Id
  private String id;
  @Indexed(unique = true)
  private String identifier;
  private String type;
  private EquipmentStatus status;
  private String labId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public EquipmentStatus getStatus() {
    return status;
  }

  public void setStatus(EquipmentStatus status) {
    this.status = status;
  }

  public String getLabId() {
    return labId;
  }

  public void setLabId(String labId) {
    this.labId = labId;
  }
}