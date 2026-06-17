package io.github.m3_assistant.controller;

import io.github.m3_assistant.model.CalendarEvent;
import io.github.m3_assistant.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class CalendarController {

private final CalendarEventRepository calendarEventRepository;

private boolean isAdminOrManager() {
    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));
}

@GetMapping("/calendar")
public String showCalendar(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        Model model) {

    LocalDate now = LocalDate.now();
    int y = (year != null) ? year : now.getYear();
    int m = (month != null) ? month : now.getMonthValue();

    // 1. Вычисляем диапазон дат для текущего месяца
    LocalDate start = LocalDate.of(y, m, 1);
    LocalDate end = start.plusMonths(1).minusDays(1);

    // 2. Достаем события из БД и передаем в модель
    model.addAttribute("events", calendarEventRepository.findByEventDateBetween(start, end));

    model.addAttribute("year", y);
    model.addAttribute("month", m);
    model.addAttribute("today", now);
    model.addAttribute("canEdit", isAdminOrManager());
    return "calendar";
}

// Метод для добавления
@PostMapping("/events/add")
public String addEvent(@RequestParam String title, @RequestParam String date) {
    CalendarEvent event = new CalendarEvent();
    event.setTitle(title);
    event.setEventDate(LocalDate.parse(date)); // Преобразуем строку из формы в дату
    calendarEventRepository.save(event);
    return "redirect:/calendar";
}

// Метод для удаления
@PostMapping("/events/delete/{id}")
public String deleteEvent(@PathVariable Long id) {
    // Сначала находим событие, чтобы узнать его дату
    CalendarEvent event = calendarEventRepository.findById(id).orElse(null);
    calendarEventRepository.deleteById(id);

    if (event != null) {
        // Возвращаемся в тот же месяц, где было событие
        return "redirect:/calendar?year=" + event.getEventDate().getYear()
                + "&month=" + event.getEventDate().getMonthValue();
    }
    return "redirect:/calendar";
}
}