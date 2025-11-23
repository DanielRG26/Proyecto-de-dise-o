package com.labturnos.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ScheduleSlot {
  private DayOfWeek dayOfWeek;
  private LocalTime start;
  private LocalTime end;

  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(DayOfWeek dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public LocalTime getStart() {
    return start;
  }

  public void setStart(LocalTime start) {
    this.start = start;
  }

  public LocalTime getEnd() {
    return end;
  }

  public void setEnd(LocalTime end) {
    this.end = end;
  }
}