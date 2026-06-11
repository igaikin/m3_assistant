package io.github.m3_assistant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности приложения.
 *
 * @Configuration указывает Spring, что здесь содержатся определения бинов.
 * @EnableWebSecurity включает встроенную защиту Spring Security.
 * @EnableMethodSecurity позволяет использовать @PreAuthorize на методах контроллеров.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
// Указываем Spring, где еще искать настройки
@PropertySource("classpath:application.properties")
@PropertySource(value = "classpath:application-secret.properties", ignoreResourceNotFound = true)
public class SecurityConfig {
@Value("${app.security.remember-me-key}")
private String rememberMeKey;

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(auth -> auth
                    // 1. Публичные ресурсы (доступны всем)
                    .requestMatchers("/", "/index", "/css/**", "/js/**").permitAll()

                    // 2. Управление файлами: удаление и загрузка только для ADMIN/MANAGER
                    .requestMatchers("/files/delete/**").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/files/upload").hasAnyRole("ADMIN", "MANAGER")

                    // 3. Доступ к остальным операциям с файлами: всем авторизованным
                    .requestMatchers("/files/**").authenticated()

                    // 4. Админ-панель
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER")

                    // 5. Все остальные страницы требуют авторизации
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/index") // Указываем свой кастомный путь к странице входа
                    .loginProcessingUrl("/login") // Сюда форма отправляет POST-запрос
                    .defaultSuccessUrl("/home", true) // Куда перенаправить после успешного входа
                    .permitAll()
            )
            // НАСТРОЙКА "ЗАПОМНИТЬ МЕНЯ"
            .rememberMe(remember -> remember
                    .key(rememberMeKey) // Секретный ключ для подписи куки (лучше вынести в properties)
                    .tokenValiditySeconds(2592000) // Время жизни токена (30 дней в секундах)
                    .rememberMeParameter("remember-me") // Имя чекбокса в вашей HTML-форме
            )
            .logout(logout -> logout
                    .logoutUrl("/logout") // URL для отправки POST запроса на выход
                    .logoutSuccessUrl("/index?logout") // После выхода перенаправляем на страницу входа с параметром
                    .invalidateHttpSession(true) // Удалить сессию
                    .deleteCookies("JSESSIONID") // Удалить куки
                    .permitAll()
            );

    return http.build();
}

/**
 * Библиотека для шифрования паролей с помощью алгоритма BCrypt.
 * Используется для сравнения введенного пароля с хэшем из БД.
 */
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
}