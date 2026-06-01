package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
// Базовые методы вроде save(), findById(), delete() уже встроены сюда по умолчанию!
}