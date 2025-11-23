package com.labturnos.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("labs")
public class Lab {
  @Id
  private String id;
  @Indexed(unique = true)
  private String code;
  private String name;
  private int capacity;
  private List<ScheduleSlot> schedule = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public List<ScheduleSlot> getSchedule() {
    return schedule;
  }

  public void setSchedule(List<ScheduleSlot> schedule) {
    this.schedule = schedule;
  }
}