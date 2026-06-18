package io.github.m3_assistant.controller;

import io.github.m3_assistant.model.CalendarEvent;
import io.github.m3_assistant.repository.CalendarEventRepository;
import io.github.m3_assistant.repository.UserRepository;
import io.github.m3_assistant.model.User;
import io.github.m3_assistant.service.FileStorageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

private final UserRepository userRepository;
private final FileStorageService storageService;
private final CalendarEventRepository calendarEventRepository;

public HomeController(UserRepository userRepository, FileStorageService storageService, CalendarEventRepository calendarEventRepository) {
    this.userRepository = userRepository;
    this.storageService = storageService;
    this.calendarEventRepository = calendarEventRepository;
}

@GetMapping("/home")
public String showHomePage(Model model, @AuthenticationPrincipal UserDetails currentUser) throws IOException {
    User user = userRepository.findByEmail(currentUser.getUsername()).get();
    model.addAttribute("user", user);
    // Добавляем список файлов в модель, чтобы он был доступен на главной
    model.addAttribute("fileStructure", storageService.listFilesAndFolders());
    // Получаем только актуальные события для этого пользователя
    List<CalendarEvent> upcomingEvents = calendarEventRepository
            .findByUserAndEventDateGreaterThanEqualOrderByEventDateAsc(user, LocalDate.now());
    model.addAttribute("upcomingEvents", upcomingEvents); // Передаем в модель
    return "home"; // Имя вашего HTML шаблона
}
}