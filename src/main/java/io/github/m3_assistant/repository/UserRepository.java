package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

// Метод для поиска по email (используется при логине)
Optional<User> findByEmail(String email);

// Метод для получения списка всех пользователей, кроме того, чей ID передан
List<User> findByIdNot(Long id);
// Метод deleteById(Long id) доступен по умолчанию из JpaRepository
}