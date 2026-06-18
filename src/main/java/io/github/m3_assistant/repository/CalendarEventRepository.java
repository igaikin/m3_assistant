package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.CalendarEvent;
import io.github.m3_assistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
List<CalendarEvent> findByEventDateBetweenAndUser(LocalDate start, LocalDate end, User user);

List<CalendarEvent> findByUser(User user);
}