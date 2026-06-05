package io.github.m3_assistant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Контроллер для работы с публичными страницами
@Controller
public class MainController {

// Страница входа (доступна по адресу /login)
@GetMapping("/login")
public String login() {
    return "login"; // Ищет login.html в папке templates
}
}