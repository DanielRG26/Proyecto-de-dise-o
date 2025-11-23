package com.labturnos.service;

import com.labturnos.domain.Role;
import com.labturnos.domain.User;
import com.labturnos.dto.LoginRequest;
import com.labturnos.dto.RegisterRequest;
import com.labturnos.repository.UserRepository;
import com.labturnos.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;

  public UserService(UserRepository users, PasswordEncoder encoder, JwtService jwtService) {
    this.users = users;
    this.encoder = encoder;
    this.jwtService = jwtService;
  }

  public void register(RegisterRequest r) {
    if (users.findByEmail(r.getEmail()).isPresent()) throw new IllegalStateException("Correo ya registrado");
    User u = new User();
    u.setFullName(r.getFullName());
    u.setEmail(r.getEmail());
    u.setPhone(r.getPhone());
    u.setPasswordHash(encoder.encode(r.getPassword()));
    Role desired = null;
    if (r.getRole() != null) {
      try { desired = Role.valueOf(r.getRole().toUpperCase()); } catch (Exception ignored) {}
    }
    long count = users.count();
    if (desired == Role.ADMINISTRADOR) {
      u.setRole(Role.ADMINISTRADOR);
    } else if (desired == Role.ESTUDIANTE) {
      u.setRole(Role.ESTUDIANTE);
    } else {
      u.setRole(count == 0 ? Role.ADMINISTRADOR : Role.ESTUDIANTE);
    }
    u.setCreatedAt(Instant.now());
    users.save(u);
  }

  public String login(LoginRequest r) {
    User u = users.findByEmail(r.getEmail()).orElseThrow(() -> new IllegalStateException("Credenciales inválidas"));
    if (!encoder.matches(r.getPassword(), u.getPasswordHash())) throw new IllegalStateException("Credenciales inválidas");
    return jwtService.generate(u.getId(), u.getRole());
  }

  public User require(String userId) {
    return users.findById(userId).orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
  }

  public Iterable<User> listAll() {
    return users.findAll();
  }

  public void deleteUser(String userId) {
    users.deleteById(userId);
  }

  public void changeRole(String userId, Role role) {
    User u = require(userId);
    u.setRole(role);
    users.save(u);
  }
}