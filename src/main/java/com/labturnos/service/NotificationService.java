package com.labturnos.service;

import com.labturnos.domain.Notification;
import com.labturnos.domain.NotificationType;
import com.labturnos.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {
  private final NotificationRepository notifications;

  public NotificationService(NotificationRepository notifications) {
    this.notifications = notifications;
  }

  public void push(String userId, NotificationType type, String message) {
    Notification n = new Notification();
    n.setUserId(userId);
    n.setType(type);
    n.setMessage(message);
    n.setCreatedAt(Instant.now());
    n.setRead(false);
    notifications.save(n);
  }

  public List<Notification> list(String userId) {
    return notifications.findByUserIdOrderByCreatedAtDesc(userId);
  }

  public void markRead(String id) {
    Notification n = notifications.findById(id).orElseThrow(() -> new IllegalStateException("Notificación no encontrada"));
    n.setRead(true);
    notifications.save(n);
  }
}