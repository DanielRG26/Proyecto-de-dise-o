package com.labturnos.controller;

import com.labturnos.dto.LoginRequest;
import com.labturnos.dto.RegisterRequest;
import com.labturnos.dto.TokenResponse;
import com.labturnos.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final UserService users;

  public AuthController(UserService users) {
    this.users = users;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest r) {
    users.register(r);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest r) {
    String token = users.login(r);
    return ResponseEntity.ok(new TokenResponse(token));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication auth) {
    var u = users.require((String) auth.getPrincipal());
    return ResponseEntity.ok(java.util.Map.of(
      "id", u.getId(),
      "fullName", u.getFullName(),
      "email", u.getEmail(),
      "role", u.getRole().name()
    ));
  }
}