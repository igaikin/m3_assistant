package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Помечает интерфейс как репозиторий Spring Data
@Repository
// Наследуя JpaRepository, мы бесплатно получаем готовые методы: save(), findAll(), deleteById() и т.д.
// Мы передаем тип сущности (Schedule) и тип её первичного ключа (Long)
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}