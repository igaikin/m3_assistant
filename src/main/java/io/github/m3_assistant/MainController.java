package io.github.m3_assistant;

import io.github.m3_assistant.model.Schedule;
import io.github.m3_assistant.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

private final ScheduleService scheduleService;

// Внедряем наш сервис с бизнес-логикой
public MainController(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
}

@GetMapping("/")
public String index(Model model) {
    // При каждом заходе на главную страницу вытаскиваем из БД список всех сохраненных расписаний
    model.addAttribute("schedules", scheduleService.getAllSchedules());
    return "index";
}

@PostMapping("/add-schedule")
public String addSchedule(@RequestParam("title") String title,
                          @RequestParam("rawText") String rawText,
                          Model model) {

    // Сохраняем данные в базу через сервис
    scheduleService.saveSchedule(title, rawText);

    // Обновляем список на экране и возвращаем на главную
    return "redirect:/";
}
}