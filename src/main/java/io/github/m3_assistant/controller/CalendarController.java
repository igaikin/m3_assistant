package io.github.m3_assistant.controller;

import io.github.m3_assistant.model.CalendarEvent;
import io.github.m3_assistant.model.User;
import io.github.m3_assistant.repository.CalendarEventRepository;
import io.github.m3_assistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CalendarController {

private final CalendarEventRepository calendarEventRepository;
private final UserRepository userRepository;

private boolean isAdminOrManager() {
    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));
}

@GetMapping("/calendar")
public String showCalendar(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        Model model, Principal principal) {

    // Находим текущего пользователя в БД
    User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

    LocalDate now = LocalDate.now();
    int y = (year != null) ? year : now.getYear();
    int m = (month != null) ? month : now.getMonthValue();

    // 1. Вычисляем диапазон дат для текущего месяца
    LocalDate start = LocalDate.of(y, m, 1);
    LocalDate end = start.plusMonths(1).minusDays(1);

    // 2. Достаем события из БД и передаем в модель
    model.addAttribute("events", calendarEventRepository.findByEventDateBetweenAndUser(start, end, user));
    model.addAttribute("year", y);
    model.addAttribute("month", m);
    model.addAttribute("today", now);
    // Получаем события только для ЭТОГО пользователя
    model.addAttribute("canEdit", isAdminOrManager());
    return "calendar";
}

// Метод для добавления
@PostMapping("/events/add")
public String addEvent(@RequestParam String title,
                       @RequestParam String date,
                       @RequestParam String userEmail) {
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Пользователь с email " + userEmail + " не найден"));
    CalendarEvent event = new CalendarEvent();
    event.setTitle(title);
    event.setEventDate(LocalDate.parse(date)); // Преобразуем строку из формы в дату
    event.setUser(user); // Привязываем к текущему пользователю
    calendarEventRepository.save(event);
    return "redirect:/admin";
}

@PostMapping("/admin/events/update")
@ResponseBody
public ResponseEntity<String> updateEvent(@RequestBody Map<String, String> payload) {
    Long id = Long.parseLong(payload.get("id"));

    CalendarEvent event = calendarEventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Событие не найдено"));

    event.setTitle(payload.get("title"));
    event.setEventDate(LocalDate.parse(payload.get("date")));

    calendarEventRepository.save(event);
    return ResponseEntity.ok("Success");
}

// Метод для удаления
@PostMapping("admin/events/delete/{id}")
@ResponseBody // Возвращаем статус, а не страницу
public ResponseEntity<String> deleteEventApi(@PathVariable Long id) {
    calendarEventRepository.deleteById(id);
    return ResponseEntity.ok("Deleted");
}
}