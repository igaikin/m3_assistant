package io.github.m3_assistant.service;

import io.github.m3_assistant.model.User;
import io.github.m3_assistant.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Сервис для загрузки данных пользователя из БД в контекст Spring Security.
 * Spring Security использует этот класс для проверки логина и пароля при входе.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

private final UserRepository userRepository;

// Внедряем репозиторий через конструктор (лучшая практика)
public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
}

/**
 * Основной метод, который вызывается при попытке входа.
 *
 * @param email - email пользователя, введенный в форме логина.
 * @return UserDetails - объект, который понимает Spring Security.
 */
@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // 1. Ищем пользователя в БД по email.
    // Если пользователя нет, выбрасываем стандартное исключение Spring.
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + email + " не найден"));
    System.out.println("Загружен пользователь: " + user.getEmail());
    System.out.println("Роль пользователя из БД: " + user.getRole().getRole());
    // 2. Создаем объект User от Spring Security.
    // Важно: Spring Security требует пароль в зашифрованном виде (BCrypt),
    // который уже лежит у нас в базе данных.
    return new org.springframework.security.core.userdetails.User(
            user.getEmail(),          // Логин (в вашем случае email)
            user.getPassword(),       // Зашифрованный пароль из БД
            // 3. Передаем права (роли) пользователя.
            // Префикс "ROLE_" обязателен для работы hasRole() в SecurityConfig.
            // Мы берем роль из вашей сущности user.getRole().getRole()
            Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole())
            )
    );
}
}