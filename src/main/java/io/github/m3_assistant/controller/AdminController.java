package io.github.m3_assistant.controller;

// Импорт моделей, репозиториев и необходимых библиотек Spring

import io.github.m3_assistant.model.CalendarEvent;
import io.github.m3_assistant.model.User;
import io.github.m3_assistant.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.io.IOException;

import static io.github.m3_assistant.model.User.DEFAULT_AVATAR;

/**
 * Контроллер административной панели.
 *
 * @Controller помечает класс как веб-контроллер, обрабатывающий HTTP-запросы.
 * @RequestMapping("/admin") задает базовый URL для всех методов внутри (напр. /admin/users).
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // Ограничение доступа: только для ADMIN и MANAGER
public class AdminController {

// Внедрение зависимостей (Repositories для БД, PasswordEncoder для шифрования)
private final UserRepository userRepository;
private final RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;
private final QualificationClassRepository qualificationClassRepository;
private final TrainRepository trainRepository;
private final ProfessionRepository professionRepository;
private final CalendarEventRepository calendarEventRepository;

// Конструктор Spring автоматически подставляет (inject) все нужные репозитории
public AdminController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       QualificationClassRepository qualificationClassRepository, TrainRepository trainRepository,
                       ProfessionRepository professionRepository, CalendarEventRepository calendarEvenRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.qualificationClassRepository = qualificationClassRepository;
    this.trainRepository = trainRepository;
    this.professionRepository = professionRepository;
    this.calendarEventRepository = calendarEvenRepository;
}

/**
 * Отображает главную страницу панели администратора.
 */
@GetMapping("")
public String adminDashboard(Model model) {
    // Передаем всех пользователей, чтобы администратор мог выбрать владельца события
    List<User> users = userRepository.findAll();
//    model.addAttribute("allUsers", userRepository.findAll());
    System.out.println("DEBUG: Найдено пользователей в БД: " + users.size()); // Смотрите в консоль IDE
    System.out.println("Количество найденных пользователей: " + users.size());
    model.addAttribute("allUsers", users);
    return "admin_panel"; // Возвращает имя HTML-файла шаблона
}

/**
 * Список всех пользователей.
 *
 * @AuthenticationPrincipal позволяет получить данные залогиненного админа.
 */
@GetMapping("/users")
public String listUsers(Model model, @AuthenticationPrincipal UserDetails currentUser) {
    // Находим текущего пользователя в БД для исключения его из списка
    User admin = userRepository.findByEmail(currentUser.getUsername())
            .orElseThrow(() -> new RuntimeException("Администратор не найден"));

    // Получаем список всех пользователей, кроме самого текущего админа
    model.addAttribute("users", userRepository.findByIdNot(admin.getId()));
    return "admin_users_list"; // Шаблон отображения таблицы
}

@GetMapping("/events/get")
@ResponseBody // Важно: возвращаем JSON, а не имя шаблона
public List<CalendarEvent> getUserEvents(@RequestParam String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    return calendarEventRepository.findByUser(user);
}

/**
 * Удаление пользователя. Только роль ADMIN.
 */
@GetMapping("/users/delete/{id}")
@PreAuthorize("hasRole('ADMIN')")
public String deleteUser(@PathVariable Long id) {
    userRepository.deleteById(id); // Удаление из базы по ID
    return "redirect:/admin/users"; // Перенаправление обратно на список
}

/**
 * Форма редактирования пользователя.
 */
@GetMapping("/users/edit/{id}")
public String editUserForm(@PathVariable Long id, Model model) {
    // Передаем существующего пользователя и все справочники для выпадающих списков
    model.addAttribute("user", userRepository.findById(id).orElseThrow());
    model.addAttribute("roles", roleRepository.findAll());
    model.addAttribute("qualification_classes", qualificationClassRepository.findAll());
    model.addAttribute("trains", trainRepository.findAll());
    model.addAttribute("professions", professionRepository.findAll());
    return "edit_user";
}

/**
 * Обработка обновления данных пользователя.
 */
@PostMapping("/users/update")
public String updateUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file, Model model) {
    User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

    // Изменение аватар
    String avatarPath = handleAvatarUpload(file, model);
    // Если был успешно загружен новый аватар, обновляем путь
    if (avatarPath != null) existingUser.setAvatar(avatarPath);

    // Обновляем поля из объекта, пришедшего из формы
    existingUser.setFirstName(user.getFirstName());
    existingUser.setSurname(user.getSurname());
    existingUser.setPatronymic(user.getPatronymic());
    existingUser.setPersonnelNumber(user.getPersonnelNumber());
    existingUser.setRole(user.getRole());
    existingUser.setQualificationClass(user.getQualificationClass());
    existingUser.setTrain(user.getTrain());
    existingUser.setProfession(user.getProfession());

    // Если поле пароля заполнено, шифруем его и сохраняем
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    userRepository.save(existingUser); // Сохраняем изменения в БД
    return "redirect:/admin/users";
}

/**
 * Подготовка пустой формы для создания нового пользователя.
 */
@GetMapping("/users/add")
public String addUserForm(Model model) {
    model.addAttribute("user", new User()); // Инициализация нового объекта
    return addErrorModel(model); // Использование хелпера для заполнения списков
}

/**
 * Создание пользователя с проверкой email и шифрованием.
 */
@PostMapping("/users/create")
public String createUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file, Model model) {

    // 1. Проверка на уникальность email
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        model.addAttribute("error", "Пользователь с таким email уже существует!");
        return addErrorModel(model);
    }

    // 2. Добавляем аватар
    String avatarPath = handleAvatarUpload(file, model);
    // Если была ошибка (в модель добавлено сообщение), метод вернул null
    if (model.containsAttribute("error")) return addErrorModel(model);

    // Если путь получен, ставим его пользователю
    if (avatarPath != null) {
        user.setAvatar(avatarPath);
    } else {
        user.setAvatar(DEFAULT_AVATAR);
    }

    // 3. Шифрование и сохранение
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);

    return "redirect:/admin/users";
}

/**
 * Выделенный метод для обработки загрузки аватара.
 *
 * @param file  Файл из запроса.
 * @param model Модель для передачи ошибок в случае проблем.
 * @return Возвращает путь к файлу, если успех, или null, если были ошибки.
 */
private String handleAvatarUpload(MultipartFile file, Model model) {
    // 1. Проверяем, пустой ли файл
    if (file == null || file.isEmpty()) {
        return null; // Ничего не загружаем
    }

    // 2. Валидация размера (5 МБ)
    if (file.getSize() > 5 * 1024 * 1024) {
        model.addAttribute("error", "Файл слишком большой (макс. 5 МБ)!");
        return null;
    }

    // 3. Валидация типа (только изображения)
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
        model.addAttribute("error", "Загрузите корректное изображение!");
        return null;
    }

    try {
        // 4. Генерация уникального имени для предотвращения конфликтов
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // 5. Установка пути к папке
        Path filePath = Paths.get("uploads/avatars/" + fileName);
        // 6. Создание директории, если её нет
        Files.createDirectories(filePath.getParent());
        // 7. Копирование файла
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        // 8. Возврат пути, который будет сохранен в БД
        return "/uploads/avatars/" + fileName;
    } catch (IOException e) {
        model.addAttribute("error", "Ошибка записи файла на диск.");
        return null;
    }
}

/**
 * Метод-хелпер (избавляет от дублирования кода).
 * Заполняет модель справочниками, необходимыми для отображения формы.
 */
private String addErrorModel(Model model) {
    model.addAttribute("roles", roleRepository.findAll());
    model.addAttribute("qualification_classes", qualificationClassRepository.findAll());
    model.addAttribute("trains", trainRepository.findAll());
    model.addAttribute("professions", professionRepository.findAll());
    return "add_user";
}
}