package io.github.m3_assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Указывает Hibernate, что данный класс является сущностью и привязан к таблице в БД
@Entity
// Задает имя таблицы в базе данных PostgreSQL
@Table(name = "schedules")
public class Schedule {

// Помечает поле как первичный ключ (Primary Key)
@Id
// Настраивает автоматическую генерацию ID базой данных по порядку (1, 2, 3...)
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// Обычное текстовое поле для хранения краткого заголовка
private String title;

// Аннотация @Lob (Large Object) сообщает базе данных, что текст может быть огромного объема
@Lob
private String rawText;

// Поле для фиксации точной даты и времени создания этой записи
private LocalDateTime createdAt;

// Пустой конструктор обязателен для корректной работы спецификации JPA / Hibernate
public Schedule() {}

// Конструктор для удобного создания объекта перед отправкой в репозиторий
public Schedule(String title, String rawText) {
    this.title = title;
    this.rawText = rawText;
    // Автоматически фиксируем текущее время компьютера в момент создания объекта
    this.createdAt = LocalDateTime.now();
}

// Стандартные геттеры и сеттеры для управления приватными свойствами объекта
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getTitle() { return title; }
public void setTitle(String title) { this.title = title; }

public String getRawText() { return rawText; }
public void setRawText(String rawText) { this.rawText = rawText; }

public LocalDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}