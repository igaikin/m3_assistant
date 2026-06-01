package io.github.m3_assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
public class Schedule {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private String title; // Название расписания (например, "Расписание автобусов Минск-Полоцк")

@Column(columnDefinition = "TEXT")
private String rawText; // Сырой текст, полученный после OCR распознавания

@Column(columnDefinition = "TEXT")
private String structuredJson; // Распарсенный чистый JSON с графиком для мобилки

private LocalDateTime createdAt;

// Автоматически проставляем дату создания при сохранении в БД
@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
}

// --- Геттеры и Сеттеры ---

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getTitle() { return title; }
public void setTitle(String title) { this.title = title; }

public String getRawText() { return rawText; }
public void setRawText(String rawText) { this.rawText = rawText; }

public String getStructuredJson() { return structuredJson; }
public void setStructuredJson(String structuredJson) { this.structuredJson = structuredJson; }

public LocalDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
