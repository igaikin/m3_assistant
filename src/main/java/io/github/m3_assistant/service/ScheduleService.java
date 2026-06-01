package io.github.m3_assistant.service;

import io.github.m3_assistant.model.Schedule;
import io.github.m3_assistant.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScheduleService {

private final ScheduleRepository scheduleRepository;

// Внедряем репозиторий через конструктор
public ScheduleService(ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
}

// Метод для сохранения нового расписания в базу данных
public Schedule saveSchedule(String title, String rawText) {
    Schedule schedule = new Schedule();
    schedule.setTitle(title);
    schedule.setRawText(rawText);

    // Тут в будущем будет вызов метода OCR для парсинга сырого текста в структурированный вид
    schedule.setStructuredJson("{ \"status\": \"not_parsed_yet\" }");

    return scheduleRepository.save(schedule);
}

// Метод для получения всех расписаний из базы данных
public List<Schedule> getAllSchedules() {
    return scheduleRepository.findAll();
}
}