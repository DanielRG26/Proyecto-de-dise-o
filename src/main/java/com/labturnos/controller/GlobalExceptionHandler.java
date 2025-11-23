package com.labturnos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.badRequest().body(Map.of("error", "Error de validación", "details", errors));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleJsonParseError(HttpMessageNotReadableException ex) {
    String message = ex.getMessage();
    if (message != null && message.contains("LocalTime")) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Formato de hora inválido",
        "message", "Las horas deben estar en formato HH:mm (ej: 08:00, 14:30)",
        "details", message
      ));
    }
    if (message != null && message.contains("DayOfWeek")) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Día de la semana inválido",
        "message", "Los días válidos son: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY",
        "details", message
      ));
    }
    return ResponseEntity.badRequest().body(Map.of("error", "Error al procesar el JSON", "details", message != null ? message : "Formato JSON inválido"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
    String message = ex.getMessage();
    if (message != null && message.contains("DayOfWeek")) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Día de la semana inválido",
        "message", "Los días válidos son: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY"
      ));
    }
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }
}