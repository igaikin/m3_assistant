package io.github.m3_assistant.controller;

import io.github.m3_assistant.model.User;
import io.github.m3_assistant.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
// Защита: только пользователи с ролью ADMIN или MANAGER могут зайти в этот контроллер
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AdminController {

private final UserRepository userRepository;
private final RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;
private final QualificationClassRepository qualificationClassRepository;
private final TrainRepository trainRepository;
private final ProfessionRepository professionRepository;// 1. Добавляем поле

public AdminController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       QualificationClassRepository qualificationClassRepository, TrainRepository trainRepository,
                       ProfessionRepository professionRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.qualificationClassRepository = qualificationClassRepository;
    this.trainRepository = trainRepository;
    this.professionRepository = professionRepository;
}

// Отображение списка пользователей: URL будет /admin/users
@GetMapping("/users")
public String listUsers(Model model, @AuthenticationPrincipal UserDetails currentUser) {

    // 1. Получаем текущего админа из БД
    User admin = userRepository.findByEmail(currentUser.getUsername())
            .orElseThrow(() -> new RuntimeException("Администратор не найден"));
    // 2. Получаем всех, кроме текущего админа
    List<User> users = userRepository.findByIdNot(admin.getId());
    // 3. Передаем в шаблон
    model.addAttribute("users", users);

    return "admin_users_list"; // Имя вашего HTML шаблона
}

@GetMapping("")
public String adminDashboard() {
    return "admin_panel"; // Убедитесь, что у вас есть файл templates/admin_dashboard.html
}
// --- МЕТОДЫ CRUD ---

@GetMapping("/users/delete/{id}")
@PreAuthorize("hasRole('ADMIN')") // Удаление разрешено только Админу
public String deleteUser(@PathVariable Long id) {
    userRepository.deleteById(id);
    return "redirect:/admin/users";
}

@GetMapping("/users/edit/{id}")
public String editUserForm(@PathVariable Long id, Model model) {
    model.addAttribute("user", userRepository.findById(id).orElseThrow());
    model.addAttribute("roles", roleRepository.findAll()); // Список ролей для выпадающего списка
    model.addAttribute("qualification_classes", qualificationClassRepository.findAll()); // Список ролей для выпадающего списка
    model.addAttribute("trains", trainRepository.findAll()); // Список составов для выпадающего списка
    model.addAttribute("professions", professionRepository.findAll()); // Список профессий для выпадающего списка
    return "edit_user";
}

@PostMapping("/users/update")
public String updateUser(@ModelAttribute User user) {
    // 1. Ищем существующего пользователя в БД по ID из формы
    User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    // 2. Обновляем только те поля, которые разрешено менять
    existingUser.setFirstName(user.getFirstName());
    existingUser.setRole(user.getRole());
    existingUser.setQualificationClass(user.getQualificationClass());
    existingUser.setTrain(user.getTrain());
    existingUser.setProfession(user.getProfession());
    // 3. Сохраняем обратно, пароль при этом остается нетронутым!
    userRepository.save(existingUser);

    return "redirect:/admin/users";
}

// 1. Показ формы для создания нового пользователя
@GetMapping("/users/add")
public String addUserForm(Model model) {
    model.addAttribute("user", new User()); // Создаем пустой объект
    model.addAttribute("roles", roleRepository.findAll()); // Список ролей
    model.addAttribute("qualification_classes", qualificationClassRepository.findAll());
    model.addAttribute("trains", trainRepository.findAll());
    model.addAttribute("professions", professionRepository.findAll());
    return "add_user"; // Имя шаблона
}

// 2. Обработка сохранения нового пользователя
@PostMapping("/users/create")
public String createUser(@ModelAttribute User user, Model model) {
    // 1. Проверяем, существует ли уже пользователь с таким email
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        // Если есть - возвращаем обратно на форму с ошибкой
        model.addAttribute("error", "Пользователь с таким email уже существует!");
        model.addAttribute("roles", roleRepository.findAll()); // Нужно передать список ролей снова
        model.addAttribute("qualification_classes", qualificationClassRepository.findAll());
        model.addAttribute("trains", trainRepository.findAll());
        model.addAttribute("professions", professionRepository.findAll());
        return "add_user";
    }

    // 2. Если всё ок, шифруем пароль и сохраняем
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    return "redirect:/admin/users";
}
}