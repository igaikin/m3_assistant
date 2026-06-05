package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Интерфейс для доступа к таблице ролей
public interface RoleRepository extends JpaRepository<Role, Long> {
// Находит роль в БД по названию (например, "ADMIN")
Optional<Role> findByRole(String role);
}