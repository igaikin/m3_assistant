package io.github.m3_assistant;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

// Этот метод просто показывает главную страницу при переходе на localhost:8080
@GetMapping("/")
public String index() {
    return "index";
}

// А этот метод будет срабатывать, когда пользователь нажмет кнопку на форме
@PostMapping("/send")
public String handleForm(@RequestParam("userInput") String inputText, Model model) {

    // Здесь мы можем делать с текстом всё что угодно.
    // Пока просто прибавим к нему строку и отправим обратно на экран:
    String serverResponse = "Бэкенд успешно принял ваш текст: \"" + inputText + "\"";

    // Кладем ответ сервера в специальную коробку (Model), чтобы HTML его увидел
    model.addAttribute("responseMessage", serverResponse);

    // Возвращаем пользователя на ту же страницу index.html, но уже с результатом
    return "index";
}
}