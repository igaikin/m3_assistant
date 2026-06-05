package io.github.m3_assistant.controller;

import io.github.m3_assistant.repository.UserRepository;
import io.github.m3_assistant.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

private final UserRepository userRepository;

public HomeController(UserRepository userRepository) {
    this.userRepository = userRepository;
}

@GetMapping("/home")
public String showHomePage(Model model, @AuthenticationPrincipal UserDetails currentUser) {
    // currentUser.getUsername() вернет email (если вы настроили так в UserDetailsService)
    User user = userRepository.findByEmail(currentUser.getUsername()).get();
    model.addAttribute("user", user);
    return "home"; // Имя вашего HTML шаблона
}
}