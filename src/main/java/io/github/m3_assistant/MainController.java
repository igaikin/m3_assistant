package io.github.m3_assistant;

import io.github.m3_assistant.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Указывает, что класс обрабатывает веб-страницы и перенаправляет на HTML-шаблоны
@Controller
public class MainController {

// Связь со слоем бизнес-логики
private final ScheduleService scheduleService;

// Внедрение сервиса через конструктор
public MainController(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
}

// Обрабатывает GET-запрос на главную страницу (http://localhost:8080/)
@GetMapping("/")
public String index(Model model) {
    // Вытягиваем все расписания из базы данных и крепим их к объекту модели под именем "schedules"
    model.addAttribute("schedules", scheduleService.getAllSchedules());
    // Возвращает имя HTML файла шаблона ("index"), который лежит в папке templates
    return "index";
}

// Обрабатывает POST-запрос при отправке заполненной формы (клик по кнопке «Сохранить»)
@PostMapping("/add-schedule")
public String addSchedule(@RequestParam("title") String title, @RequestParam("rawText") String rawText) {
    // Передаем полученные текстовые параметры формы в сервис для физической записи в Postgres
    scheduleService.saveSchedule(title, rawText);
    // Делаем перенаправление (редирект) обратно на главную страницу, чтобы обновить таблицу на экране
    return "redirect:/";
}
}