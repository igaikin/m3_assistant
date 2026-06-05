package io.github.m3_assistant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Контроллер для административной панели
// Аннотация @RequestMapping("/admin") делает все пути в этом классе уникальными
@Controller
@RequestMapping("/admin")
public class UserController {

// Страница панели администратора
// Теперь доступна по полному адресу: /admin/dashboard
@GetMapping("/dashboard")
public String adminPage() {
    return "admin_panel"; // Ищет admin_panel.html в папке templates
}
}