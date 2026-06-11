package io.github.m3_assistant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

/**
 * Обрабатывает запрос на страницу входа.
 *
 * @param error Если в URL есть параметр ?error, Spring Security сигнализирует об ошибке входа.
 * @param model Передаем сообщение об ошибке в шаблон, если она есть.
 */
@GetMapping("/login")
public String login(@RequestParam(value = "error", required = false) String error,
                    @RequestParam(value = "logout", required = false) String logout,
                    Model model) {

    // Если была ошибка при входе, добавляем сообщение в модель
    if (error != null) {
        model.addAttribute("error", "Неверный логин или пароль!");
    }

    // Дополнительно можно добавить сообщение при выходе
    if (logout != null) {
        model.addAttribute("message", "Вы успешно вышли из системы.");
    }

    return "login"; // Возвращает шаблон login.html
}
}