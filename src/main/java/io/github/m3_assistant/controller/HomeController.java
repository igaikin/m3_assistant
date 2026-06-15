package io.github.m3_assistant.controller;

import io.github.m3_assistant.repository.UserRepository;
import io.github.m3_assistant.model.User;
import io.github.m3_assistant.service.FileStorageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class HomeController {

private final UserRepository userRepository;
private final FileStorageService storageService;

public HomeController(UserRepository userRepository, FileStorageService storageService) {
    this.userRepository = userRepository;
    this.storageService = storageService;
}

@GetMapping("/home")
public String showHomePage(Model model, @AuthenticationPrincipal UserDetails currentUser) throws IOException {
    // currentUser.getUsername() вернет email (если вы настроили так в UserDetailsService)
    User user = userRepository.findByEmail(currentUser.getUsername()).get();
    model.addAttribute("user", user);
    // Добавляем список файлов в модель, чтобы он был доступен на главной
    model.addAttribute("fileStructure", storageService.listFilesAndFolders());
    return "home"; // Имя вашего HTML шаблона
}
}