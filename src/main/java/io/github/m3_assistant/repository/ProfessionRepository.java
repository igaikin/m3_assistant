package io.github.m3_assistant.repository;

import io.github.m3_assistant.model.Profession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Интерфейс для доступа к таблице профессий
public interface ProfessionRepository extends JpaRepository<Profession, Long> {
// Находит профессию в БД по названию (например, "DRIVER")
Optional<Profession> findByProfession(String profession);
}