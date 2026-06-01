package io.github.m3_assistant.service;

import io.github.m3_assistant.model.Schedule;
import io.github.m3_assistant.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

// Регистрирует класс в Spring-контексте как компонент сервисного слоя
@Service
public class ScheduleService {

// Финальная ссылка на репозиторий для безопасного обращения к БД
private final ScheduleRepository scheduleRepository;

// Внедрение зависимости репозитория через конструктор (Рекомендуемый подход Spring)
public ScheduleService(ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
}

// Бизнес-метод для получения абсолютно всех строк из таблицы schedules
public List<Schedule> getAllSchedules() {
    return scheduleRepository.findAll();
}

// Бизнес-метод для упаковки параметров с формы в объект Schedule и сохранения в Postgres
public void saveSchedule(String title, String rawText) {
    Schedule schedule = new Schedule(title, rawText);
    scheduleRepository.save(schedule);
}
}