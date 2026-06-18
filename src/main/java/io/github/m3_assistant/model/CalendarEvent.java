package io.github.m3_assistant.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "calendar_event")
@Data
public class CalendarEvent {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String title;
private LocalDate eventDate; // Дата события
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user; // Привязка к владельцу
}